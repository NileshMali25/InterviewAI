package com.aiinterview.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InterviewSummaryDto {
    private Long id;
    private String interviewType;
    private String technology;
    private String difficulty;
    private Integer questionCount;
    private String status;
    private BigDecimal overallScore;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public InterviewSummaryDto() {}

    public InterviewSummaryDto(Long id, String interviewType, String technology, String difficulty, Integer questionCount, String status, BigDecimal overallScore, LocalDateTime startedAt, LocalDateTime completedAt) {
        this.id = id;
        this.interviewType = interviewType;
        this.technology = technology;
        this.difficulty = difficulty;
        this.questionCount = questionCount;
        this.status = status;
        this.overallScore = overallScore;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInterviewType() { return interviewType; }
    public void setInterviewType(String interviewType) { this.interviewType = interviewType; }

    public String getTechnology() { return technology; }
    public void setTechnology(String technology) { this.technology = technology; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getOverallScore() { return overallScore; }
    public void setOverallScore(BigDecimal overallScore) { this.overallScore = overallScore; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
