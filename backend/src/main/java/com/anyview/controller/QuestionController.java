package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.entity.Question;
import com.anyview.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    public ApiResponse<Question> createQuestion(@RequestBody Question question) {
        try {
            Question created = questionService.createQuestion(question);
            return ApiResponse.success("题目创建成功", created);
        } catch (Exception e) {
            return ApiResponse.error("创建题目失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<Question> getQuestionById(@PathVariable Long id) {
        try {
            Question question = questionService.getQuestionById(id);
            return ApiResponse.success(question);
        } catch (Exception e) {
            return ApiResponse.error("获取题目信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/creator/{creatorId}")
    public ApiResponse<List<Question>> getQuestionsByCreator(@PathVariable Long creatorId) {
        try {
            List<Question> questions = questionService.getQuestionsByCreator(creatorId);
            return ApiResponse.success(questions);
        } catch (Exception e) {
            return ApiResponse.error("获取题目列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/public")
    public ApiResponse<List<Question>> getPublicQuestions() {
        try {
            List<Question> questions = questionService.getPublicQuestions();
            return ApiResponse.success(questions);
        } catch (Exception e) {
            return ApiResponse.error("获取公共题目失败：" + e.getMessage());
        }
    }

    @GetMapping("/difficulty/{difficulty}")
    public ApiResponse<List<Question>> getQuestionsByDifficulty(@PathVariable Integer difficulty) {
        try {
            List<Question> questions = questionService.getQuestionsByDifficulty(difficulty);
            return ApiResponse.success(questions);
        } catch (Exception e) {
            return ApiResponse.error("获取题目列表失败：" + e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<List<Question>> getAllQuestions() {
        try {
            List<Question> questions = questionService.getAllQuestions();
            return ApiResponse.success(questions);
        } catch (Exception e) {
            return ApiResponse.error("获取题目列表失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Question> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        try {
            question.setId(id);
            Question updated = questionService.updateQuestion(question);
            return ApiResponse.success("题目更新成功", updated);
        } catch (Exception e) {
            return ApiResponse.error("更新题目失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteQuestion(@PathVariable Long id) {
        try {
            questionService.deleteQuestion(id);
            return ApiResponse.success("题目删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error("删除题目失败：" + e.getMessage());
        }
    }
}
