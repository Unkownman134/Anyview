package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.dto.ClassCreateRequest;
import com.anyview.dto.ClassDTO;
import com.anyview.entity.ClassInfo;
import com.anyview.entity.ClassStudent;
import com.anyview.entity.School;
import com.anyview.entity.User;
import com.anyview.service.ClassService;
import com.anyview.util.ClassMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClassController {
    private final ClassService classService;

    @GetMapping
    public ApiResponse<List<ClassDTO>> getClasses() {
        try {
            List<ClassInfo> classes = classService.getClasses();
            List<ClassDTO> classDTOs = classes.stream()
                    .map(ClassMapper::toDTO)
                    .collect(Collectors.toList());
            return ApiResponse.success(classDTOs);
        } catch (Exception e) {
            return ApiResponse.error("获取班级列表失败：" + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<ClassDTO> createClass(@RequestBody ClassCreateRequest request) {
        try {
            ClassInfo classInfo = new ClassInfo();
            classInfo.setClassName(request.getClassName());
            classInfo.setDescription(request.getDescription());
            
            if (request.getSchoolId() != null) {
                School school = new School();
                school.setId(request.getSchoolId());
                classInfo.setSchool(school);
            }
            
            if (request.getTeacherId() != null) {
                User teacher = new User();
                teacher.setId(request.getTeacherId());
                classInfo.setTeacher(teacher);
            }
            
            ClassInfo created = classService.createClass(classInfo);
            return ApiResponse.success("班级创建成功", ClassMapper.toDTO(created));
        } catch (Exception e) {
            return ApiResponse.error("创建班级失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<ClassDTO> getClassById(@PathVariable Long id) {
        try {
            ClassInfo classInfo = classService.getClassById(id);
            return ApiResponse.success(ClassMapper.toDTO(classInfo));
        } catch (Exception e) {
            return ApiResponse.error("获取班级信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/teacher/{teacherId}")
    public ApiResponse<List<ClassDTO>> getClassesByTeacher(@PathVariable Long teacherId) {
        try {
            List<ClassInfo> classes = classService.getClassesByTeacher(teacherId);
            List<ClassDTO> classDTOs = classes.stream()
                    .map(ClassMapper::toDTO)
                    .collect(Collectors.toList());
            return ApiResponse.success(classDTOs);
        } catch (Exception e) {
            return ApiResponse.error("获取班级列表失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<ClassDTO> updateClass(@PathVariable Long id, @RequestBody ClassInfo classInfo) {
        try {
            classInfo.setId(id);
            ClassInfo updated = classService.updateClass(classInfo);
            return ApiResponse.success("班级更新成功", ClassMapper.toDTO(updated));
        } catch (Exception e) {
            return ApiResponse.error("更新班级失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteClass(@PathVariable Long id) {
        try {
            classService.deleteClass(id);
            return ApiResponse.success("班级删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error("删除班级失败：" + e.getMessage());
        }
    }

    @PostMapping("/{classId}/students/{studentId}")
    public ApiResponse<Void> addStudentToClass(@PathVariable Long classId, @PathVariable Long studentId) {
        try {
            classService.addStudentToClass(classId, studentId);
            return ApiResponse.success("学生添加成功", null);
        } catch (Exception e) {
            return ApiResponse.error("添加学生失败：" + e.getMessage());
        }
    }

    @PostMapping("/{classId}/students/batch")
    public ApiResponse<Void> addStudentsToClass(@PathVariable Long classId, @RequestBody List<Long> studentIds) {
        try {
            for (Long studentId : studentIds) {
                try {
                    classService.addStudentToClass(classId, studentId);
                } catch (Exception ignored) {
                }
            }
            return ApiResponse.success("学生添加成功", null);
        } catch (Exception e) {
            return ApiResponse.error("添加学生失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{classId}/students/{studentId}")
    public ApiResponse<Void> removeStudentFromClass(@PathVariable Long classId, @PathVariable Long studentId) {
        try {
            classService.removeStudentFromClass(classId, studentId);
            return ApiResponse.success("学生移除成功", null);
        } catch (Exception e) {
            return ApiResponse.error("移除学生失败：" + e.getMessage());
        }
    }

    @GetMapping("/{classId}/students")
    public ApiResponse<List<ClassStudent>> getStudentsByClass(@PathVariable Long classId) {
        try {
            List<ClassStudent> students = classService.getStudentsByClass(classId);
            return ApiResponse.success(students);
        } catch (Exception e) {
            return ApiResponse.error("获取学生列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse<List<ClassStudent>> getClassesByStudent(@PathVariable Long studentId) {
        try {
            List<ClassStudent> classes = classService.getClassesByStudent(studentId);
            return ApiResponse.success(classes);
        } catch (Exception e) {
            return ApiResponse.error("获取班级列表失败：" + e.getMessage());
        }
    }
}
