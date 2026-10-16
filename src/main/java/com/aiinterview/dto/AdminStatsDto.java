package com.aiinterview.dto;

import java.math.BigDecimal;
import java.util.Map;

public class AdminStatsDto {
    private long totalUsers;
    private long totalInterviews;
    private long completedInterviews;
    private long totalQuestions;
    private BigDecimal averagePlatformScore;
    private Map<String, Long> interviewsByTechnology;
    private Map<String, Long> questionsByTechnology;

    public AdminStatsDto() {}

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalInterviews() { return totalInterviews; }
    public void setTotalInterviews(long totalInterviews) { this.totalInterviews = totalInterviews; }

    public long getCompletedInterviews() { return completedInterviews; }
    public void setCompletedInterviews(long completedInterviews) { this.completedInterviews = completedInterviews; }

    public long getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(long totalQuestions) { this.totalQuestions = totalQuestions; }

    public BigDecimal getAveragePlatformScore() { return averagePlatformScore; }
    public void setAveragePlatformScore(BigDecimal averagePlatformScore) { this.averagePlatformScore = averagePlatformScore; }

    public Map<String, Long> getInterviewsByTechnology() { return interviewsByTechnology; }
    public void setInterviewsByTechnology(Map<String, Long> interviewsByTechnology) { this.interviewsByTechnology = interviewsByTechnology; }

    public Map<String, Long> getQuestionsByTechnology() { return questionsByTechnology; }
    public void setQuestionsByTechnology(Map<String, Long> questionsByTechnology) { this.questionsByTechnology = questionsByTechnology; }
}
