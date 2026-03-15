package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/create")
    public ApiResponse<Map<String, Object>> createSession(@RequestParam String userId, @RequestBody Object sessionData) {
        try {
            sessionService.saveSession(userId, sessionData);
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("message", "会话创建成功");
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("创建会话失败：" + e.getMessage());
        }
    }

    @GetMapping("/get")
    public ApiResponse<Object> getSession(@RequestParam String userId) {
        try {
            Object sessionData = sessionService.getSession(userId);
            if (sessionData != null) {
                return ApiResponse.success(sessionData);
            } else {
                return ApiResponse.error("会话不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("获取会话失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ApiResponse<Void> deleteSession(@RequestParam String userId) {
        try {
            sessionService.removeSession(userId);
            return ApiResponse.success("会话删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error("删除会话失败：" + e.getMessage());
        }
    }

    @PostMapping("/update")
    public ApiResponse<Void> updateSession(@RequestParam String userId, @RequestBody Object sessionData) {
        try {
            sessionService.updateSession(userId, sessionData);
            return ApiResponse.success("会话更新成功", null);
        } catch (Exception e) {
            return ApiResponse.error("更新会话失败：" + e.getMessage());
        }
    }

    @GetMapping("/exists")
    public ApiResponse<Boolean> sessionExists(@RequestParam String userId) {
        try {
            boolean exists = sessionService.existsSession(userId);
            return ApiResponse.success(exists);
        } catch (Exception e) {
            return ApiResponse.error("检查会话失败：" + e.getMessage());
        }
    }
}