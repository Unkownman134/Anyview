package com.anyview.util;

import com.anyview.dto.ClassDTO;
import com.anyview.entity.ClassInfo;

public class ClassMapper {
    public static ClassDTO toDTO(ClassInfo classInfo) {
        if (classInfo == null) {
            return null;
        }
        
        ClassDTO dto = new ClassDTO();
        dto.setId(classInfo.getId());
        dto.setClassName(classInfo.getClassName());
        dto.setDescription(classInfo.getDescription());
        dto.setCreatedAt(classInfo.getCreatedAt());
        dto.setUpdatedAt(classInfo.getUpdatedAt());
        
        if (classInfo.getSchool() != null) {
            ClassDTO.SchoolDTO schoolDTO = new ClassDTO.SchoolDTO();
            schoolDTO.setId(classInfo.getSchool().getId());
            schoolDTO.setName(classInfo.getSchool().getName());
            schoolDTO.setCode(classInfo.getSchool().getCode());
            schoolDTO.setDescription(classInfo.getSchool().getDescription());
            schoolDTO.setEnabled(classInfo.getSchool().getEnabled());
            dto.setSchool(schoolDTO);
        }
        
        if (classInfo.getTeacher() != null) {
            ClassDTO.TeacherDTO teacherDTO = new ClassDTO.TeacherDTO();
            teacherDTO.setId(classInfo.getTeacher().getId());
            teacherDTO.setUsername(classInfo.getTeacher().getUsername());
            teacherDTO.setRealName(classInfo.getTeacher().getRealName());
            teacherDTO.setEmail(classInfo.getTeacher().getEmail());
            teacherDTO.setRole(classInfo.getTeacher().getRole());
            dto.setTeacher(teacherDTO);
        }
        
        return dto;
    }
}