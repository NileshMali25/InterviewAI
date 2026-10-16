package com.aiinterview.dto;

import jakarta.validation.constraints.NotBlank;

public class QuestionDto {
    private Long id;

    @NotBlank(message = "Technology is required")
    private String technology;

    @NotBlank(message = "Difficulty is required")
    private String difficulty;

    private String category = "Technical";

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String sampleAnswer;
    private String keyConcepts;

    public QuestionDto() {}

    public QuestionDto(Long id, String technology, String difficulty, String category, String title, String description, String sampleAnswer, String keyConcepts) {
        this.id = id;
        this.technology = technology;
        this.difficulty = difficulty;
        this.category = category;
        this.title = title;
        this.description = description;
        this.sampleAnswer = sampleAnswer;
        this.keyConcepts = keyConcepts;
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
}
