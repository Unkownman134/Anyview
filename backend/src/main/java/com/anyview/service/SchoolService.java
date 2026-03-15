package com.anyview.service;

import com.anyview.entity.School;
import com.anyview.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolService {
    private final SchoolRepository schoolRepository;

    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }

    public List<School> getEnabledSchools() {
        return schoolRepository.findByEnabledTrue();
    }

    public School getSchoolById(Long id) {
        return schoolRepository.findById(id).orElse(null);
    }

    @Transactional
    public School createSchool(School school) {
        if (schoolRepository.existsByCode(school.getCode())) {
            throw new RuntimeException("学校代码已存在");
        }
        if (schoolRepository.existsByName(school.getName())) {
            throw new RuntimeException("学校名称已存在");
        }
        return schoolRepository.save(school);
    }

    @Transactional
    public School updateSchool(Long id, School school) {
        School existingSchool = schoolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("学校不存在"));
        
        if (!existingSchool.getCode().equals(school.getCode()) && 
            schoolRepository.existsByCode(school.getCode())) {
            throw new RuntimeException("学校代码已存在");
        }
        
        if (!existingSchool.getName().equals(school.getName()) && 
            schoolRepository.existsByName(school.getName())) {
            throw new RuntimeException("学校名称已存在");
        }
        
        existingSchool.setName(school.getName());
        existingSchool.setCode(school.getCode());
        existingSchool.setDescription(school.getDescription());
        
        return schoolRepository.save(existingSchool);
    }

    @Transactional
    public void deleteSchool(Long id) {
        schoolRepository.deleteById(id);
    }

    @Transactional
    public School enableSchool(Long id, Boolean enabled) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("学校不存在"));
        school.setEnabled(enabled);
        return schoolRepository.save(school);
    }
}
