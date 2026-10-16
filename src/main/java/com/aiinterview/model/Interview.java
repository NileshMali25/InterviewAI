package com.aiinterview.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "interview_type", nullable = false, length = 50)
    private String interviewType; // Technical, Mock Interview

    @Column(nullable = false, length = 50)
    private String technology;

    @Column(nullable = false, length = 20)
    private String difficulty;

    @Column(name = "question_count", nullable = false)
    private Integer questionCount = 5;

    @Column(nullable = false, length = 30)
    private String status = "IN_PROGRESS"; // IN_PROGRESS, COMPLETED, ABANDONED

    @Column(name = "overall_score", precision = 4, scale = 2)
    private BigDecimal overallScore;

    @Column(name = "technical_score", precision = 4, scale = 2)
    private BigDecimal technicalScore;

    @Column(name = "communication_score", precision = 4, scale = 2)
    private BigDecimal communicationScore;

    @Column(name = "relevance_score", precision = 4, scale = 2)
    private BigDecimal relevanceScore;

    @Column(name = "completeness_score", precision = 4, scale = 2)
    private BigDecimal completenessScore;

    @Column(name = "summary_feedback", columnDefinition = "TEXT")
    private String summaryFeedback;

    @Column(name = "started_at")
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public Interview() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    public BigDecimal getTechnicalScore() { return technicalScore; }
    public void setTechnicalScore(BigDecimal technicalScore) { this.technicalScore = technicalScore; }

    public BigDecimal getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(BigDecimal communicationScore) { this.communicationScore = communicationScore; }

    public BigDecimal getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(BigDecimal relevanceScore) { this.relevanceScore = relevanceScore; }

    public BigDecimal getCompletenessScore() { return completenessScore; }
    public void setCompletenessScore(BigDecimal completenessScore) { this.completenessScore = completenessScore; }

    public String getSummaryFeedback() { return summaryFeedback; }
    public void setSummaryFeedback(String summaryFeedback) { this.summaryFeedback = summaryFeedback; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
