package com.aiinterview.repository;

import com.aiinterview.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByInterviewIdOrderBySubmittedAtAsc(Long interviewId);
    Optional<Answer> findByInterviewIdAndQuestionId(Long interviewId, Long questionId);
    long countByInterviewId(Long interviewId);
}
