package com.anyview.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class AiService {
    @Value("${ai.api.key:}")
    private String apiKey;

    @Value("${ai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    public String analyzeCode(String code, String language) {
        String prompt = buildCodeAnalysisPrompt(code, language);
        return callAiApi(prompt);
    }

    public String gradeCode(String code, String question, String sampleInput, String sampleOutput) {
        String prompt = buildGradingPrompt(code, question, sampleInput, sampleOutput);
        return callAiApi(prompt);
    }

    public String generateFeedback(String code, String errorType) {
        String prompt = buildFeedbackPrompt(code, errorType);
        return callAiApi(prompt);
    }

    private String buildCodeAnalysisPrompt(String code, String language) {
        return String.format(
                "请分析以下 %s 代码，给出以下信息：\n" +
                "1. 代码质量评分（1-10分）\n" +
                "2. 代码风格评价\n" +
                "3. 潜在的问题或改进建议\n" +
                "4. 时间复杂度分析\n" +
                "5. 空间复杂度分析\n\n" +
                "代码：\n%s\n\n" +
                "请以 JSON 格式返回，包含 score, style, issues, timeComplexity, spaceComplexity 字段。",
                language, code
        );
    }

    private String buildGradingPrompt(String code, String question, String sampleInput, String sampleOutput) {
        return String.format(
                "请评估以下代码是否正确解决了题目：\n\n" +
                "题目描述：\n%s\n\n" +
                "示例输入：\n%s\n\n" +
                "示例输出：\n%s\n\n" +
                "学生代码：\n%s\n\n" +
                "请分析代码的正确性，并给出评分（0-100分）和详细评语。" +
                "请以 JSON 格式返回，包含 isCorrect, score, comment 字段。",
                question, sampleInput, sampleOutput, code
        );
    }

    private String buildFeedbackPrompt(String code, String errorType) {
        return String.format(
                "学生的代码出现了 %s 错误，请给出详细的错误分析和改进建议：\n\n" +
                "代码：\n%s\n\n" +
                "请以 JSON 格式返回，包含 errorAnalysis, suggestions 字段。",
                errorType, code
        );
    }

    private String callAiApi(String prompt) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("max_tokens", 1000);
            requestBody.put("temperature", 0.7);

            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            requestBody.put("messages", messages);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            BufferedReader br;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            JSONObject jsonResponse = JSON.parseObject(response.toString());
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject messageObj = firstChoice.getJSONObject("message");
                return messageObj.getString("content");
            }

            return "AI 服务暂时不可用";
        } catch (Exception e) {
            log.error("调用 AI API 失败", e);
            return "AI 服务暂时不可用：" + e.getMessage();
        }
    }
}
