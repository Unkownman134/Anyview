package com.anyview.dto;

import lombok.Data;

@Data
public class ApiConfigRequest {
    private String name;
    private String provider;
    private String apiKey;
    private String apiUrl;
    private String model;
    private Integer maxTokens;
    private Double temperature;
    private Boolean enabled;
}
