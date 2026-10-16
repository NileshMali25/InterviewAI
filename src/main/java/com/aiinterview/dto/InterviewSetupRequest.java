package com.aiinterview.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InterviewSetupRequest {

    @NotBlank(message = "Interview type is required")
    private String interviewType; // Technical or Mock Interview

    @NotBlank(message = "Technology is required")
    private String technology; // Java, Python, C++, JavaScript, SQL, Data Structures and Algorithms, Web Development

    @NotBlank(message = "Difficulty is required")
    private String difficulty; // Easy, Medium, Hard

    @NotNull(message = "Question count is required")
    @Min(value = 1, message = "Minimum 1 question")
    @Max(value = 10, message = "Maximum 10 questions")
    private Integer questionCount = 3;

    public InterviewSetupRequest() {}

    public String getInterviewType() { return interviewType; }
    public void setInterviewType(String interviewType) { this.interviewType = interviewType; }

    public String getTechnology() { return technology; }
    public void setTechnology(String technology) { this.technology = technology; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
}
