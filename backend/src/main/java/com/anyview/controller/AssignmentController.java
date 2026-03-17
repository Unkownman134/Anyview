package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.entity.Assignment;
import com.anyview.entity.AssignmentQuestion;
import com.anyview.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    @GetMapping
    public ApiResponse<List<Assignment>> getAssignments() {
        try {
            List<Assignment> assignments = assignmentService.getAssignments();
            return ApiResponse.success(assignments);
        } catch (Exception e) {
            return ApiResponse.error("获取作业列表失败：" + e.getMessage());
        }
    }

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
            System.out.println("收到获取作业详情请求，作业ID: " + id);
            Assignment assignment = assignmentService.getAssignmentById(id);
            System.out.println("作业题目数量: " + (assignment != null && assignment.getAssignmentQuestions() != null ? assignment.getAssignmentQuestions().size() : 0));
            return ApiResponse.success(assignment);
        } catch (Exception e) {
            System.err.println("获取作业详情失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error("获取作业信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/class/{classId}")
    public ApiResponse<List<Assignment>> getAssignmentsByClass(@PathVariable Long classId) {
        try {
            List<Assignment> assignments = assignmentService.getAssignmentsByClassId(classId);
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

    @PutMapping("/{id}/with-questions")
    public ApiResponse<Assignment> updateAssignmentWithQuestions(@PathVariable Long id, @RequestBody AssignmentRequest request) {
        try {
            Assignment assignment = request.getAssignment();
            assignment.setId(id);
            List<AssignmentQuestion> questions = request.getQuestions();
            Assignment updated = assignmentService.updateAssignmentWithQuestions(id, assignment, questions);
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

    public static class AssignmentRequest {
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
