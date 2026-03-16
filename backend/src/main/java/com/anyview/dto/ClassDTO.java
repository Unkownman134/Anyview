package com.anyview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassDTO {
    private Long id;
    private String className;
    private String description;
    private SchoolDTO school;
    private TeacherDTO teacher;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Data
    public static class SchoolDTO {
        private Long id;
        private String name;
        private String code;
        private String description;
        private Boolean enabled;
    }

    @Data
    public static class TeacherDTO {
        private Long id;
        private String username;
        private String realName;
        private String email;
        private String role;
    }
}