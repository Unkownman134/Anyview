package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.service.CodeExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/code")
@RequiredArgsConstructor
public class CodeExecutionController {
    private final CodeExecutionService codeExecutionService;

    @PostMapping("/execute")
    public ApiResponse<CodeExecutionService.ExecutionResult> executeCode(@RequestBody Map<String, String> request) {
        try {
            String code = request.get("code");
            String language = request.get("language");

            if (code == null || language == null) {
                return ApiResponse.error("缺少必要参数");
            }

            CodeExecutionService.ExecutionResult result = codeExecutionService.executeCode(code, language);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("执行代码失败: " + e.getMessage());
        }
    }
}