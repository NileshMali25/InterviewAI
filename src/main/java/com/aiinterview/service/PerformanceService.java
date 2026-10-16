package com.aiinterview.service;

import com.aiinterview.dto.InterviewSummaryDto;
import com.aiinterview.dto.PerformanceReportDto;
import com.aiinterview.model.Interview;
import com.aiinterview.model.Performance;
import com.aiinterview.model.User;
import com.aiinterview.repository.InterviewRepository;
import com.aiinterview.repository.PerformanceRepository;
import com.aiinterview.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;

    public PerformanceService(PerformanceRepository performanceRepository,
                              InterviewRepository interviewRepository,
                              UserRepository userRepository) {
        this.performanceRepository = performanceRepository;
        this.interviewRepository = interviewRepository;
        this.userRepository = userRepository;
    }

    public PerformanceReportDto getUserPerformanceReport(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        Performance perf = performanceRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Performance p = new Performance();
                    p.setUser(user);
                    return performanceRepository.save(p);
                });

        List<Interview> completedList = interviewRepository.findByUserIdAndStatusOrderByStartedAtDesc(user.getId(), "COMPLETED");

        PerformanceReportDto dto = new PerformanceReportDto();
        dto.setUserId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setTotalInterviews(perf.getTotalInterviews());
        dto.setCompletedInterviews(perf.getCompletedInterviews());
        dto.setAverageScore(perf.getAverageScore());
        dto.setHighestScore(perf.getHighestScore());
        dto.setImprovementRate(perf.getImprovementRate());
        dto.setTechnicalAvg(perf.getTechnicalAvg());
        dto.setCommunicationAvg(perf.getCommunicationAvg());

        // Calculate technology breakdown
        Map<String, List<BigDecimal>> scoresByTech = new HashMap<>();
        for (Interview i : completedList) {
            if (i.getOverallScore() != null) {
                scoresByTech.computeIfAbsent(i.getTechnology(), k -> new ArrayList<>()).add(i.getOverallScore());
            }
        }

        Map<String, BigDecimal> proficiencyMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<BigDecimal>> entry : scoresByTech.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0.0);
            proficiencyMap.put(entry.getKey(), BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
        }
        dto.setTechnologyProficiency(proficiencyMap);

        // Recent completed interviews
        dto.setRecentInterviews(completedList.stream().limit(5).map(i -> new InterviewSummaryDto(
                i.getId(),
                i.getInterviewType(),
                i.getTechnology(),
                i.getDifficulty(),
                i.getQuestionCount(),
                i.getStatus(),
                i.getOverallScore(),
                i.getStartedAt(),
                i.getCompletedAt()
        )).collect(Collectors.toList()));

        return dto;
    }

    @Transactional
    public void updateUserPerformance(User user) {
        List<Interview> allInterviews = interviewRepository.findByUserIdOrderByStartedAtDesc(user.getId());
        List<Interview> completedInterviews = allInterviews.stream()
                .filter(i -> "COMPLETED".equalsIgnoreCase(i.getStatus()) && i.getOverallScore() != null)
                .collect(Collectors.toList());

        Performance perf = performanceRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Performance p = new Performance();
                    p.setUser(user);
                    return p;
                });

        perf.setTotalInterviews(allInterviews.size());
        perf.setCompletedInterviews(completedInterviews.size());

        if (!completedInterviews.isEmpty()) {
            double avgOverall = completedInterviews.stream().mapToDouble(i -> i.getOverallScore().doubleValue()).average().orElse(0.0);
            double maxOverall = completedInterviews.stream().mapToDouble(i -> i.getOverallScore().doubleValue()).max().orElse(0.0);
            double avgTech = completedInterviews.stream().filter(i -> i.getTechnicalScore() != null)
                    .mapToDouble(i -> i.getTechnicalScore().doubleValue()).average().orElse(0.0);
            double avgComm = completedInterviews.stream().filter(i -> i.getCommunicationScore() != null)
                    .mapToDouble(i -> i.getCommunicationScore().doubleValue()).average().orElse(0.0);

            perf.setAverageScore(BigDecimal.valueOf(avgOverall).setScale(2, RoundingMode.HALF_UP));
            perf.setHighestScore(BigDecimal.valueOf(maxOverall).setScale(2, RoundingMode.HALF_UP));
            perf.setTechnicalAvg(BigDecimal.valueOf(avgTech).setScale(2, RoundingMode.HALF_UP));
            perf.setCommunicationAvg(BigDecimal.valueOf(avgComm).setScale(2, RoundingMode.HALF_UP));

            // Improvement rate = ((latest - earliest) / earliest) * 100
            if (completedInterviews.size() >= 2) {
                double earliest = completedInterviews.get(completedInterviews.size() - 1).getOverallScore().doubleValue();
                double latest = completedInterviews.get(0).getOverallScore().doubleValue();
                if (earliest > 0) {
                    double rate = ((latest - earliest) / earliest) * 100.0;
                    perf.setImprovementRate(BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP));
                }
            }
        }

        performanceRepository.save(perf);
    }
}
