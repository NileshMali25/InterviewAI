package com.aiinterview.service;

import com.aiinterview.dto.AdminStatsDto;
import com.aiinterview.dto.UserProfileDto;
import com.aiinterview.model.Interview;
import com.aiinterview.model.Question;
import com.aiinterview.model.User;
import com.aiinterview.repository.InterviewRepository;
import com.aiinterview.repository.QuestionRepository;
import com.aiinterview.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final QuestionRepository questionRepository;

    public AdminService(UserRepository userRepository,
                        InterviewRepository interviewRepository,
                        QuestionRepository questionRepository) {
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
        this.questionRepository = questionRepository;
    }

    public AdminStatsDto getPlatformStats() {
        AdminStatsDto stats = new AdminStatsDto();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalInterviews(interviewRepository.count());
        stats.setCompletedInterviews(interviewRepository.countByStatus("COMPLETED"));
        stats.setTotalQuestions(questionRepository.count());

        List<Interview> completed = interviewRepository.findAll().stream()
                .filter(i -> "COMPLETED".equalsIgnoreCase(i.getStatus()) && i.getOverallScore() != null)
                .collect(Collectors.toList());

        double avgScore = completed.stream().mapToDouble(i -> i.getOverallScore().doubleValue()).average().orElse(0.0);
        stats.setAveragePlatformScore(BigDecimal.valueOf(avgScore).setScale(2, RoundingMode.HALF_UP));

        // Group by technology
        Map<String, Long> interviewsByTech = interviewRepository.findAll().stream()
                .collect(Collectors.groupingBy(Interview::getTechnology, Collectors.counting()));
        stats.setInterviewsByTechnology(interviewsByTech);

        Map<String, Long> questionsByTech = questionRepository.findAll().stream()
                .collect(Collectors.groupingBy(Question::getTechnology, Collectors.counting()));
        stats.setQuestionsByTechnology(questionsByTech);

        return stats;
    }

    public List<UserProfileDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserProfileDto(u.getId(), u.getFullName(), u.getEmail(), u.getRole(), u.getTargetRole(), u.getBio()))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserProfileDto updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (!newRole.startsWith("ROLE_")) {
            newRole = "ROLE_" + newRole;
        }
        user.setRole(newRole);
        User saved = userRepository.save(user);
        return new UserProfileDto(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getRole(), saved.getTargetRole(), saved.getBio());
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }
}
