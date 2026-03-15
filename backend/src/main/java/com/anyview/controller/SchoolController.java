package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.entity.School;
import com.anyview.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schools")
@RequiredArgsConstructor
public class SchoolController {
    private final SchoolService schoolService;

    @GetMapping
    public ApiResponse<List<School>> getAllSchools() {
        try {
            List<School> schools = schoolService.getAllSchools();
            return ApiResponse.success(schools);
        } catch (Exception e) {
            return ApiResponse.error("获取学校列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/enabled")
    public ApiResponse<List<School>> getEnabledSchools() {
        try {
            List<School> schools = schoolService.getEnabledSchools();
            return ApiResponse.success(schools);
        } catch (Exception e) {
            return ApiResponse.error("获取学校列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<School> getSchoolById(@PathVariable Long id) {
        try {
            School school = schoolService.getSchoolById(id);
            return ApiResponse.success(school);
        } catch (Exception e) {
            return ApiResponse.error("获取学校信息失败：" + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<School> createSchool(@RequestBody School school) {
        try {
            School created = schoolService.createSchool(school);
            return ApiResponse.success("学校创建成功", created);
        } catch (Exception e) {
            return ApiResponse.error("创建学校失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<School> updateSchool(@PathVariable Long id, @RequestBody School school) {
        try {
            School updated = schoolService.updateSchool(id, school);
            return ApiResponse.success("学校更新成功", updated);
        } catch (Exception e) {
            return ApiResponse.error("更新学校失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSchool(@PathVariable Long id) {
        try {
            schoolService.deleteSchool(id);
            return ApiResponse.success("学校删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error("删除学校失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}/enable")
    public ApiResponse<School> enableSchool(@PathVariable Long id, @RequestBody EnableRequest request) {
        try {
            School school = schoolService.enableSchool(id, request.isEnabled());
            return ApiResponse.success(request.isEnabled() ? "学校启用成功" : "学校禁用成功", school);
        } catch (Exception e) {
            return ApiResponse.error("更新学校状态失败：" + e.getMessage());
        }
    }

    public static class EnableRequest {
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
