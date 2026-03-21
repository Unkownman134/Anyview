package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.dto.analysis.ClassAnalysisDTO;
import com.anyview.dto.analysis.QuestionAnalysisDTO;
import com.anyview.dto.analysis.SystemAnalysisDTO;
import com.anyview.dto.analysis.UserAnalysisDTO;
import com.anyview.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final AnalysisService analysisService;

    @GetMapping("/system")
    public ApiResponse<SystemAnalysisDTO> getSystemAnalysis(
            @RequestParam(defaultValue = "30") Integer days) {
        try {
            SystemAnalysisDTO analysis = analysisService.getSystemAnalysis(days);
            return ApiResponse.success(analysis);
        } catch (Exception e) {
            return ApiResponse.error("获取系统分析数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<UserAnalysisDTO> getUserAnalysis(@PathVariable Long userId) {
        try {
            UserAnalysisDTO analysis = analysisService.getUserAnalysis(userId);
            return ApiResponse.success(analysis);
        } catch (Exception e) {
            return ApiResponse.error("获取用户分析数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/question/{questionId}")
    public ApiResponse<QuestionAnalysisDTO> getQuestionAnalysis(@PathVariable Long questionId) {
        try {
            QuestionAnalysisDTO analysis = analysisService.getQuestionAnalysis(questionId);
            return ApiResponse.success(analysis);
        } catch (Exception e) {
            return ApiResponse.error("获取题目分析数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/class/{classId}")
    public ApiResponse<ClassAnalysisDTO> getClassAnalysis(@PathVariable Long classId) {
        try {
            ClassAnalysisDTO analysis = analysisService.getClassAnalysis(classId);
            return ApiResponse.success(analysis);
        } catch (Exception e) {
            return ApiResponse.error("获取班级分析数据失败: " + e.getMessage());
        }
    }

    @PostMapping("/activity/login")
    public ApiResponse<Void> recordLogin() {
        try {
            analysisService.recordLoginActivity();
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("记录登录活动失败: " + e.getMessage());
        }
    }
}
