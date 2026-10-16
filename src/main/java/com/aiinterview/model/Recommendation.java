package com.aiinterview.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(name = "weak_area", nullable = false, length = 150)
    private String weakArea;

    @Column(name = "recommended_topic", nullable = false, length = 150)
    private String recommendedTopic;

    @Column(name = "suggested_difficulty", length = 20)
    private String suggestedDifficulty = "Medium";

    @Column(name = "is_verified_resource")
    private Boolean isVerifiedResource = false;

    @Column(name = "resource_url", length = 255)
    private String resourceUrl;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Recommendation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public String getWeakArea() { return weakArea; }
    public void setWeakArea(String weakArea) { this.weakArea = weakArea; }

    public String getRecommendedTopic() { return recommendedTopic; }
    public void setRecommendedTopic(String recommendedTopic) { this.recommendedTopic = recommendedTopic; }

    public String getSuggestedDifficulty() { return suggestedDifficulty; }
    public void setSuggestedDifficulty(String suggestedDifficulty) { this.suggestedDifficulty = suggestedDifficulty; }

    public Boolean getIsVerifiedResource() { return isVerifiedResource; }
    public void setIsVerifiedResource(Boolean isVerifiedResource) { this.isVerifiedResource = isVerifiedResource; }

    public String getResourceUrl() { return resourceUrl; }
    public void setResourceUrl(String resourceUrl) { this.resourceUrl = resourceUrl; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
