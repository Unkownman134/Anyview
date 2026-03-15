package com.anyview.repository;

import com.anyview.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCreatorId(Long creatorId);
    List<Question> findByIsPublicTrue();
    List<Question> findByDifficulty(Integer difficulty);
}
