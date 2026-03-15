package com.anyview.repository;

import com.anyview.entity.ClassStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {
    List<ClassStudent> findByClassInfoId(Long classId);
    List<ClassStudent> findByStudentId(Long studentId);
    Optional<ClassStudent> findByClassInfoIdAndStudentId(Long classId, Long studentId);
    boolean existsByClassInfoIdAndStudentId(Long classId, Long studentId);
}
