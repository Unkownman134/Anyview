package com.anyview.repository;

import com.anyview.entity.ClassInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<ClassInfo, Long> {
    List<ClassInfo> findByTeacherId(Long teacherId);
    
    @Query("SELECT c FROM ClassInfo c LEFT JOIN FETCH c.school LEFT JOIN FETCH c.teacher")
    List<ClassInfo> findAllWithSchoolAndTeacher();
}
