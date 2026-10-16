package com.aiinterview.service;

import com.aiinterview.dto.AnswerSubmitRequest;
import com.aiinterview.dto.EvaluationResultDto;
import com.aiinterview.dto.InterviewDetailDto;
import com.aiinterview.dto.InterviewSetupRequest;
import com.aiinterview.dto.InterviewSummaryDto;
import com.aiinterview.model.*;
import com.aiinterview.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final AiEvaluationService aiEvaluationService;
    private final QuestionService questionService;
    private final PerformanceService performanceService;
    private final RecommendationService recommendationService;

    public InterviewService(InterviewRepository interviewRepository,
                            QuestionRepository questionRepository,
                            AnswerRepository answerRepository,
                            UserRepository userRepository,
                            AiEvaluationService aiEvaluationService,
                            QuestionService questionService,
                            PerformanceService performanceService,
                            RecommendationService recommendationService) {
        this.interviewRepository = interviewRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.aiEvaluationService = aiEvaluationService;
        this.questionService = questionService;
        this.performanceService = performanceService;
        this.recommendationService = recommendationService;
    }

    @Transactional
    public InterviewDetailDto createInterview(String userEmail, InterviewSetupRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        Interview interview = new Interview();
        interview.setUser(user);
        interview.setInterviewType(request.getInterviewType());
        interview.setTechnology(request.getTechnology());
        interview.setDifficulty(request.getDifficulty());
        interview.setQuestionCount(request.getQuestionCount());
        interview.setStatus("IN_PROGRESS");
        interview.setStartedAt(LocalDateTime.now());

        Interview saved = interviewRepository.save(interview);

        // Fetch random questions for this interview
        List<Question> selectedQuestions = questionService.getRandomQuestionsForInterview(
                request.getTechnology(), request.getDifficulty(), request.getQuestionCount()
        );

        InterviewDetailDto dto = mapToDetailDto(saved);
        dto.setQuestions(selectedQuestions.stream().map(questionService::mapToDto).collect(Collectors.toList()));
        dto.setEvaluations(new ArrayList<>());
        return dto;
    }

    public InterviewDetailDto getInterviewDetails(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new IllegalArgumentException("Interview not found with id: " + interviewId));

        InterviewDetailDto dto = mapToDetailDto(interview);

        List<Answer> answers = answerRepository.findByInterviewIdOrderBySubmittedAtAsc(interviewId);
        List<EvaluationResultDto> evals = new ArrayList<>();
        List<Question> questions = new ArrayList<>();

        for (Answer ans : answers) {
            questions.add(ans.getQuestion());
            if (ans.getEvaluation() != null) {
                evals.add(aiEvaluationService.mapToDto(ans.getEvaluation()));
            }
        }

        // If interview is in progress, load remaining questions for technology
        if (questions.size() < interview.getQuestionCount()) {
            List<Question> moreQuestions = questionService.getRandomQuestionsForInterview(
                    interview.getTechnology(), interview.getDifficulty(), interview.getQuestionCount()
            );
            for (Question mq : moreQuestions) {
                if (questions.stream().noneMatch(q -> q.getId().equals(mq.getId()))) {
                    questions.add(mq);
                    if (questions.size() >= interview.getQuestionCount()) break;
                }
            }
        }

        dto.setQuestions(questions.stream().map(questionService::mapToDto).collect(Collectors.toList()));
        dto.setEvaluations(evals);
        return dto;
    }

    @Transactional
    public EvaluationResultDto submitAnswer(String userEmail, AnswerSubmitRequest request) {
        Interview interview = interviewRepository.findById(request.getInterviewId())
                .orElseThrow(() -> new IllegalArgumentException("Interview not found: " + request.getInterviewId()));

        if (!interview.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new SecurityException("Unauthorized access to interview");
        }

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + request.getQuestionId()));

        Answer answer = answerRepository.findByInterviewIdAndQuestionId(request.getInterviewId(), request.getQuestionId())
                .orElse(new Answer());

        answer.setInterview(interview);
        answer.setQuestion(question);
        answer.setCandidateAnswer(request.getCandidateAnswer());
        answer.setTimeTakenSeconds(request.getTimeTakenSeconds());
        answer.setSubmittedAt(LocalDateTime.now());

        Answer savedAnswer = answerRepository.save(answer);

        // Perform AI evaluation
        AiEvaluation evaluation = aiEvaluationService.evaluateAnswer(savedAnswer);
        savedAnswer.setEvaluation(evaluation);

        return aiEvaluationService.mapToDto(evaluation);
    }

    @Transactional
    public InterviewDetailDto completeInterview(Long interviewId, String userEmail) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new IllegalArgumentException("Interview not found: " + interviewId));

        if (!interview.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new SecurityException("Unauthorized access to interview");
        }

        List<Answer> answers = answerRepository.findByInterviewIdOrderBySubmittedAtAsc(interviewId);

        if (answers.isEmpty()) {
            throw new IllegalStateException("Cannot complete interview without answers");
        }

        // Compute averages
        double totalOverall = 0;
        double totalTech = 0;
        double totalComm = 0;
        double totalRel = 0;
        double totalComp = 0;
        int evaluatedCount = 0;

        for (Answer ans : answers) {
            if (ans.getEvaluation() != null) {
                totalOverall += ans.getEvaluation().getOverallScore().doubleValue();
                totalTech += ans.getEvaluation().getTechnicalScore().doubleValue();
                totalComm += ans.getEvaluation().getCommunicationScore().doubleValue();
                totalRel += ans.getEvaluation().getRelevanceScore().doubleValue();
                totalComp += ans.getEvaluation().getCompletenessScore().doubleValue();
                evaluatedCount++;
            }
        }

        if (evaluatedCount > 0) {
            interview.setOverallScore(BigDecimal.valueOf(totalOverall / evaluatedCount).setScale(2, RoundingMode.HALF_UP));
            interview.setTechnicalScore(BigDecimal.valueOf(totalTech / evaluatedCount).setScale(2, RoundingMode.HALF_UP));
            interview.setCommunicationScore(BigDecimal.valueOf(totalComm / evaluatedCount).setScale(2, RoundingMode.HALF_UP));
            interview.setRelevanceScore(BigDecimal.valueOf(totalRel / evaluatedCount).setScale(2, RoundingMode.HALF_UP));
            interview.setCompletenessScore(BigDecimal.valueOf(totalComp / evaluatedCount).setScale(2, RoundingMode.HALF_UP));

            double avgScore = interview.getOverallScore().doubleValue();
            if (avgScore >= 8.5) {
                interview.setSummaryFeedback("Exceptional performance. Strong technical precision, well-articulated conceptual depth, and crisp communication.");
            } else if (avgScore >= 7.0) {
                interview.setSummaryFeedback("Good performance. Solid foundational knowledge demonstrated with minor omissions in advanced edge cases.");
            } else if (avgScore >= 5.0) {
                interview.setSummaryFeedback("Average performance. Basic concepts are understood but answers lack technical depth, examples, and clarity.");
            } else {
                interview.setSummaryFeedback("Needs improvement. Significant gaps identified in foundational topics. Thorough review recommended.");
            }
        }

        interview.setStatus("COMPLETED");
        interview.setCompletedAt(LocalDateTime.now());
        Interview completed = interviewRepository.save(interview);

        // Update overall performance metrics
        performanceService.updateUserPerformance(interview.getUser());

        // Generate tailored skill recommendations
        recommendationService.generateRecommendationsForUser(interview.getUser(), completed, answers);

        return getInterviewDetails(completed.getId());
    }

    public List<InterviewSummaryDto> getUserInterviews(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        return interviewRepository.findByUserIdOrderByStartedAtDesc(user.getId()).stream()
                .map(i -> new InterviewSummaryDto(
                        i.getId(),
                        i.getInterviewType(),
                        i.getTechnology(),
                        i.getDifficulty(),
                        i.getQuestionCount(),
                        i.getStatus(),
                        i.getOverallScore(),
                        i.getStartedAt(),
                        i.getCompletedAt()
                ))
                .collect(Collectors.toList());
    }

    private InterviewDetailDto mapToDetailDto(Interview i) {
        InterviewDetailDto dto = new InterviewDetailDto();
        dto.setId(i.getId());
        dto.setUserId(i.getUser().getId());
        dto.setInterviewType(i.getInterviewType());
        dto.setTechnology(i.getTechnology());
        dto.setDifficulty(i.getDifficulty());
        dto.setQuestionCount(i.getQuestionCount());
        dto.setStatus(i.getStatus());
        dto.setOverallScore(i.getOverallScore());
        dto.setTechnicalScore(i.getTechnicalScore());
        dto.setCommunicationScore(i.getCommunicationScore());
        dto.setRelevanceScore(i.getRelevanceScore());
        dto.setCompletenessScore(i.getCompletenessScore());
        dto.setSummaryFeedback(i.getSummaryFeedback());
        dto.setStartedAt(i.getStartedAt());
        dto.setCompletedAt(i.getCompletedAt());
        return dto;
    }
}
