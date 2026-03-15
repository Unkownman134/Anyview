package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiService aiService;

    @PostMapping("/analyze")
    public ApiResponse<String> analyzeCode(@RequestBody AnalyzeRequest request) {
        try {
            String result = aiService.analyzeCode(request.getCode(), request.getLanguage());
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("代码分析失败：" + e.getMessage());
        }
    }

    @PostMapping("/grade")
    public ApiResponse<String> gradeCode(@RequestBody GradeRequest request) {
        try {
            String result = aiService.gradeCode(
                    request.getCode(),
                    request.getQuestion(),
                    request.getSampleInput(),
                    request.getSampleOutput()
            );
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("代码评分失败：" + e.getMessage());
        }
    }

    @PostMapping("/feedback")
    public ApiResponse<String> generateFeedback(@RequestBody FeedbackRequest request) {
        try {
            String result = aiService.generateFeedback(request.getCode(), request.getErrorType());
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("生成反馈失败：" + e.getMessage());
        }
    }

    public static class AnalyzeRequest {
        private String code;
        private String language;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }

    public static class GradeRequest {
        private String code;
        private String question;
        private String sampleInput;
        private String sampleOutput;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getSampleInput() {
            return sampleInput;
        }

        public void setSampleInput(String sampleInput) {
            this.sampleInput = sampleInput;
        }

        public String getSampleOutput() {
            return sampleOutput;
        }

        public void setSampleOutput(String sampleOutput) {
            this.sampleOutput = sampleOutput;
        }
    }

    public static class FeedbackRequest {
        private String code;
        private String errorType;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getErrorType() {
            return errorType;
        }

        public void setErrorType(String errorType) {
            this.errorType = errorType;
        }
    }
}
