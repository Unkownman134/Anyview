package com.anyview.repository;

import com.anyview.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudentId(Long studentId);
    List<Submission> findByAssignmentId(Long assignmentId);
    List<Submission> findByQuestionId(Long questionId);
    List<Submission> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);
}
