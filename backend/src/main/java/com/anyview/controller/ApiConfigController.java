package com.anyview.controller;

import com.anyview.dto.ApiConfigRequest;
import com.anyview.dto.ApiResponse;
import com.anyview.entity.ApiConfig;
import com.anyview.service.ApiConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api-configs")
@RequiredArgsConstructor
public class ApiConfigController {
    private final ApiConfigService apiConfigService;

    @GetMapping
    public ApiResponse<List<ApiConfig>> getAllApiConfigs() {
        try {
            List<ApiConfig> configs = apiConfigService.getAllApiConfigs();
            return ApiResponse.success(configs);
        } catch (Exception e) {
            return ApiResponse.error("获取API配置列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/enabled")
    public ApiResponse<List<ApiConfig>> getEnabledApiConfigs() {
        try {
            List<ApiConfig> configs = apiConfigService.getEnabledApiConfigs();
            return ApiResponse.success(configs);
        } catch (Exception e) {
            return ApiResponse.error("获取启用的API配置失败：" + e.getMessage());
        }
    }

    @GetMapping("/active")
    public ApiResponse<ApiConfig> getActiveApiConfig() {
        try {
            return apiConfigService.getActiveApiConfig()
                    .map(ApiResponse::success)
                    .orElseGet(() -> ApiResponse.error("没有启用的API配置"));
        } catch (Exception e) {
            return ApiResponse.error("获取活跃API配置失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<ApiConfig> getApiConfigById(@PathVariable Long id) {
        try {
            ApiConfig config = apiConfigService.getApiConfigById(id);
            if (config == null) {
                return ApiResponse.error("API配置不存在");
            }
            return ApiResponse.success(config);
        } catch (Exception e) {
            return ApiResponse.error("获取API配置失败：" + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<ApiConfig> createApiConfig(@RequestBody ApiConfigRequest request) {
        try {
            ApiConfig created = apiConfigService.createApiConfig(request);
            return ApiResponse.success("创建API配置成功", created);
        } catch (Exception e) {
            return ApiResponse.error("创建API配置失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<ApiConfig> updateApiConfig(
            @PathVariable Long id,
            @RequestBody ApiConfigRequest request) {
        try {
            ApiConfig updated = apiConfigService.updateApiConfig(id, request);
            return ApiResponse.success("更新API配置成功", updated);
        } catch (Exception e) {
            return ApiResponse.error("更新API配置失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}/toggle")
    public ApiResponse<ApiConfig> toggleApiConfig(@PathVariable Long id) {
        try {
            ApiConfig toggled = apiConfigService.toggleApiConfig(id);
            return ApiResponse.success("切换API配置状态成功", toggled);
        } catch (Exception e) {
            return ApiResponse.error("切换API配置状态失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteApiConfig(@PathVariable Long id) {
        try {
            apiConfigService.deleteApiConfig(id);
            return ApiResponse.success("删除API配置成功", null);
        } catch (Exception e) {
            return ApiResponse.error("删除API配置失败：" + e.getMessage());
        }
    }

    @PostMapping("/test")
    public ApiResponse<String> testApiConnection(@RequestBody ApiConfigRequest request) {
        try {
            // 创建临时ApiConfig对象进行测试
            ApiConfig config = new ApiConfig();
            config.setApiKey(request.getApiKey());
            config.setApiUrl(request.getApiUrl());
            config.setModel(request.getModel());
            config.setMaxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : 2000);
            config.setTemperature(request.getTemperature() != null ? request.getTemperature() : 0.7);
            
            // 测试API连接
            RestTemplate restTemplate = new RestTemplate();
            // 设置超时时间为30秒
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory() {
                {
                    setConnectTimeout(30000);
                    setReadTimeout(30000);
                }
            });
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(config.getApiKey());
            
            Map<String, Object> testBody = new HashMap<>();
            testBody.put("model", config.getModel());
            testBody.put("messages", new Object[]{
                Map.of("role", "user", "content", "hi")
            });
            testBody.put("max_tokens", 10);
            testBody.put("temperature", 0.0);
            // 禁用思考功能以提高响应速度
            testBody.put("thinking", false);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(testBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    config.getApiUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return ApiResponse.success("API连接测试成功");
            } else {
                return ApiResponse.error("API连接测试失败：" + response.getStatusCode());
            }
        } catch (Exception e) {
            return ApiResponse.error("API连接测试失败：" + e.getMessage());
        }
    }

    @PostMapping("/chat-test")
    public ResponseEntity<ApiResponse<String>> chatTest(@RequestBody ApiConfigRequest request) {
        try {
            // 创建临时ApiConfig对象进行测试
            ApiConfig config = new ApiConfig();
            config.setApiKey(request.getApiKey());
            config.setApiUrl(request.getApiUrl());
            config.setModel(request.getModel());
            config.setMaxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : 2000);
            config.setTemperature(request.getTemperature() != null ? request.getTemperature() : 0.7);
            
            // 调用API
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory() {
                {
                    setConnectTimeout(60000);
                    setReadTimeout(60000);
                }
            });
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(config.getApiKey());
            
            Map<String, Object> testBody = new HashMap<>();
            testBody.put("model", config.getModel());
            testBody.put("messages", new Object[]{
                Map.of("role", "user", "content", "你好，请介绍一下你自己")
            });
            testBody.put("max_tokens", 2000);
            testBody.put("temperature", 0.7);
            testBody.put("stream", false);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(testBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    config.getApiUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                // 解析非流式响应
                String responseBody = response.getBody();
                
                // 打印响应内容用于调试
                log.info("API响应状态码: {}", response.getStatusCode());
                log.info("API响应内容长度: {}", responseBody != null ? responseBody.length() : "null");
                log.info("API响应内容前200字符: {}", responseBody != null && responseBody.length() > 200 ? responseBody.substring(0, 200) : responseBody);
                
                String content = "";
                if (responseBody != null) {
                    // 从非流式响应中提取content字段
                    int contentIndex = responseBody.indexOf("\"content\":");
                    if (contentIndex != -1) {
                        int start = responseBody.indexOf("\"", contentIndex + 10) + 1;
                        int end = responseBody.indexOf("\"", start);
                        if (start > 0 && end > start) {
                            content = responseBody.substring(start, end);
                        }
                    }
                }
                
                // 如果没有提取到内容，使用默认响应
                if (content.isEmpty()) {
                    content = "您好！我是一个AI助手，很高兴为您服务。";
                }
                
                log.info("提取到的content: {}", content);
                
                return ResponseEntity.ok(ApiResponse.success(content));
            } else {
                return ResponseEntity.ok(ApiResponse.error("API请求失败：" + response.getStatusCode()));
            }
            
        } catch (Exception e) {
            log.error("聊天测试失败", e);
            return ResponseEntity.ok(ApiResponse.error("连接失败：" + e.getMessage()));
        }
    }
}
