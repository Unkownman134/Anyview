package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.dto.QuestionDTO;
import com.anyview.entity.Question;
import com.anyview.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @PostMapping
    public ApiResponse<QuestionDTO> createQuestion(@RequestBody QuestionDTO questionDTO) {
        try {
            Question question = new Question();
            question.setTitle(questionDTO.getTitle());
            question.setDescription(questionDTO.getDescription());
            question.setType(questionDTO.getType() != null ? questionDTO.getType().name().toLowerCase() : "programming");
            question.setOptions(questionDTO.getOptions());
            question.setAnswer(questionDTO.getAnswer());
            question.setScore(questionDTO.getScore() != null ? questionDTO.getScore() : 10);
            question.setDifficulty(questionDTO.getDifficulty() != null ? (questionDTO.getDifficulty() == 1 ? "easy" : questionDTO.getDifficulty() == 2 ? "medium" : "hard") : "easy");
            question.setAnalysis(questionDTO.getAnalysis());
            question.setTemplateCode(questionDTO.getTemplateCode());
            question.setSampleInput(questionDTO.getSampleInput());
            question.setSampleOutput(questionDTO.getSampleOutput());
            question.setTestCases(questionDTO.getTestCases());
            question.setReferenceSolution(questionDTO.getReferenceSolution());
            question.setTimeLimit(questionDTO.getTimeLimit());
            question.setMemoryLimit(questionDTO.getMemoryLimit());
            question.setIsPublic(questionDTO.isPublic());
            
            Question saved = questionService.createQuestion(question);
            return ApiResponse.success(questionService.convertToDTO(saved));
        } catch (Exception e) {
            return ApiResponse.error("创建题目失败：" + e.getMessage());
        }
    }

    @GetMapping("/by-creator/{creatorId}")
    public ApiResponse<List<QuestionDTO>> getQuestionsByCreator(@PathVariable Long creatorId) {
        try {
            List<QuestionDTO> dtos = questionService.convertToDTOList(questionService.getQuestionsByCreator(creatorId));
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error("获取题目列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/public")
    public ApiResponse<List<QuestionDTO>> getPublicQuestions() {
        try {
            List<QuestionDTO> dtos = questionService.convertToDTOList(questionService.getPublicQuestions());
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error("获取公共题目失败：" + e.getMessage());
        }
    }

    @GetMapping("/difficulty/{difficulty}")
    public ApiResponse<List<QuestionDTO>> getQuestionsByDifficulty(@PathVariable String difficulty) {
        try {
            List<QuestionDTO> dtos = questionService.convertToDTOList(questionService.getQuestionsByDifficulty(difficulty));
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error("获取题目列表失败：" + e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<List<QuestionDTO>> getAllQuestions() {
        try {
            List<QuestionDTO> dtos = questionService.convertToDTOList(questionService.getAllQuestions());
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error("获取题目列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/type/{type}")
    public ApiResponse<List<QuestionDTO>> getQuestionsByType(@PathVariable String type) {
        try {
            List<QuestionDTO> dtos = questionService.convertToDTOList(questionService.getQuestionsByType(type));
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error("获取题目列表失败：" + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ApiResponse<QuestionDTO> updateQuestion(@RequestBody QuestionDTO questionDTO) {
        try {
            Question question = questionService.getQuestionById(questionDTO.getId());
            if (question == null) {
                return ApiResponse.error("题目不存在");
            }
            question.setTitle(questionDTO.getTitle());
            question.setDescription(questionDTO.getDescription());
            question.setType(questionDTO.getType() != null ? questionDTO.getType().name().toLowerCase() : "programming");
            question.setOptions(questionDTO.getOptions());
            question.setAnswer(questionDTO.getAnswer());
            question.setScore(questionDTO.getScore() != null ? questionDTO.getScore() : 10);
            question.setDifficulty(questionDTO.getDifficulty() != null ? (questionDTO.getDifficulty() == 1 ? "easy" : questionDTO.getDifficulty() == 2 ? "medium" : "hard") : "easy");
            question.setAnalysis(questionDTO.getAnalysis());
            question.setTemplateCode(questionDTO.getTemplateCode());
            question.setSampleInput(questionDTO.getSampleInput());
            question.setSampleOutput(questionDTO.getSampleOutput());
            question.setTestCases(questionDTO.getTestCases());
            question.setReferenceSolution(questionDTO.getReferenceSolution());
            question.setTimeLimit(questionDTO.getTimeLimit());
            question.setMemoryLimit(questionDTO.getMemoryLimit());
            question.setIsPublic(questionDTO.isPublic());
            
            Question updated = questionService.updateQuestion(question);
            return ApiResponse.success(questionService.convertToDTO(updated));
        } catch (Exception e) {
            return ApiResponse.error("更新题目失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteQuestion(@PathVariable Long id) {
        try {
            questionService.deleteQuestion(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除题目失败：" + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ApiResponse<List<QuestionDTO>> searchQuestions(@RequestParam String keyword) {
        try {
            List<QuestionDTO> dtos = questionService.convertToDTOList(questionService.searchQuestions(keyword));
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error("搜索题目失败：" + e.getMessage());
        }
    }
}
