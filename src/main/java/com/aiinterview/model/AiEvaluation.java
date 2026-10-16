package com.aiinterview.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_evaluations")
public class AiEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", nullable = false, unique = true)
    @JsonIgnore
    private Answer answer;

    @Column(name = "technical_score", nullable = false, precision = 4, scale = 2)
    private BigDecimal technicalScore;

    @Column(name = "relevance_score", nullable = false, precision = 4, scale = 2)
    private BigDecimal relevanceScore;

    @Column(name = "completeness_score", nullable = false, precision = 4, scale = 2)
    private BigDecimal completenessScore;

    @Column(name = "communication_score", nullable = false, precision = 4, scale = 2)
    private BigDecimal communicationScore;

    @Column(name = "overall_score", nullable = false, precision = 4, scale = 2)
    private BigDecimal overallScore;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String strengths; // JSON or text

    @Column(columnDefinition = "TEXT", nullable = false)
    private String weaknesses; // JSON or text

    @Column(name = "suggested_answer", columnDefinition = "TEXT", nullable = false)
    private String suggestedAnswer;

    @Column(name = "actionable_advice", columnDefinition = "TEXT", nullable = false)
    private String actionableAdvice;

    @Column(name = "model_used", length = 50)
    private String modelUsed = "gemini-1.5-pro";

    @Column(name = "evaluated_at")
    private LocalDateTime evaluatedAt = LocalDateTime.now();

    public AiEvaluation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Answer getAnswer() { return answer; }
    public void setAnswer(Answer answer) { this.answer = answer; }

    public BigDecimal getTechnicalScore() { return technicalScore; }
    public void setTechnicalScore(BigDecimal technicalScore) { this.technicalScore = technicalScore; }

    public BigDecimal getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(BigDecimal relevanceScore) { this.relevanceScore = relevanceScore; }

    public BigDecimal getCompletenessScore() { return completenessScore; }
    public void setCompletenessScore(BigDecimal completenessScore) { this.completenessScore = completenessScore; }

    public BigDecimal getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(BigDecimal communicationScore) { this.communicationScore = communicationScore; }

    public BigDecimal getOverallScore() { return overallScore; }
    public void setOverallScore(BigDecimal overallScore) { this.overallScore = overallScore; }

    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }

    public String getWeaknesses() { return weaknesses; }
    public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }

    public String getSuggestedAnswer() { return suggestedAnswer; }
    public void setSuggestedAnswer(String suggestedAnswer) { this.suggestedAnswer = suggestedAnswer; }

    public String getActionableAdvice() { return actionableAdvice; }
    public void setActionableAdvice(String actionableAdvice) { this.actionableAdvice = actionableAdvice; }

    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }

    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }
}
