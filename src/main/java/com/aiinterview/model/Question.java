package com.aiinterview.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String technology; // Java, Python, C++, JavaScript, SQL, DSA, Web Development

    @Column(nullable = false, length = 20)
    private String difficulty; // Easy, Medium, Hard

    @Column(length = 50)
    private String category = "Technical";

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "sample_answer", columnDefinition = "TEXT")
    private String sampleAnswer;

    @Column(name = "key_concepts", columnDefinition = "TEXT")
    private String keyConcepts;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Question() {}

    public Question(Long id, String technology, String difficulty, String category, String title, String description, String sampleAnswer, String keyConcepts) {
        this.id = id;
        this.technology = technology;
        this.difficulty = difficulty;
        this.category = category;
        this.title = title;
        this.description = description;
        this.sampleAnswer = sampleAnswer;
        this.keyConcepts = keyConcepts;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTechnology() { return technology; }
    public void setTechnology(String technology) { this.technology = technology; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSampleAnswer() { return sampleAnswer; }
    public void setSampleAnswer(String sampleAnswer) { this.sampleAnswer = sampleAnswer; }

    public String getKeyConcepts() { return keyConcepts; }
    public void setKeyConcepts(String keyConcepts) { this.keyConcepts = keyConcepts; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
