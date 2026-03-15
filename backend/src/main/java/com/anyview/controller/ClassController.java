package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.entity.ClassInfo;
import com.anyview.entity.ClassStudent;
import com.anyview.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClassController {
    private final ClassService classService;

    @GetMapping
    public ApiResponse<List<ClassInfo>> getClasses() {
        try {
            List<ClassInfo> classes = classService.getClasses();
            return ApiResponse.success(classes);
        } catch (Exception e) {
            return ApiResponse.error("获取班级列表失败：" + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<ClassInfo> createClass(@RequestBody ClassInfo classInfo) {
        try {
            ClassInfo created = classService.createClass(classInfo);
            return ApiResponse.success("班级创建成功", created);
        } catch (Exception e) {
            return ApiResponse.error("创建班级失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<ClassInfo> getClassById(@PathVariable Long id) {
        try {
            ClassInfo classInfo = classService.getClassById(id);
            return ApiResponse.success(classInfo);
        } catch (Exception e) {
            return ApiResponse.error("获取班级信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/teacher/{teacherId}")
    public ApiResponse<List<ClassInfo>> getClassesByTeacher(@PathVariable Long teacherId) {
        try {
            List<ClassInfo> classes = classService.getClassesByTeacher(teacherId);
            return ApiResponse.success(classes);
        } catch (Exception e) {
            return ApiResponse.error("获取班级列表失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<ClassInfo> updateClass(@PathVariable Long id, @RequestBody ClassInfo classInfo) {
        try {
            classInfo.setId(id);
            ClassInfo updated = classService.updateClass(classInfo);
            return ApiResponse.success("班级更新成功", updated);
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
