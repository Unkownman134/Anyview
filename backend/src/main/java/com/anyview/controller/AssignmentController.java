package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.entity.Assignment;
import com.anyview.entity.AssignmentQuestion;
import com.anyview.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    @PostMapping
    public ApiResponse<Assignment> createAssignment(@RequestBody Assignment assignment) {
        try {
            Assignment created = assignmentService.createAssignment(assignment);
            return ApiResponse.success("作业创建成功", created);
        } catch (Exception e) {
            return ApiResponse.error("创建作业失败：" + e.getMessage());
        }
    }

    @PostMapping("/with-questions")
    public ApiResponse<Assignment> createAssignmentWithQuestions(@RequestBody CreateAssignmentRequest request) {
        try {
            Assignment created = assignmentService.createAssignmentWithQuestions(request.getAssignment(), request.getQuestions());
            return ApiResponse.success("作业创建成功", created);
        } catch (Exception e) {
            return ApiResponse.error("创建作业失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<Assignment> getAssignmentById(@PathVariable Long id) {
        try {
            Assignment assignment = assignmentService.getAssignmentById(id);
            return ApiResponse.success(assignment);
        } catch (Exception e) {
            return ApiResponse.error("获取作业信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/class/{classId}")
    public ApiResponse<List<Assignment>> getAssignmentsByClass(@PathVariable Long classId) {
        try {
            List<Assignment> assignments = assignmentService.getAssignmentsByClass(classId);
            return ApiResponse.success(assignments);
        } catch (Exception e) {
            return ApiResponse.error("获取作业列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/teacher/{teacherId}")
    public ApiResponse<List<Assignment>> getAssignmentsByTeacher(@PathVariable Long teacherId) {
        try {
            List<Assignment> assignments = assignmentService.getAssignmentsByTeacher(teacherId);
            return ApiResponse.success(assignments);
        } catch (Exception e) {
            return ApiResponse.error("获取作业列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/published")
    public ApiResponse<List<Assignment>> getPublishedAssignments() {
        try {
            List<Assignment> assignments = assignmentService.getPublishedAssignments();
            return ApiResponse.success(assignments);
        } catch (Exception e) {
            return ApiResponse.error("获取作业列表失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Assignment> updateAssignment(@PathVariable Long id, @RequestBody Assignment assignment) {
        try {
            assignment.setId(id);
            Assignment updated = assignmentService.updateAssignment(assignment);
            return ApiResponse.success("作业更新成功", updated);
        } catch (Exception e) {
            return ApiResponse.error("更新作业失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAssignment(@PathVariable Long id) {
        try {
            assignmentService.deleteAssignment(id);
            return ApiResponse.success("作业删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error("删除作业失败：" + e.getMessage());
        }
    }

    @PostMapping("/{id}/publish")
    public ApiResponse<Void> publishAssignment(@PathVariable Long id) {
        try {
            assignmentService.publishAssignment(id);
            return ApiResponse.success("作业发布成功", null);
        } catch (Exception e) {
            return ApiResponse.error("发布作业失败：" + e.getMessage());
        }
    }

    public static class CreateAssignmentRequest {
        private Assignment assignment;
        private List<AssignmentQuestion> questions;

        public Assignment getAssignment() {
            return assignment;
        }

        public void setAssignment(Assignment assignment) {
            this.assignment = assignment;
        }

        public List<AssignmentQuestion> getQuestions() {
            return questions;
        }

        public void setQuestions(List<AssignmentQuestion> questions) {
            this.questions = questions;
        }
    }
}
