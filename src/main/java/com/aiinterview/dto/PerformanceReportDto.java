package com.aiinterview.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PerformanceReportDto {
    private Long userId;
    private String fullName;
    private Integer totalInterviews;
    private Integer completedInterviews;
    private BigDecimal averageScore;
    private BigDecimal highestScore;
    private BigDecimal improvementRate;
    private BigDecimal technicalAvg;
    private BigDecimal communicationAvg;
    private Map<String, BigDecimal> technologyProficiency;
    private List<InterviewSummaryDto> recentInterviews;

    public PerformanceReportDto() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Integer getTotalInterviews() { return totalInterviews; }
    public void setTotalInterviews(Integer totalInterviews) { this.totalInterviews = totalInterviews; }

    public Integer getCompletedInterviews() { return completedInterviews; }
    public void setCompletedInterviews(Integer completedInterviews) { this.completedInterviews = completedInterviews; }

    public BigDecimal getAverageScore() { return averageScore; }
    public void setAverageScore(BigDecimal averageScore) { this.averageScore = averageScore; }

    public BigDecimal getHighestScore() { return highestScore; }
    public void setHighestScore(BigDecimal highestScore) { this.highestScore = highestScore; }

    public BigDecimal getImprovementRate() { return improvementRate; }
    public void setImprovementRate(BigDecimal improvementRate) { this.improvementRate = improvementRate; }

    public BigDecimal getTechnicalAvg() { return technicalAvg; }
    public void setTechnicalAvg(BigDecimal technicalAvg) { this.technicalAvg = technicalAvg; }

    public BigDecimal getCommunicationAvg() { return communicationAvg; }
    public void setCommunicationAvg(BigDecimal communicationAvg) { this.communicationAvg = communicationAvg; }

    public Map<String, BigDecimal> getTechnologyProficiency() { return technologyProficiency; }
    public void setTechnologyProficiency(Map<String, BigDecimal> technologyProficiency) { this.technologyProficiency = technologyProficiency; }

    public List<InterviewSummaryDto> getRecentInterviews() { return recentInterviews; }
    public void setRecentInterviews(List<InterviewSummaryDto> recentInterviews) { this.recentInterviews = recentInterviews; }
}
