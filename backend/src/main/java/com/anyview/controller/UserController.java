package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.dto.UserDTO;
import com.anyview.entity.User;
import com.anyview.service.UserService;
import com.anyview.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ApiResponse<List<UserDTO>> getUsers() {
        try {
            List<User> users = userService.getAllUsers();
            List<UserDTO> userDTOs = users.stream()
                    .map(UserMapper::toDTO)
                    .collect(Collectors.toList());
            return ApiResponse.success(userDTOs);
        } catch (Exception e) {
            return ApiResponse.error("获取用户列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }
            return ApiResponse.success(UserMapper.toDTO(user));
        } catch (Exception e) {
            return ApiResponse.error("获取用户失败：" + e.getMessage());
        }
    }

    @GetMapping("/role/{role}")
    public ApiResponse<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        try {
            List<User> users = userService.getUsersByRole(role.toUpperCase());
            List<UserDTO> userDTOs = users.stream()
                    .map(UserMapper::toDTO)
                    .collect(Collectors.toList());
            return ApiResponse.success(userDTOs);
        } catch (Exception e) {
            return ApiResponse.error("获取用户列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/role/{role}/school/{schoolId}")
    public ApiResponse<List<UserDTO>> getUsersByRoleAndSchool(@PathVariable String role, @PathVariable Long schoolId) {
        try {
            List<User> users = userService.getUsersByRoleAndSchool(role.toUpperCase(), schoolId);
            List<UserDTO> userDTOs = users.stream()
                    .map(UserMapper::toDTO)
                    .collect(Collectors.toList());
            return ApiResponse.success(userDTOs);
        } catch (Exception e) {
            return ApiResponse.error("获取用户列表失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<UserDTO> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ApiResponse.success(UserMapper.toDTO(updatedUser));
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
