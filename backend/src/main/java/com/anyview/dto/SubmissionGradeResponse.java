package com.anyview.dto;

import com.anyview.entity.GradeStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionGradeResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long assignmentId;
    private String assignmentTitle;
    private Long questionId;
    private String questionTitle;
    private String code;
    private Integer score;
    private String teacherComment;
    private GradeStatus gradeStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
