package com.aiinterview.repository;

import com.aiinterview.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByTechnologyIgnoreCase(String technology);

    List<Question> findByTechnologyIgnoreCaseAndDifficultyIgnoreCase(String technology, String difficulty);

    @Query(value = "SELECT * FROM questions WHERE LOWER(technology) = LOWER(:technology) AND LOWER(difficulty) = LOWER(:difficulty) ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomByTechnologyAndDifficulty(@Param("technology") String technology, @Param("difficulty") String difficulty, @Param("limit") int limit);

    @Query(value = "SELECT * FROM questions WHERE LOWER(technology) = LOWER(:technology) ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomByTechnology(@Param("technology") String technology, @Param("limit") int limit);

    @Query(value = "SELECT * FROM questions ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("limit") int limit);

    @Query("SELECT DISTINCT q.technology FROM Question q")
    List<String> findDistinctTechnologies();
}
