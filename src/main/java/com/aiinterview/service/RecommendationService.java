package com.aiinterview.service;

import com.aiinterview.model.*;
import com.aiinterview.repository.RecommendationRepository;
import com.aiinterview.repository.SkillRepository;
import com.aiinterview.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public RecommendationService(RecommendationRepository recommendationRepository,
                                 SkillRepository skillRepository,
                                 UserRepository userRepository) {
        this.recommendationRepository = recommendationRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    public List<Recommendation> getRecommendationsForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));
        return recommendationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Transactional
    public void generateRecommendationsForUser(User user, Interview interview, List<Answer> answers) {
        // Collect low scoring areas
        for (Answer ans : answers) {
            AiEvaluation eval = ans.getEvaluation();
            if (eval != null && eval.getOverallScore().doubleValue() < 8.0) {
                Question q = ans.getQuestion();

                Recommendation rec = new Recommendation();
                rec.setUser(user);
                rec.setWeakArea(q.getCategory() + " (" + q.getTechnology() + ")");
                rec.setRecommendedTopic(q.getTitle());
                rec.setSuggestedDifficulty(q.getDifficulty());

                // Select curated resource URL
                String url = getVerifiedUrlForTech(q.getTechnology(), q.getTitle());
                if (url != null) {
                    rec.setIsVerifiedResource(true);
                    rec.setResourceUrl(url);
                } else {
                    rec.setIsVerifiedResource(false);
                }

                rec.setReason("Identified score of " + eval.getOverallScore() + "/10. AI advice: " + eval.getActionableAdvice());

                // Find matching skill if available
                Optional<Skill> skill = skillRepository.findByName(q.getTechnology());
                skill.ifPresent(rec::setSkill);

                recommendationRepository.save(rec);
            }
        }
    }

    private String getVerifiedUrlForTech(String technology, String topic) {
        if ("Java".equalsIgnoreCase(technology)) {
            return "https://docs.oracle.com/en/java/javase/17/docs/api/";
        } else if ("Python".equalsIgnoreCase(technology)) {
            return "https://docs.python.org/3/tutorial/";
        } else if ("C++".equalsIgnoreCase(technology)) {
            return "https://en.cppreference.com/w/cpp";
        } else if ("JavaScript".equalsIgnoreCase(technology)) {
            return "https://developer.mozilla.org/en-US/docs/Web/JavaScript";
        } else if ("SQL".equalsIgnoreCase(technology)) {
            return "https://dev.mysql.com/doc/refman/8.0/en/";
        } else if ("Data Structures and Algorithms".equalsIgnoreCase(technology) || "DSA".equalsIgnoreCase(technology)) {
            return "https://neetcode.io/roadmap";
        } else if ("Web Development".equalsIgnoreCase(technology)) {
            return "https://developer.mozilla.org/en-US/docs/Learn";
        } else if ("Data Science".equalsIgnoreCase(technology)) {
            return "https://scikit-learn.org/stable/";
        } else if ("AI Developer".equalsIgnoreCase(technology)) {
            return "https://huggingface.co/docs/transformers/index";
        } else if ("Machine Learning".equalsIgnoreCase(technology)) {
            return "https://developers.google.com/machine-learning/crash-course";
        } else if ("DevOps".equalsIgnoreCase(technology)) {
            return "https://kubernetes.io/docs/home/";
        }
        return "https://github.com";
    }
}
