package com.aiinterview.controller;

import com.aiinterview.dto.PerformanceReportDto;
import com.aiinterview.service.PerformanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @GetMapping("/me")
    public ResponseEntity<PerformanceReportDto> getMyPerformance(Authentication authentication) {
        PerformanceReportDto report = performanceService.getUserPerformanceReport(authentication.getName());
        return ResponseEntity.ok(report);
    }
}
