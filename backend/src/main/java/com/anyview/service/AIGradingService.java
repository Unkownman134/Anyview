package com.anyview.service;

import com.anyview.entity.ApiConfig;
import com.anyview.entity.Question;
import com.anyview.entity.Submission;
import com.anyview.entity.SubmissionStatus;
import com.anyview.repository.SubmissionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIGradingService {
    private final ApiConfigService apiConfigService;
    private final CodeExecutionService codeExecutionService;
    private final SubmissionRepository submissionRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public void gradeSubmission(Submission submission) {
        try {
            submission.setStatus(SubmissionStatus.JUDGING);
            
            Question question = submission.getQuestion();
            
            if ("programming".equals(question.getType())) {
                gradeProgrammingSubmission(submission, question);
            } else {
                gradeNonProgrammingSubmission(submission, question);
            }
            
        } catch (Exception e) {
            log.error("AI评分失败", e);
            submission.setStatus(SubmissionStatus.RUNTIME_ERROR);
            submission.setAiFeedback("评分失败：" + e.getMessage());
        }
    }

    private void gradeProgrammingSubmission(Submission submission, Question question) {
        try {
            String code = submission.getCode();
            String language = question.getTemplateCode() != null && question.getTemplateCode().contains("python") ? "python" : "java";
            
            CodeExecutionService.ExecutionResult executionResult = codeExecutionService.executeCode(code, language);
            
            if (!executionResult.isSuccess()) {
                if (executionResult.getError().contains("编译错误") || executionResult.getError().contains("compile")) {
                    submission.setCompileError(executionResult.getError());
                    submission.setStatus(SubmissionStatus.COMPILATION_ERROR);
                } else {
                    submission.setRuntimeError(executionResult.getError());
                    submission.setStatus(SubmissionStatus.RUNTIME_ERROR);
                }
                
                generateAIFeedback(submission, question, executionResult.getError(), true, language);
                submission.setScore(0);
                submission.setGradeStatus(com.anyview.entity.GradeStatus.GRADED);
            } else {
                submission.setStatus(SubmissionStatus.ACCEPTED);
                submission.setTestResults(executionResult.getOutput());
                
                generateAIFeedback(submission, question, executionResult.getOutput(), false, language);
                submission.setGradeStatus(com.anyview.entity.GradeStatus.GRADED);
            }
            
            submissionRepository.save(submission);
            
        } catch (Exception e) {
            log.error("代码执行失败", e);
            submission.setStatus(SubmissionStatus.RUNTIME_ERROR);
            submission.setRuntimeError(e.getMessage());
            submission.setScore(0);
            String feedback = "执行失败：" + e.getMessage();
            submission.setAiFeedback(feedback);
            submission.setTeacherComment(feedback);
            submission.setGradeStatus(com.anyview.entity.GradeStatus.GRADED);
            submissionRepository.save(submission);
        }
    }

    private void gradeNonProgrammingSubmission(Submission submission, Question question) {
        String answer = submission.getCode();
        String correctAnswer = question.getAnswer();
        
        String feedback = generateFeedbackForNonProgramming(submission, question, answer, correctAnswer);
        submission.setAiFeedback(feedback);
        
        if (correctAnswer != null && correctAnswer.equals(answer)) {
            submission.setScore(question.getScore());
            submission.setStatus(SubmissionStatus.ACCEPTED);
        } else {
            submission.setScore(0);
            submission.setStatus(SubmissionStatus.WRONG_ANSWER);
        }
    }

    private void generateAIFeedback(Submission submission, Question question, String executionOutput, boolean hasError, String language) {
        try {
            Optional<ApiConfig> apiConfigOpt = apiConfigService.getActiveApiConfig();
            if (apiConfigOpt.isEmpty()) {
                String feedback = "未配置AI评分服务";
                submission.setAiFeedback(feedback);
                submission.setTeacherComment(feedback);
                submission.setScore(0);
                return;
            }
            
            ApiConfig apiConfig = apiConfigOpt.get();
            
            String prompt = buildPrompt(submission, question, executionOutput, hasError, language);
            
            String aiResponse = callAIAPI(apiConfig, prompt);
            
            parseAIResponse(submission, aiResponse);
            
        } catch (Exception e) {
            log.error("生成AI反馈失败", e);
            String feedback = "生成反馈失败：" + e.getMessage();
            submission.setAiFeedback(feedback);
            submission.setTeacherComment(feedback);
            submission.setScore(0);
        }
    }

    private String buildPrompt(Submission submission, Question question, String executionOutput, boolean hasError, String language) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("【编程题评分任务】\n\n");
        prompt.append("题目描述：\n").append(question.getDescription()).append("\n\n");
        
        if (question.getSampleInput() != null && !question.getSampleInput().isEmpty()) {
            prompt.append("示例输入：\n").append(question.getSampleInput()).append("\n\n");
        }
        
        if (question.getSampleOutput() != null && !question.getSampleOutput().isEmpty()) {
            prompt.append("示例输出：\n").append(question.getSampleOutput()).append("\n\n");
        }
        
        prompt.append("学生代码：\n```\n").append(submission.getCode()).append("\n```\n\n");
        prompt.append("代码语言：").append(language).append("\n\n");
        
        if (hasError) {
            prompt.append("【错误信息】\n").append(executionOutput).append("\n\n");
            prompt.append("请分析这个错误，给出：\n");
            prompt.append("1. 错误原因分析\n");
            prompt.append("2. 详细的修改建议\n");
            prompt.append("3. 评分：0分\n");
        } else {
            prompt.append("【执行输出】\n").append(executionOutput).append("\n\n");
            prompt.append("请评估这段代码，给出：\n");
            prompt.append("1. 正确性分析（是否通过测试用例）\n");
            prompt.append("2. 代码质量评估（可读性、规范性）\n");
            prompt.append("3. 性能分析（时间复杂度、空间复杂度）\n");
            prompt.append("4. 改进建议\n");
            prompt.append("5. 评分：0-100分（满分100）\n");
        }
        
        prompt.append("\n请以结构化的方式输出评分结果，包含明确的分数。");
        
        return prompt.toString();
    }

    private String callAIAPI(ApiConfig apiConfig, String prompt) throws Exception {
        String apiUrl = apiConfig.getApiUrl();
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", apiConfig.getModel());
        requestBody.put("messages", new Object[]{
                Map.of("role", "user", "content", prompt)
        });
        requestBody.put("max_tokens", apiConfig.getMaxTokens());
        requestBody.put("temperature", apiConfig.getTemperature());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiConfig.getApiKey());
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                String.class
        );
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("message").path("content").asText();
            }
        }
        
        throw new RuntimeException("AI API调用失败");
    }

    private void parseAIResponse(Submission submission, String aiResponse) {
        try {
            submission.setAiFeedback(aiResponse);
            submission.setTeacherComment(aiResponse);
            
            int score = extractScoreFromResponse(aiResponse);
            submission.setScore(score);
            
        } catch (Exception e) {
            log.error("解析AI响应失败", e);
            submission.setAiFeedback(aiResponse);
            submission.setTeacherComment(aiResponse);
            submission.setScore(0);
        }
    }

    private int extractScoreFromResponse(String response) {
        try {
            String[] patterns = {
                    "评分[:：]\\s*(\\d+)",
                    "分数[:：]\\s*(\\d+)",
                    "score[:：]\\s*(\\d+)",
                    "得分[:：]\\s*(\\d+)"
            };
            
            for (String pattern : patterns) {
                java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
                java.util.regex.Matcher m = p.matcher(response);
                if (m.find()) {
                    int score = Integer.parseInt(m.group(1));
                    return Math.min(100, Math.max(0, score));
                }
            }
            
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private String generateFeedbackForNonProgramming(Submission submission, Question question, String studentAnswer, String correctAnswer) {
        if (correctAnswer != null && correctAnswer.equals(studentAnswer)) {
            return "回答正确！得分：" + question.getScore();
        } else {
            return "回答错误。正确答案是：" + correctAnswer + "。你的答案是：" + studentAnswer;
        }
    }
}
