package com.anyview.repository;

import com.anyview.entity.Submission;
import com.anyview.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudentId(Long studentId);
    
    @Query("SELECT s FROM Submission s LEFT JOIN FETCH s.student LEFT JOIN FETCH s.question LEFT JOIN FETCH s.assignment WHERE s.assignment.id = :assignmentId")
    List<Submission> findByAssignmentIdWithDetails(@Param("assignmentId") Long assignmentId);
    
    @Query("SELECT s FROM Submission s LEFT JOIN FETCH s.student LEFT JOIN FETCH s.question LEFT JOIN FETCH s.assignment")
    List<Submission> findAllWithDetails();
    
    @Query("SELECT s FROM Submission s LEFT JOIN FETCH s.student LEFT JOIN FETCH s.question LEFT JOIN FETCH s.assignment WHERE s.student.id = :studentId")
    List<Submission> findByStudentIdWithDetails(@Param("studentId") Long studentId);
    
    @Query("SELECT s FROM Submission s LEFT JOIN FETCH s.student LEFT JOIN FETCH s.question LEFT JOIN FETCH s.assignment WHERE s.student.id = :studentId AND s.assignment.id = :assignmentId")
    List<Submission> findByStudentIdAndAssignmentIdWithDetails(@Param("studentId") Long studentId, @Param("assignmentId") Long assignmentId);
    
    @Query("SELECT s FROM Submission s WHERE s.student.id = :studentId AND s.question.id = :questionId")
    List<Submission> findByStudentIdAndQuestionId(@Param("studentId") Long studentId, @Param("questionId") Long questionId);
    
    List<Submission> findByAssignmentId(Long assignmentId);
    List<Submission> findByQuestionId(Long questionId);
    List<Submission> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);
    List<Submission> findByCreatedAtAfter(LocalDateTime dateTime);
    List<Submission> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    long countByStatus(SubmissionStatus status);
    long countByCreatedAtAfter(LocalDateTime dateTime);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    long countByCreatedAtBetweenAndStatus(LocalDateTime start, LocalDateTime end, SubmissionStatus status);
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
    
    void deleteByAssignmentId(Long assignmentId);
}
