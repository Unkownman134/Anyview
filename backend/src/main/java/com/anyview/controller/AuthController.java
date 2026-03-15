package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.dto.ForgotPasswordRequest;
import com.anyview.dto.JwtResponse;
import com.anyview.dto.LoginRequest;
import com.anyview.dto.RegisterRequest;
import com.anyview.dto.ResetPasswordRequest;
import com.anyview.entity.User;
import com.anyview.service.AuthService;
import com.anyview.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ApiResponse<JwtResponse> login(@RequestBody LoginRequest request) {
        try {
            JwtResponse response = authService.login(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("登录失败：" + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request);
            return ApiResponse.success("注册成功", user);
        } catch (Exception e) {
            return ApiResponse.error("注册失败：" + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ApiResponse<User> getCurrentUser() {
        try {
            User user = authService.getCurrentUser();
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error("获取用户信息失败：" + e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            passwordResetService.forgotPassword(request);
            return ApiResponse.success("重置密码链接已发送到您的邮箱", null);
        } catch (Exception e) {
            return ApiResponse.error("发送重置链接失败：" + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request);
            return ApiResponse.success("密码重置成功", null);
        } catch (Exception e) {
            return ApiResponse.error("密码重置失败：" + e.getMessage());
        }
    }
}
