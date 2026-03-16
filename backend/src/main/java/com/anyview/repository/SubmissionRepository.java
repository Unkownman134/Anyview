package com.anyview.repository;

import com.anyview.entity.Submission;
import com.anyview.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudentId(Long studentId);
    List<Submission> findByAssignmentId(Long assignmentId);
    List<Submission> findByQuestionId(Long questionId);
    List<Submission> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);
    long countByStatus(SubmissionStatus status);
    long countByCreatedAtAfter(LocalDateTime dateTime);
    long countByQuestionId(Long questionId);
    long countByQuestionIdAndStatus(Long questionId, SubmissionStatus status);
    long countByStudentId(Long studentId);
    long countByStudentIdAndStatus(Long studentId, SubmissionStatus status);
    @Query("SELECT AVG(s.score) FROM Submission s")
    Double findAverageScore();
    @Query("SELECT AVG(s.score) FROM Submission s WHERE s.question.id = :questionId")
    Double findAverageScoreByQuestionId(Long questionId);
    @Query("SELECT AVG(s.score) FROM Submission s WHERE s.student.id = :userId")
    Double findAverageScoreByUserId(Long userId);
}
