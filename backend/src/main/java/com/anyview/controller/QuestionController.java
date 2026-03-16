package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.dto.QuestionDTO;
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
            // 这里需要根据DTO创建Question实体，暂时返回成功
            return ApiResponse.success(questionDTO);
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
            // 这里需要根据DTO更新Question实体，暂时返回成功
            return ApiResponse.success(questionDTO);
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
