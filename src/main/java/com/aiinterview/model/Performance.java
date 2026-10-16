package com.aiinterview.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "performance")
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "total_interviews")
    private Integer totalInterviews = 0;

    @Column(name = "completed_interviews")
    private Integer completedInterviews = 0;

    @Column(name = "average_score", precision = 4, scale = 2)
    private BigDecimal averageScore = BigDecimal.ZERO;

    @Column(name = "highest_score", precision = 4, scale = 2)
    private BigDecimal highestScore = BigDecimal.ZERO;

    @Column(name = "improvement_rate", precision = 5, scale = 2)
    private BigDecimal improvementRate = BigDecimal.ZERO;

    @Column(name = "technical_avg", precision = 4, scale = 2)
    private BigDecimal technicalAvg = BigDecimal.ZERO;

    @Column(name = "communication_avg", precision = 4, scale = 2)
    private BigDecimal communicationAvg = BigDecimal.ZERO;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Performance() {}

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
