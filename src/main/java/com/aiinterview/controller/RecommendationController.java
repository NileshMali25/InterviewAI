package com.aiinterview.controller;

import com.aiinterview.model.Recommendation;
import com.aiinterview.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<Recommendation>> getMyRecommendations(Authentication authentication) {
        List<Recommendation> list = recommendationService.getRecommendationsForUser(authentication.getName());
        return ResponseEntity.ok(list);
    }
}
