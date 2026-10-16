package com.aiinterview.service;

import com.aiinterview.dto.EvaluationResultDto;
import com.aiinterview.model.AiEvaluation;
import com.aiinterview.model.Answer;
import com.aiinterview.model.Question;
import com.aiinterview.repository.AiEvaluationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AiEvaluationService {

    private static final Logger logger = LoggerFactory.getLogger(AiEvaluationService.class);

    private final AiEvaluationRepository evaluationRepository;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${ai.provider:gemini}")
    private String aiProvider;

    @Value("${ai.gemini.api-key:}")
    private String geminiApiKey;

    @Value("${ai.gemini.model:gemini-1.5-pro}")
    private String geminiModel;

    @Value("${ai.openai.api-key:}")
    private String openaiApiKey;

    @Value("${ai.openai.model:gpt-4o-mini}")
    private String openaiModel;

    @Value("${ai.fallback-enabled:true}")
    private boolean fallbackEnabled;

    public AiEvaluationService(AiEvaluationRepository evaluationRepository, ObjectMapper objectMapper) {
        this.evaluationRepository = evaluationRepository;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    @Transactional
    public AiEvaluation evaluateAnswer(Answer answer) {
        Question question = answer.getQuestion();
        String candidateAnswer = answer.getCandidateAnswer();

        AiEvaluation evaluation = null;

        // Try Live AI API if key is configured
        if ("gemini".equalsIgnoreCase(aiProvider) && geminiApiKey != null && !geminiApiKey.isBlank()) {
            try {
                evaluation = callGeminiApi(question, candidateAnswer);
            } catch (Exception e) {
                logger.error("Gemini API call failed, falling back to heuristic engine: {}", e.getMessage());
            }
        } else if ("openai".equalsIgnoreCase(aiProvider) && openaiApiKey != null && !openaiApiKey.isBlank()) {
            try {
                evaluation = callOpenAiApi(question, candidateAnswer);
            } catch (Exception e) {
                logger.error("OpenAI API call failed, falling back to heuristic engine: {}", e.getMessage());
            }
        }

        // Graceful intelligent heuristic evaluation fallback
        if (evaluation == null) {
            evaluation = evaluateWithLocalHeuristic(question, candidateAnswer);
        }

        evaluation.setAnswer(answer);
        evaluation.setEvaluatedAt(LocalDateTime.now());

        // Check if an existing evaluation exists for this answer
        Optional<AiEvaluation> existing = evaluationRepository.findByAnswerId(answer.getId());
        if (existing.isPresent()) {
            AiEvaluation curr = existing.get();
            curr.setTechnicalScore(evaluation.getTechnicalScore());
            curr.setRelevanceScore(evaluation.getRelevanceScore());
            curr.setCompletenessScore(evaluation.getCompletenessScore());
            curr.setCommunicationScore(evaluation.getCommunicationScore());
            curr.setOverallScore(evaluation.getOverallScore());
            curr.setStrengths(evaluation.getStrengths());
            curr.setWeaknesses(evaluation.getWeaknesses());
            curr.setSuggestedAnswer(evaluation.getSuggestedAnswer());
            curr.setActionableAdvice(evaluation.getActionableAdvice());
            curr.setModelUsed(evaluation.getModelUsed());
            curr.setEvaluatedAt(LocalDateTime.now());
            return evaluationRepository.save(curr);
        }

        return evaluationRepository.save(evaluation);
    }

    /**
     * Call Google Gemini API with strict JSON schema instructions
     */
    private AiEvaluation callGeminiApi(Question question, String candidateAnswer) throws Exception {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + geminiModel + ":generateContent?key=" + geminiApiKey;

        String prompt = buildPrompt(question, candidateAnswer);

        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> body = Map.of(
                "contents", List.of(content),
                "generationConfig", Map.of(
                        "response_mime_type", "application/json",
                        "temperature", 0.2
                )
        );

        String jsonPayload = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            JsonNode root = objectMapper.readTree(response.body());
            String responseText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            return parseAiJsonResponse(responseText, "gemini-1.5-pro");
        } else {
            throw new RuntimeException("Gemini returned HTTP " + response.statusCode() + ": " + response.body());
        }
    }

    /**
     * Call OpenAI Chat Completion API
     */
    private AiEvaluation callOpenAiApi(Question question, String candidateAnswer) throws Exception {
        String url = "https://api.openai.com/v1/chat/completions";
        String prompt = buildPrompt(question, candidateAnswer);

        Map<String, Object> message = Map.of("role", "user", "content", prompt);
        Map<String, Object> body = Map.of(
                "model", openaiModel,
                "messages", List.of(message),
                "temperature", 0.2,
                "response_format", Map.of("type", "json_object")
        );

        String jsonPayload = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + openaiApiKey)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            JsonNode root = objectMapper.readTree(response.body());
            String contentText = root.path("choices").get(0).path("message").path("content").asText();
            return parseAiJsonResponse(contentText, openaiModel);
        } else {
            throw new RuntimeException("OpenAI returned HTTP " + response.statusCode() + ": " + response.body());
        }
    }

    private String buildPrompt(Question question, String candidateAnswer) {
        return "You are an expert technical interviewer and hiring committee evaluator. "
                + "Analyze the candidate's answer for the following question strictly based on technical correctness, completeness, relevance, and communication.\n\n"
                + "QUESTION: " + question.getTitle() + "\n"
                + "DETAILS: " + question.getDescription() + "\n"
                + "KEY CONCEPTS EXPECTED: " + (question.getKeyConcepts() != null ? question.getKeyConcepts() : "Core architectural and coding principles") + "\n"
                + "SAMPLE BENCHMARK ANSWER: " + (question.getSampleAnswer() != null ? question.getSampleAnswer() : "N/A") + "\n\n"
                + "CANDIDATE'S ANSWER:\n\"\"\"" + candidateAnswer + "\"\"\"\n\n"
                + "Provide an objective, constructive evaluation formatted STRICTLY as a single JSON object with the following schema:\n"
                + "{\n"
                + "  \"technicalScore\": <number between 1.0 and 10.0>,\n"
                + "  \"relevanceScore\": <number between 1.0 and 10.0>,\n"
                + "  \"completenessScore\": <number between 1.0 and 10.0>,\n"
                + "  \"communicationScore\": <number between 1.0 and 10.0>,\n"
                + "  \"overallScore\": <weighted average between 1.0 and 10.0>,\n"
                + "  \"strengths\": [\"point 1\", \"point 2\"],\n"
                + "  \"weaknesses\": [\"point 1\", \"point 2\"],\n"
                + "  \"suggestedAnswer\": \"<clear, exemplary interview response>\",\n"
                + "  \"actionableAdvice\": \"<concise advice on what the student should study next>\"\n"
                + "}";
    }

    private AiEvaluation parseAiJsonResponse(String json, String model) {
        try {
            JsonNode node = objectMapper.readTree(json);
            AiEvaluation eval = new AiEvaluation();
            eval.setTechnicalScore(BigDecimal.valueOf(node.path("technicalScore").asDouble(7.0)).setScale(2, RoundingMode.HALF_UP));
            eval.setRelevanceScore(BigDecimal.valueOf(node.path("relevanceScore").asDouble(7.0)).setScale(2, RoundingMode.HALF_UP));
            eval.setCompletenessScore(BigDecimal.valueOf(node.path("completenessScore").asDouble(7.0)).setScale(2, RoundingMode.HALF_UP));
            eval.setCommunicationScore(BigDecimal.valueOf(node.path("communicationScore").asDouble(7.0)).setScale(2, RoundingMode.HALF_UP));
            eval.setOverallScore(BigDecimal.valueOf(node.path("overallScore").asDouble(7.0)).setScale(2, RoundingMode.HALF_UP));

            eval.setStrengths(node.path("strengths").isMissingNode() ? "[\"Good effort addressing the core question\"]" : node.path("strengths").toString());
            eval.setWeaknesses(node.path("weaknesses").isMissingNode() ? "[\"Could elaborate with concrete code examples\"]" : node.path("weaknesses").toString());
            eval.setSuggestedAnswer(node.path("suggestedAnswer").asText("A comprehensive answer should address key concepts and trade-offs directly."));
            eval.setActionableAdvice(node.path("actionableAdvice").asText("Review core foundational concepts and practice articulating technical edge cases."));
            eval.setModelUsed(model);
            return eval;
        } catch (Exception e) {
            logger.error("Failed to parse AI JSON response: {}", json, e);
            throw new RuntimeException("Malformed AI response format");
        }
    }

    /**
     * Intelligent local heuristic evaluator based on rubric NLP analysis.
     * Guaranteed zero-failure offline mode for testing and grading without API billing.
     */
    public AiEvaluation evaluateWithLocalHeuristic(Question question, String candidateAnswer) {
        AiEvaluation eval = new AiEvaluation();
        String ans = candidateAnswer != null ? candidateAnswer.trim() : "";
        int wordCount = ans.isEmpty() ? 0 : ans.split("\\s+").length;

        // Extract key concepts from question
        List<String> keyConcepts = new ArrayList<>();
        if (question.getKeyConcepts() != null && !question.getKeyConcepts().isBlank()) {
            String[] tokens = question.getKeyConcepts().split("[,;\\n]+");
            for (String t : tokens) {
                if (!t.trim().isEmpty()) {
                    keyConcepts.add(t.trim().toLowerCase());
                }
            }
        }

        // Match concepts present in answer
        int matchedConcepts = 0;
        List<String> matchedList = new ArrayList<>();
        List<String> missedList = new ArrayList<>();
        String lowerAns = ans.toLowerCase();

        for (String concept : keyConcepts) {
            if (lowerAns.contains(concept)) {
                matchedConcepts++;
                matchedList.add(concept);
            } else {
                missedList.add(concept);
            }
        }

        double conceptRatio = keyConcepts.isEmpty() ? 0.7 : ((double) matchedConcepts / keyConcepts.size());

        // Technical score (based on concept coverage and technical depth)
        double techScore = 3.0 + (conceptRatio * 5.5);
        if (wordCount < 15) techScore = Math.min(techScore, 3.5);
        else if (wordCount > 60 && conceptRatio >= 0.5) techScore = Math.min(10.0, techScore + 1.2);

        // Relevance score (checks title/category keywords)
        double relScore = 4.0;
        String[] titleWords = question.getTitle().toLowerCase().split("\\W+");
        int titleMatches = 0;
        for (String tw : titleWords) {
            if (tw.length() > 3 && lowerAns.contains(tw)) titleMatches++;
        }
        relScore += Math.min(5.0, titleMatches * 1.5);
        if (wordCount > 25) relScore = Math.min(10.0, relScore + 1.0);

        // Completeness score
        double compScore = 3.0;
        if (wordCount > 100) compScore += 5.0;
        else if (wordCount > 50) compScore += 3.5;
        else if (wordCount > 25) compScore += 2.0;
        compScore += (conceptRatio * 2.0);
        compScore = Math.min(10.0, compScore);

        // Communication score (structure, formatting, punctuation, capitalization)
        double commScore = 4.5;
        if (ans.contains(".") && ans.length() > 50) commScore += 2.0;
        if (ans.contains("\n") || ans.contains(":") || ans.contains("-")) commScore += 1.5;
        if (wordCount >= 30 && wordCount <= 250) commScore += 1.5;
        commScore = Math.min(9.5, commScore);

        // Weighted Overall Score
        double overall = (techScore * 0.40) + (relScore * 0.20) + (compScore * 0.25) + (commScore * 0.15);
        overall = Math.round(overall * 10.0) / 10.0;

        eval.setTechnicalScore(BigDecimal.valueOf(Math.min(10.0, Math.max(1.0, techScore))).setScale(2, RoundingMode.HALF_UP));
        eval.setRelevanceScore(BigDecimal.valueOf(Math.min(10.0, Math.max(1.0, relScore))).setScale(2, RoundingMode.HALF_UP));
        eval.setCompletenessScore(BigDecimal.valueOf(Math.min(10.0, Math.max(1.0, compScore))).setScale(2, RoundingMode.HALF_UP));
        eval.setCommunicationScore(BigDecimal.valueOf(Math.min(10.0, Math.max(1.0, commScore))).setScale(2, RoundingMode.HALF_UP));
        eval.setOverallScore(BigDecimal.valueOf(Math.min(10.0, Math.max(1.0, overall))).setScale(2, RoundingMode.HALF_UP));

        // Strengths & Weaknesses
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();

        if (!matchedList.isEmpty()) {
            strengths.add("Accurately mentioned core concepts: " + String.join(", ", matchedList.subList(0, Math.min(3, matchedList.size()))));
        } else {
            strengths.add("Provided a direct attempt at framing the answer");
        }
        if (wordCount >= 40) {
            strengths.add("Provided adequate detail and explanation length (" + wordCount + " words)");
        }
        if (commScore >= 7.0) {
            strengths.add("Structured explanation clearly with readable sentence transitions");
        }

        if (!missedList.isEmpty()) {
            weaknesses.add("Missed discussing critical key terms: " + String.join(", ", missedList.subList(0, Math.min(3, missedList.size()))));
        }
        if (wordCount < 40) {
            weaknesses.add("Answer was brief. Expand with concrete architecture details, time complexity, or code snippets");
        }
        if (compScore < 6.5) {
            weaknesses.add("Elaborate on real-world edge cases and trade-offs to demonstrate senior-level depth");
        }

        try {
            eval.setStrengths(objectMapper.writeValueAsString(strengths));
            eval.setWeaknesses(objectMapper.writeValueAsString(weaknesses));
        } catch (Exception e) {
            eval.setStrengths("[\"Attempted question\"]");
            eval.setWeaknesses("[\"Review key principles\"]");
        }

        eval.setSuggestedAnswer(question.getSampleAnswer() != null ? question.getSampleAnswer()
                : "A strong response should clearly define the concept, explain how it operates internally, highlight edge cases, and discuss real-world trade-offs.");
        eval.setActionableAdvice("Practice articulating '" + question.getTitle() + "' focusing on underlying memory behavior and performance trade-offs.");
        eval.setModelUsed("heuristic-semantic-engine-v1");

        return eval;
    }

    public EvaluationResultDto mapToDto(AiEvaluation eval) {
        EvaluationResultDto dto = new EvaluationResultDto();
        dto.setEvaluationId(eval.getId());
        if (eval.getAnswer() != null) {
            dto.setAnswerId(eval.getAnswer().getId());
            if (eval.getAnswer().getQuestion() != null) {
                dto.setQuestionId(eval.getAnswer().getQuestion().getId());
                dto.setQuestionTitle(eval.getAnswer().getQuestion().getTitle());
            }
        }
        dto.setTechnicalScore(eval.getTechnicalScore());
        dto.setRelevanceScore(eval.getRelevanceScore());
        dto.setCompletenessScore(eval.getCompletenessScore());
        dto.setCommunicationScore(eval.getCommunicationScore());
        dto.setOverallScore(eval.getOverallScore());
        dto.setSuggestedAnswer(eval.getSuggestedAnswer());
        dto.setActionableAdvice(eval.getActionableAdvice());
        dto.setModelUsed(eval.getModelUsed());

        try {
            List<String> strList = objectMapper.readValue(eval.getStrengths(), new TypeReference<List<String>>() {});
            dto.setStrengths(strList);
        } catch (Exception e) {
            dto.setStrengths(Collections.singletonList(eval.getStrengths()));
        }

        try {
            List<String> weakList = objectMapper.readValue(eval.getWeaknesses(), new TypeReference<List<String>>() {});
            dto.setWeaknesses(weakList);
        } catch (Exception e) {
            dto.setWeaknesses(Collections.singletonList(eval.getWeaknesses()));
        }

        return dto;
    }
}
