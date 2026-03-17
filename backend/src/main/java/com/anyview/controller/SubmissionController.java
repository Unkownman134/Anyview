package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.dto.GradeRequest;
import com.anyview.dto.SubmissionGradeResponse;
import com.anyview.entity.Submission;
import com.anyview.repository.SubmissionRepository;
import com.anyview.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionRepository submissionRepository;
    private final SubmissionService submissionService;
    private static final Logger logger = LoggerFactory.getLogger(SubmissionController.class);

    @GetMapping
    public ApiResponse<List<Submission>> getSubmissions() {
        try {
            List<Submission> submissions = submissionService.getSubmissions();
            logger.info("获取提交记录列表，数量: {}", submissions.size());
            submissions.forEach(sub -> logger.info("提交记录: ID={}, 学生ID={}, 作业ID={}, 题目ID={}", 
                sub.getId(), sub.getStudentId(), sub.getAssignmentId(), sub.getQuestionId()));
            return ApiResponse.success(submissions);
        } catch (Exception e) {
            logger.error("获取提交列表失败", e);
            return ApiResponse.error("获取提交列表失败：" + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<Submission> createSubmission(@RequestBody Submission submission) {
        try {
            Submission created = submissionService.createSubmission(submission);
            return ApiResponse.success("提交成功", created);
        } catch (Exception e) {
            return ApiResponse.error("提交失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<Submission> getSubmissionById(@PathVariable Long id) {
        try {
            Submission submission = submissionService.getSubmissionByIdWithDetails(id);
            return ApiResponse.success(submission);
        } catch (Exception e) {
            return ApiResponse.error("获取提交信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse<List<Submission>> getSubmissionsByStudent(@PathVariable Long studentId) {
        try {
            List<Submission> submissions = submissionService.getSubmissionsByStudent(studentId);
            return ApiResponse.success(submissions);
        } catch (Exception e) {
            return ApiResponse.error("获取提交列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/assignment/{assignmentId}")
    public ApiResponse<List<Submission>> getSubmissionsByAssignment(@PathVariable Long assignmentId) {
        try {
            List<Submission> submissions = submissionRepository.findByAssignmentIdWithDetails(assignmentId);
            return ApiResponse.success(submissions);
        } catch (Exception e) {
            return ApiResponse.error("获取提交列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/question/{questionId}")
    public ApiResponse<List<Submission>> getSubmissionsByQuestion(@PathVariable Long questionId) {
        try {
            List<Submission> submissions = submissionService.getSubmissionsByQuestion(questionId);
            return ApiResponse.success(submissions);
        } catch (Exception e) {
            return ApiResponse.error("获取提交列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}/assignment/{assignmentId}")
    public ApiResponse<List<Submission>> getSubmissionsByStudentAndAssignment(
            @PathVariable Long studentId, @PathVariable Long assignmentId) {
        try {
            List<Submission> submissions = submissionService.getSubmissionsByStudentAndAssignment(studentId, assignmentId);
            return ApiResponse.success(submissions);
        } catch (Exception e) {
            return ApiResponse.error("获取提交列表失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Submission> updateSubmission(@PathVariable Long id, @RequestBody Submission submission) {
        try {
            submission.setId(id);
            Submission updated = submissionService.updateSubmission(submission);
            return ApiResponse.success("提交更新成功", updated);
        } catch (Exception e) {
            return ApiResponse.error("更新提交失败：" + e.getMessage());
        }
    }

    @PostMapping("/{id}/grade")
    public ApiResponse<SubmissionGradeResponse> gradeSubmission(
            @PathVariable Long id,
            @RequestBody GradeRequest request) {
        try {
            SubmissionGradeResponse graded = submissionService.gradeSubmission(id, request.getScore(), request.getTeacherComment());
            return ApiResponse.success("评分成功", graded);
        } catch (Exception e) {
            return ApiResponse.error("评分失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSubmission(@PathVariable Long id) {
        try {
            submissionService.deleteSubmission(id);
            return ApiResponse.success("提交删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error("删除提交失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}/question")
    public ApiResponse<com.anyview.entity.Question> getSubmissionQuestion(@PathVariable Long id) {
        try {
            Submission submission = submissionService.getSubmissionById(id);
            if (submission == null) {
                return ApiResponse.error("提交记录不存在");
            }
            return ApiResponse.success(submission.getQuestion());
        } catch (Exception e) {
            return ApiResponse.error("获取题目信息失败：" + e.getMessage());
        }
    }
}
