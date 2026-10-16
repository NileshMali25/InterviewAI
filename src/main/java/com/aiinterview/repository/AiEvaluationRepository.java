package com.aiinterview.repository;

import com.aiinterview.model.AiEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiEvaluationRepository extends JpaRepository<AiEvaluation, Long> {
    Optional<AiEvaluation> findByAnswerId(Long answerId);
}
