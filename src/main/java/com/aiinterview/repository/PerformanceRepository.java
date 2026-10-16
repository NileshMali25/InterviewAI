package com.aiinterview.repository;

import com.aiinterview.model.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    Optional<Performance> findByUserId(Long userId);
}
