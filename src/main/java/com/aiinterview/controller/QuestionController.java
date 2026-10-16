package com.aiinterview.controller;

import com.aiinterview.dto.QuestionDto;
import com.aiinterview.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/public")
    public ResponseEntity<List<QuestionDto>> getQuestions(
            @RequestParam(required = false) String technology,
            @RequestParam(required = false) String difficulty) {
        List<QuestionDto> list = questionService.getQuestionsByFilter(technology, difficulty);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/public/technologies")
    public ResponseEntity<List<String>> getTechnologies() {
        return ResponseEntity.ok(questionService.getAvailableTechnologies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }
}
