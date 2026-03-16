package com.anyview.service;

import com.anyview.entity.Question;
import com.anyview.entity.Submission;
import com.anyview.entity.SubmissionStatus;
import com.anyview.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RealTimeEvaluationService {
    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private CodeEvaluationService codeEvaluationService;

    @Autowired
    private QuestionService questionService;

    @Transactional
    public void evaluateSubmission(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId).orElse(null);
        if (submission == null) {
            return;
        }

        try {
            submission.setStatus(SubmissionStatus.RUNNING);
            submissionRepository.save(submission);

            Question question = questionService.getQuestionById(submission.getQuestion().getId());
            CodeEvaluationService.EvaluationResult result = codeEvaluationService.evaluateSubmission(submission, question);

            submission.setStatus(result.isAllPassed() ? SubmissionStatus.ACCEPTED : SubmissionStatus.WRONG_ANSWER);
            submission.setScore(result.getScore());
            submission.setAiFeedback(generateFeedback(result));
            submissionRepository.save(submission);

        } catch (Exception e) {
            if (submission != null) {
                submission.setStatus(SubmissionStatus.RUNTIME_ERROR);
                submission.setAiFeedback("评测失败: " + e.getMessage());
                submissionRepository.save(submission);
            }
        }
    }

    private String generateFeedback(CodeEvaluationService.EvaluationResult result) {
        StringBuilder feedback = new StringBuilder();
        feedback.append("评测结果: " + (result.isAllPassed() ? "通过" : "未通过") + "\n");
        feedback.append("得分: " + result.getScore() + "分\n");
        feedback.append("通过用例: " + result.getPassed() + "/" + result.getTotal() + "\n");

        if (!result.isAllPassed()) {
            feedback.append("\n失败用例:\n");
            for (var testResult : result.getTestResults()) {
                if (!testResult.isPassed()) {
                    feedback.append("期望输出: " + testResult.getExpectedOutput() + "\n");
                    if (testResult.getActualOutput() != null) {
                        feedback.append("实际输出: " + testResult.getActualOutput() + "\n");
                    }
                    if (testResult.getMessage() != null) {
                        feedback.append("错误: " + testResult.getMessage() + "\n");
                    }
                    feedback.append("\n");
                }
            }
        }

        return feedback.toString();
    }
}
