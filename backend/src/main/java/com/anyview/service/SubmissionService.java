package com.anyview.service;

import com.anyview.entity.Submission;
import com.anyview.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;

    public Submission createSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    public Submission getSubmissionById(Long id) {
        return submissionRepository.findById(id).orElse(null);
    }

    public List<Submission> getSubmissionsByStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId);
    }

    public List<Submission> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    public List<Submission> getSubmissionsByQuestion(Long questionId) {
        return submissionRepository.findByQuestionId(questionId);
    }

    public List<Submission> getSubmissionsByStudentAndAssignment(Long studentId, Long assignmentId) {
        return submissionRepository.findByStudentIdAndAssignmentId(studentId, assignmentId);
    }

    public Submission updateSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    public void deleteSubmission(Long id) {
        submissionRepository.deleteById(id);
    }

    public Submission gradeSubmission(Long id, Integer score, String teacherComment) {
        Submission submission = submissionRepository.findById(id).orElseThrow();
        submission.setScore(score);
        submission.setTeacherComment(teacherComment);
        return submissionRepository.save(submission);
    }
}
