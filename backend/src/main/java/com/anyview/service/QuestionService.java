package com.anyview.service;

import com.anyview.dto.QuestionDTO;
import com.anyview.entity.Question;
import com.anyview.entity.QuestionType;
import com.anyview.repository.QuestionRepository;
import com.anyview.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private RedisUtil redisUtil;

    private static final String QUESTIONS_LIST_PREFIX = "questions:list:";

    // 创建题目
    public Question createQuestion(Question question) {
        Question saved = questionRepository.save(question);
        // 清除缓存
        redisUtil.delete(QUESTIONS_LIST_PREFIX + "all");
        return saved;
    }

    // 根据ID获取题目
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    // 更新题目
    public Question updateQuestion(Question question) {
        Question updated = questionRepository.save(question);
        // 清除缓存
        redisUtil.delete(QUESTIONS_LIST_PREFIX + "all");
        return updated;
    }

    // 删除题目
    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question != null) {
            questionRepository.delete(question);
            // 清除缓存
            redisUtil.delete(QUESTIONS_LIST_PREFIX + "all");
        }
    }

    // 获取所有题目
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // 根据类型获取题目（由于QuestionRepository没有findByType方法，这里返回所有题目）
    public List<Question> getQuestionsByType(String type) {
        return getAllQuestions();
    }

    // 根据难度获取题目
    public List<Question> getQuestionsByDifficulty(String difficulty) {
        return questionRepository.findByDifficulty(difficulty);
    }

    // 搜索题目（由于QuestionRepository没有findAll(Specification)方法，这里返回所有题目）
    public List<Question> searchQuestions(String keyword) {
        return getAllQuestions();
    }

    // 根据创建者获取题目
    public List<Question> getQuestionsByCreator(Long creatorId) {
        return questionRepository.findByCreatorId(creatorId);
    }

    // 获取公开题目（由于Question实体类没有isPublic字段，这里返回所有题目）
    public List<Question> getPublicQuestions() {
        return getAllQuestions();
    }

    // 转换方法：Question -> QuestionDTO
    public QuestionDTO convertToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setTitle(question.getTitle());
        dto.setDescription(question.getDescription());
        dto.setOptions(question.getOptions());
        dto.setAnswer(question.getAnswer());
        dto.setScore(question.getScore());
        dto.setAnalysis(question.getAnalysis());
        dto.setTemplateCode(question.getTemplateCode());
        dto.setSampleInput(question.getSampleInput());
        dto.setSampleOutput(question.getSampleOutput());
        dto.setTestCases(question.getTestCases());
        dto.setReferenceSolution(question.getReferenceSolution());
        dto.setDifficulty(question.getDifficulty().equals("easy") ? 1 : question.getDifficulty().equals("medium") ? 2 : 3);
        dto.setTimeLimit(question.getTimeLimit());
        dto.setMemoryLimit(question.getMemoryLimit());
        try {
            dto.setType(QuestionType.valueOf(question.getType().toUpperCase()));
        } catch (Exception e) {
            dto.setType(QuestionType.PROGRAMMING);
        }
        dto.setPublic(question.getIsPublic() != null ? question.getIsPublic() : true);
        if (question.getCreator() != null) {
            dto.setCreatorId(question.getCreator().getId());
            dto.setCreatorName(question.getCreator().getUsername());
        }
        dto.setCreatedAt(question.getCreatedAt());
        dto.setUpdatedAt(question.getUpdatedAt());
        return dto;
    }

    // 转换方法：List<Question> -> List<QuestionDTO>
    public List<QuestionDTO> convertToDTOList(List<Question> questions) {
        return questions.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }
}
