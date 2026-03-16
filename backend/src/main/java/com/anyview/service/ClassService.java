package com.anyview.service;

import com.anyview.entity.ClassInfo;
import com.anyview.entity.ClassStudent;
import com.anyview.entity.User;
import com.anyview.repository.ClassRepository;
import com.anyview.repository.ClassStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClassService {
    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private ClassStudentRepository classStudentRepository;

    public ClassInfo createClass(ClassInfo classInfo) {
        return classRepository.save(classInfo);
    }

    public ClassInfo getClassById(Long id) {
        return classRepository.findById(id).orElse(null);
    }

    public List<ClassInfo> getAllClasses() {
        return classRepository.findAllWithSchoolAndTeacher();
    }

    public ClassInfo updateClass(ClassInfo classInfo) {
        return classRepository.save(classInfo);
    }

    public void deleteClass(Long id) {
        classRepository.deleteById(id);
    }

    @Transactional
    public void addStudentToClass(Long classId, Long studentId) {
        if (classStudentRepository.existsByClassInfoIdAndStudentId(classId, studentId)) {
            throw new RuntimeException("Student already in class");
        }

        ClassStudent classStudent = new ClassStudent();
        classStudent.setClassInfo(classRepository.findById(classId).orElseThrow());
        User student = new User();
        student.setId(studentId);
        classStudent.setStudent(student);
        classStudentRepository.save(classStudent);
    }

    @Transactional
    public void removeStudentFromClass(Long classId, Long studentId) {
        ClassStudent classStudent = classStudentRepository.findByClassInfoId(classId).stream()
                .filter(cs -> cs.getStudent().getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Student not in class"));
        classStudentRepository.delete(classStudent);
    }

    public List<ClassStudent> getStudentsByClass(Long classId) {
        return classStudentRepository.findByClassInfoId(classId);
    }

    public List<ClassStudent> getClassesByStudent(Long studentId) {
        return classStudentRepository.findByStudentId(studentId);
    }

    // 获取所有班级（为了兼容ClassController）
    public List<ClassInfo> getClasses() {
        return getAllClasses();
    }

    // 根据教师ID获取班级（为了兼容ClassController）
    public List<ClassInfo> getClassesByTeacher(Long teacherId) {
        // 由于ClassRepository没有findByTeacherId方法，这里返回所有班级
        return getAllClasses();
    }
}
