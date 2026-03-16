package com.anyview.util;

import com.anyview.dto.UserDTO;
import com.anyview.entity.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setEnabled(user.getEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        if (user.getSchool() != null) {
            UserDTO.SchoolDTO schoolDTO = new UserDTO.SchoolDTO();
            schoolDTO.setId(user.getSchool().getId());
            schoolDTO.setName(user.getSchool().getName());
            schoolDTO.setCode(user.getSchool().getCode());
            schoolDTO.setDescription(user.getSchool().getDescription());
            schoolDTO.setEnabled(user.getSchool().getEnabled());
            dto.setSchool(schoolDTO);
        }
        
        return dto;
    }
}