package com.aiinterview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AnswerSubmitRequest {

    @NotNull(message = "Interview ID is required")
    private Long interviewId;

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotBlank(message = "Answer cannot be blank")
    private String candidateAnswer;

    private Integer timeTakenSeconds = 0;

    public AnswerSubmitRequest() {}

    public Long getInterviewId() { return interviewId; }
    public void setInterviewId(Long interviewId) { this.interviewId = interviewId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getCandidateAnswer() { return candidateAnswer; }
    public void setCandidateAnswer(String candidateAnswer) { this.candidateAnswer = candidateAnswer; }

    public Integer getTimeTakenSeconds() { return timeTakenSeconds; }
    public void setTimeTakenSeconds(Integer timeTakenSeconds) { this.timeTakenSeconds = timeTakenSeconds; }
}
