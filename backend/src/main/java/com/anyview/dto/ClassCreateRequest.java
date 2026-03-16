package com.anyview.dto;

import lombok.Data;

@Data
public class ClassCreateRequest {
    private Long schoolId;
    private Long teacherId;
    private String className;
    private String description;
}
