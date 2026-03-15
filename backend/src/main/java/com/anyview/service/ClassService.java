package com.anyview.service;

import com.anyview.entity.ClassInfo;
import com.anyview.entity.ClassStudent;
import com.anyview.entity.User;
import com.anyview.repository.ClassRepository;
import com.anyview.repository.ClassStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassService {
    private final ClassRepository classRepository;
    private final ClassStudentRepository classStudentRepository;

    public ClassInfo createClass(ClassInfo classInfo) {
        return classRepository.save(classInfo);
    }

    public ClassInfo getClassById(Long id) {
        return classRepository.findById(id).orElse(null);
    }

    public List<ClassInfo> getClassesByTeacher(Long teacherId) {
        return classRepository.findByTeacherId(teacherId);
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
        classStudent.setStudent(new User(studentId));
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
}
