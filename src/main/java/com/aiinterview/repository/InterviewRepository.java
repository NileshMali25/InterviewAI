package com.aiinterview.repository;

import com.aiinterview.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByUserIdOrderByStartedAtDesc(Long userId);
    List<Interview> findByUserIdAndStatusOrderByStartedAtDesc(Long userId, String status);
    long countByUserId(Long userId);
    long countByStatus(String status);
}
