package com.anyview.service;

import com.anyview.dto.SubmissionGradeResponse;
import com.anyview.entity.Assignment;
import com.anyview.entity.GradeStatus;
import com.anyview.entity.Question;
import com.anyview.entity.Submission;
import com.anyview.entity.User;
import com.anyview.repository.QuestionRepository;
import com.anyview.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final QuestionRepository questionRepository;
    private final AIGradingService aiGradingService;

    public List<Submission> getSubmissions() {
        return submissionRepository.findAllWithDetails();
    }

    public Submission createSubmission(Submission submission) {
        if (submission.getTeacherComment() == null) {
            submission.setTeacherComment("");
        }
        
        // 检查学生是否已经提交过该作业的该题目
        if (submission.getAssignmentId() != null && submission.getStudentId() != null && submission.getQuestionId() != null) {
            List<Submission> existingSubmissions = submissionRepository.findByStudentIdAndQuestionId(
                submission.getStudentId(), 
                submission.getQuestionId()
            );
            
            if (!existingSubmissions.isEmpty()) {
                throw new RuntimeException("该题目只能提交一次，您已经提交过了");
            }
        }
        
        // 创建关联对象以确保外键正确设置
        if (submission.getAssignmentId() != null && submission.getAssignment() == null) {
            Assignment assignment = new Assignment();
            assignment.setId(submission.getAssignmentId());
            submission.setAssignment(assignment);
        }
        
        if (submission.getQuestionId() != null && submission.getQuestion() == null) {
            Question question = new Question();
            question.setId(submission.getQuestionId());
            submission.setQuestion(question);
        }
        
        if (submission.getStudentId() != null && submission.getStudent() == null) {
            User student = new User();
            student.setId(submission.getStudentId());
            submission.setStudent(student);
        }
        
        Submission saved = submissionRepository.save(submission);
        
        if (saved.getQuestionId() != null) {
            Optional<Question> questionOpt = questionRepository.findById(saved.getQuestionId());
            if (questionOpt.isPresent()) {
                Question question = questionOpt.get();
                saved.setQuestion(question);
                
                if ("programming".equals(question.getType())) {
                    saved.setGradeStatus(GradeStatus.UNGRADED);
                    saved.setStatus(com.anyview.entity.SubmissionStatus.PENDING);
                    saved = submissionRepository.save(saved);
                    gradeSubmissionAsync(saved);
                } else if ("single".equals(question.getType())) {
                    String studentAnswer = saved.getCode();
                    String correctAnswer = question.getAnswer();
                    
                    if (correctAnswer != null && correctAnswer.trim().equals(studentAnswer.trim())) {
                        saved.setScore(question.getScore());
                        saved.setGradeStatus(GradeStatus.GRADED);
                        saved.setTeacherComment("回答正确");
                    } else {
                        saved.setScore(0);
                        saved.setGradeStatus(GradeStatus.GRADED);
                        saved.setTeacherComment("回答错误。正确答案是：" + correctAnswer);
                    }
                    saved.setStatus(com.anyview.entity.SubmissionStatus.ACCEPTED);
                    saved = submissionRepository.save(saved);
                } else if ("multiple".equals(question.getType())) {
                    String studentAnswer = saved.getCode();
                    String correctAnswer = question.getAnswer();
                    
                    if (correctAnswer != null && studentAnswer != null) {
                        String[] studentOptions = studentAnswer.split(",");
                        String[] correctOptions = correctAnswer.split(",");
                        
                        java.util.Arrays.sort(studentOptions);
                        java.util.Arrays.sort(correctOptions);
                        
                        if (java.util.Arrays.equals(studentOptions, correctOptions)) {
                            saved.setScore(question.getScore());
                            saved.setGradeStatus(GradeStatus.GRADED);
                            saved.setTeacherComment("回答正确");
                        } else {
                            saved.setScore(0);
                            saved.setGradeStatus(GradeStatus.GRADED);
                            saved.setTeacherComment("回答错误。正确答案是：" + correctAnswer);
                        }
                    } else {
                        saved.setScore(0);
                        saved.setGradeStatus(GradeStatus.GRADED);
                        saved.setTeacherComment("回答错误。正确答案是：" + correctAnswer);
                    }
                    saved.setStatus(com.anyview.entity.SubmissionStatus.ACCEPTED);
                    saved = submissionRepository.save(saved);
                } else if ("fill".equals(question.getType())) {
                    saved.setGradeStatus(GradeStatus.UNGRADED);
                    saved.setStatus(com.anyview.entity.SubmissionStatus.PENDING);
                    saved = submissionRepository.save(saved);
                } else {
                    saved.setGradeStatus(GradeStatus.UNGRADED);
                    saved.setStatus(com.anyview.entity.SubmissionStatus.PENDING);
                    saved = submissionRepository.save(saved);
                    aiGradingService.gradeSubmission(saved);
                }
            }
        }
        
        return saved;
    }

    @Async
    public void gradeSubmissionAsync(Submission submission) {
        try {
            aiGradingService.gradeSubmission(submission);
        } catch (Exception e) {
            System.err.println("异步评分失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Submission getSubmissionById(Long id) {
        return submissionRepository.findById(id).orElse(null);
    }
    
    public Submission getSubmissionByIdWithDetails(Long id) {
        return submissionRepository.findAllWithDetails().stream()
            .filter(s -> s.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public List<Submission> getSubmissionsByStudent(Long studentId) {
        return submissionRepository.findByStudentIdWithDetails(studentId);
    }

    public List<Submission> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentIdWithDetails(assignmentId);
    }

    public List<Submission> getSubmissionsByQuestion(Long questionId) {
        return submissionRepository.findByQuestionId(questionId);
    }

    public List<Submission> getSubmissionsByStudentAndAssignment(Long studentId, Long assignmentId) {
        return submissionRepository.findByStudentIdAndAssignmentIdWithDetails(studentId, assignmentId);
    }

    public Submission updateSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    public void deleteSubmission(Long id) {
        submissionRepository.deleteById(id);
    }

    public SubmissionGradeResponse gradeSubmission(Long id, Integer score, String teacherComment) {
        Submission submission = getSubmissionByIdWithDetails(id);
        
        if (submission == null) {
            throw new IllegalArgumentException("提交记录不存在");
        }
        
        if (teacherComment == null || teacherComment.trim().isEmpty()) {
            throw new IllegalArgumentException("评语不能为空");
        }
        
        Question question = submission.getQuestion();
        int maxScore = question != null ? question.getScore() : 100;
        
        if (score < 0 || score > maxScore) {
            throw new IllegalArgumentException("分数必须在0到" + maxScore + "之间");
        }
        
        submission.setScore(score);
        submission.setTeacherComment(teacherComment);
        submission.setGradeStatus(GradeStatus.GRADED);
        Submission saved = submissionRepository.saveAndFlush(submission);
        
        SubmissionGradeResponse response = new SubmissionGradeResponse();
        response.setId(saved.getId());
        response.setStudentId(saved.getStudentId());
        if (saved.getStudent() != null) {
            response.setStudentName(saved.getStudent().getUsername());
        }
        response.setAssignmentId(saved.getAssignmentId());
        if (saved.getAssignment() != null) {
            response.setAssignmentTitle(saved.getAssignment().getTitle());
        }
        response.setQuestionId(saved.getQuestionId());
        if (saved.getQuestion() != null) {
            response.setQuestionTitle(saved.getQuestion().getTitle());
        }
        response.setCode(saved.getCode());
        response.setScore(saved.getScore());
        response.setTeacherComment(saved.getTeacherComment());
        response.setGradeStatus(saved.getGradeStatus());
        response.setCreatedAt(saved.getCreatedAt());
        response.setUpdatedAt(saved.getUpdatedAt());
        return response;
    }
}
