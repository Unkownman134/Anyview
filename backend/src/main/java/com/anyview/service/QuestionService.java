package com.anyview.service;

import com.anyview.entity.Question;
import com.anyview.entity.User;
import com.anyview.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    public List<Question> getQuestionsByCreator(Long creatorId) {
        return questionRepository.findByCreatorId(creatorId);
    }

    public List<Question> getPublicQuestions() {
        return questionRepository.findByIsPublicTrue();
    }

    public List<Question> getQuestionsByDifficulty(Integer difficulty) {
        return questionRepository.findByDifficulty(difficulty);
    }

    public Question updateQuestion(Question question) {
        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
}
