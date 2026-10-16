package com.aiinterview.controller;

import com.aiinterview.dto.AnswerSubmitRequest;
import com.aiinterview.dto.EvaluationResultDto;
import com.aiinterview.model.Question;
import com.aiinterview.repository.QuestionRepository;
import com.aiinterview.service.AiEvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final AiEvaluationService aiEvaluationService;
    private final QuestionRepository questionRepository;

    public EvaluationController(AiEvaluationService aiEvaluationService, QuestionRepository questionRepository) {
        this.aiEvaluationService = aiEvaluationService;
        this.questionRepository = questionRepository;
    }

    @PostMapping("/test")
    public ResponseEntity<EvaluationResultDto> testEvaluation(@RequestBody AnswerSubmitRequest request) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + request.getQuestionId()));

        var evaluation = aiEvaluationService.evaluateWithLocalHeuristic(question, request.getCandidateAnswer());
        return ResponseEntity.ok(aiEvaluationService.mapToDto(evaluation));
    }
}
