package com.aiinterview.service;

import com.aiinterview.dto.QuestionDto;
import com.aiinterview.model.Question;
import com.aiinterview.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<QuestionDto> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<QuestionDto> getQuestionsByFilter(String technology, String difficulty) {
        List<Question> questions;
        if (technology != null && !technology.isBlank() && difficulty != null && !difficulty.isBlank()) {
            questions = questionRepository.findByTechnologyIgnoreCaseAndDifficultyIgnoreCase(technology.trim(), difficulty.trim());
        } else if (technology != null && !technology.isBlank()) {
            questions = questionRepository.findByTechnologyIgnoreCase(technology.trim());
        } else {
            questions = questionRepository.findAll();
        }
        return questions.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<Question> getRandomQuestionsForInterview(String technology, String difficulty, int count) {
        List<Question> list = questionRepository.findRandomByTechnologyAndDifficulty(technology, difficulty, count);
        if (list.size() < count) {
            // Fallback to any difficulty in same technology
            list = questionRepository.findRandomByTechnology(technology, count);
        }
        if (list.size() < count) {
            // General fallback
            list = questionRepository.findRandomQuestions(count);
        }
        return list;
    }

    public QuestionDto getQuestionById(Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + id));
        return mapToDto(q);
    }

    public List<String> getAvailableTechnologies() {
        return questionRepository.findDistinctTechnologies();
    }

    @Transactional
    public QuestionDto createQuestion(QuestionDto dto) {
        Question q = new Question();
        q.setTechnology(dto.getTechnology().trim());
        q.setDifficulty(dto.getDifficulty().trim());
        q.setCategory(dto.getCategory() != null ? dto.getCategory().trim() : "Technical");
        q.setTitle(dto.getTitle().trim());
        q.setDescription(dto.getDescription().trim());
        q.setSampleAnswer(dto.getSampleAnswer());
        q.setKeyConcepts(dto.getKeyConcepts());

        Question saved = questionRepository.save(q);
        return mapToDto(saved);
    }

    @Transactional
    public QuestionDto updateQuestion(Long id, QuestionDto dto) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + id));

        q.setTechnology(dto.getTechnology().trim());
        q.setDifficulty(dto.getDifficulty().trim());
        if (dto.getCategory() != null) q.setCategory(dto.getCategory().trim());
        q.setTitle(dto.getTitle().trim());
        q.setDescription(dto.getDescription().trim());
        q.setSampleAnswer(dto.getSampleAnswer());
        q.setKeyConcepts(dto.getKeyConcepts());

        Question updated = questionRepository.save(q);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new IllegalArgumentException("Question not found with id: " + id);
        }
        questionRepository.deleteById(id);
    }

    public QuestionDto mapToDto(Question q) {
        return new QuestionDto(
                q.getId(),
                q.getTechnology(),
                q.getDifficulty(),
                q.getCategory(),
                q.getTitle(),
                q.getDescription(),
                q.getSampleAnswer(),
                q.getKeyConcepts()
        );
    }
}
