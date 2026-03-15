package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.entity.User;
import com.anyview.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ApiResponse<List<User>> getUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.error("获取用户列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error("获取用户失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ApiResponse.success(updatedUser);
        } catch (Exception e) {
            return ApiResponse.error("更新用户失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("删除用户失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}/enable")
    public ApiResponse<Void> enableUser(@PathVariable Long id, @RequestBody EnableRequest request) {
        try {
            userService.enableUser(id, request.enabled);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("操作失败：" + e.getMessage());
        }
    }

    static class EnableRequest {
        public boolean enabled;
    }
}
