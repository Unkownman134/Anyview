package com.anyview.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private String ip;
    private String userAgent;
}
