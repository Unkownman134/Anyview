package com.anyview.service;

import com.anyview.dto.ApiConfigRequest;
import com.anyview.entity.ApiConfig;
import com.anyview.repository.ApiConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiConfigService {
    private final ApiConfigRepository apiConfigRepository;

    public List<ApiConfig> getAllApiConfigs() {
        return apiConfigRepository.findAll();
    }

    public List<ApiConfig> getEnabledApiConfigs() {
        return apiConfigRepository.findByEnabledTrue();
    }

    public ApiConfig getApiConfigById(Long id) {
        return apiConfigRepository.findById(id).orElse(null);
    }

    public Optional<ApiConfig> getApiConfigByName(String name) {
        return apiConfigRepository.findByName(name);
    }

    public Optional<ApiConfig> getActiveApiConfig() {
        return apiConfigRepository.findFirstByEnabledTrueOrderByUpdatedAtDesc();
    }

    public ApiConfig createApiConfig(ApiConfigRequest request) {
        ApiConfig config = new ApiConfig();
        config.setName(request.getName());
        config.setProvider(request.getProvider());
        config.setApiKey(request.getApiKey());
        config.setApiUrl(request.getApiUrl());
        config.setModel(request.getModel());
        config.setMaxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : 2000);
        config.setTemperature(request.getTemperature() != null ? request.getTemperature() : 0.7);
        config.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        return apiConfigRepository.save(config);
    }

    public ApiConfig updateApiConfig(Long id, ApiConfigRequest request) {
        ApiConfig config = apiConfigRepository.findById(id).orElseThrow();
        config.setName(request.getName());
        config.setProvider(request.getProvider());
        config.setApiKey(request.getApiKey());
        config.setApiUrl(request.getApiUrl());
        config.setModel(request.getModel());
        if (request.getMaxTokens() != null) {
            config.setMaxTokens(request.getMaxTokens());
        }
        if (request.getTemperature() != null) {
            config.setTemperature(request.getTemperature());
        }
        if (request.getEnabled() != null) {
            config.setEnabled(request.getEnabled());
        }
        return apiConfigRepository.save(config);
    }

    public void deleteApiConfig(Long id) {
        apiConfigRepository.deleteById(id);
    }

    public ApiConfig toggleApiConfig(Long id) {
        ApiConfig config = apiConfigRepository.findById(id).orElseThrow();
        config.setEnabled(!config.getEnabled());
        return apiConfigRepository.save(config);
    }
}
