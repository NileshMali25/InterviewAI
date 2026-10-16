package com.aiinterview.controller;

import com.aiinterview.dto.AnswerSubmitRequest;
import com.aiinterview.dto.EvaluationResultDto;
import com.aiinterview.dto.InterviewDetailDto;
import com.aiinterview.dto.InterviewSetupRequest;
import com.aiinterview.dto.InterviewSummaryDto;
import com.aiinterview.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping("/setup")
    public ResponseEntity<InterviewDetailDto> setupInterview(
            @Valid @RequestBody InterviewSetupRequest request,
            Authentication authentication) {
        InterviewDetailDto dto = interviewService.createInterview(authentication.getName(), request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewDetailDto> getInterview(@PathVariable Long id) {
        InterviewDetailDto dto = interviewService.getInterviewDetails(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/submit-answer")
    public ResponseEntity<EvaluationResultDto> submitAnswer(
            @Valid @RequestBody AnswerSubmitRequest request,
            Authentication authentication) {
        EvaluationResultDto result = interviewService.submitAnswer(authentication.getName(), request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<InterviewDetailDto> completeInterview(
            @PathVariable Long id,
            Authentication authentication) {
        InterviewDetailDto completed = interviewService.completeInterview(id, authentication.getName());
        return ResponseEntity.ok(completed);
    }

    @GetMapping("/my-history")
    public ResponseEntity<List<InterviewSummaryDto>> getMyHistory(Authentication authentication) {
        List<InterviewSummaryDto> history = interviewService.getUserInterviews(authentication.getName());
        return ResponseEntity.ok(history);
    }
}
