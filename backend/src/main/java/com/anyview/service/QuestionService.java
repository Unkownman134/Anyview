package com.anyview.service;

import com.anyview.entity.Question;
import com.anyview.entity.User;
import com.anyview.repository.QuestionRepository;
import com.anyview.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final RedisUtil redisUtil;
    
    private static final String QUESTION_PREFIX = "question:";
    private static final String QUESTIONS_LIST_PREFIX = "questions:";
    private static final long CACHE_TIMEOUT = 30;

    public Question createQuestion(Question question) {
        Question savedQuestion = questionRepository.save(question);
        
        // 清除相关缓存
        String listKey = QUESTIONS_LIST_PREFIX + "creator:" + question.getCreator().getId();
        redisUtil.delete(listKey);
        redisUtil.delete(QUESTIONS_LIST_PREFIX + "public");
        redisUtil.delete(QUESTIONS_LIST_PREFIX + "all");
        
        return savedQuestion;
    }

    public Question getQuestionById(Long id) {
        String key = QUESTION_PREFIX + id;
        Object cachedQuestion = redisUtil.get(key);
        
        if (cachedQuestion instanceof Question) {
            return (Question) cachedQuestion;
        }
        
        Question question = questionRepository.findById(id).orElse(null);
        if (question != null) {
            redisUtil.set(key, question, CACHE_TIMEOUT, TimeUnit.MINUTES);
        }
        
        return question;
    }

    public List<Question> getQuestionsByCreator(Long creatorId) {
        String key = QUESTIONS_LIST_PREFIX + "creator:" + creatorId;
        Object cachedQuestions = redisUtil.get(key);
        
        if (cachedQuestions instanceof List) {
            return (List<Question>) cachedQuestions;
        }
        
        List<Question> questions = questionRepository.findByCreatorId(creatorId);
        redisUtil.set(key, questions, CACHE_TIMEOUT, TimeUnit.MINUTES);
        
        return questions;
    }

    public List<Question> getPublicQuestions() {
        String key = QUESTIONS_LIST_PREFIX + "public";
        Object cachedQuestions = redisUtil.get(key);
        
        if (cachedQuestions instanceof List) {
            return (List<Question>) cachedQuestions;
        }
        
        List<Question> questions = questionRepository.findByIsPublicTrue();
        redisUtil.set(key, questions, CACHE_TIMEOUT, TimeUnit.MINUTES);
        
        return questions;
    }

    public List<Question> getQuestionsByDifficulty(Integer difficulty) {
        String key = QUESTIONS_LIST_PREFIX + "difficulty:" + difficulty;
        Object cachedQuestions = redisUtil.get(key);
        
        if (cachedQuestions instanceof List) {
            return (List<Question>) cachedQuestions;
        }
        
        List<Question> questions = questionRepository.findByDifficulty(difficulty);
        redisUtil.set(key, questions, CACHE_TIMEOUT, TimeUnit.MINUTES);
        
        return questions;
    }

    public Question updateQuestion(Question question) {
        Question updatedQuestion = questionRepository.save(question);
        
        // 更新缓存
        String key = QUESTION_PREFIX + question.getId();
        redisUtil.set(key, updatedQuestion, CACHE_TIMEOUT, TimeUnit.MINUTES);
        
        // 清除相关列表缓存
        String listKey = QUESTIONS_LIST_PREFIX + "creator:" + question.getCreator().getId();
        redisUtil.delete(listKey);
        redisUtil.delete(QUESTIONS_LIST_PREFIX + "public");
        redisUtil.delete(QUESTIONS_LIST_PREFIX + "difficulty:" + question.getDifficulty());
        redisUtil.delete(QUESTIONS_LIST_PREFIX + "all");
        
        return updatedQuestion;
    }

    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question != null) {
            questionRepository.deleteById(id);
            
            // 删除缓存
            String key = QUESTION_PREFIX + id;
            redisUtil.delete(key);
            
            // 清除相关列表缓存
            String listKey = QUESTIONS_LIST_PREFIX + "creator:" + question.getCreator().getId();
            redisUtil.delete(listKey);
            redisUtil.delete(QUESTIONS_LIST_PREFIX + "public");
            redisUtil.delete(QUESTIONS_LIST_PREFIX + "difficulty:" + question.getDifficulty());
            redisUtil.delete(QUESTIONS_LIST_PREFIX + "all");
        }
    }

    public List<Question> getAllQuestions() {
        String key = QUESTIONS_LIST_PREFIX + "all";
        Object cachedQuestions = redisUtil.get(key);
        
        if (cachedQuestions instanceof List) {
            return (List<Question>) cachedQuestions;
        }
        
        List<Question> questions = questionRepository.findAll();
        redisUtil.set(key, questions, CACHE_TIMEOUT, TimeUnit.MINUTES);
        
        return questions;
    }
}
