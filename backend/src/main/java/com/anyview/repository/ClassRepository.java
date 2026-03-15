package com.anyview.repository;

import com.anyview.entity.ClassInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<ClassInfo, Long> {
    List<ClassInfo> findByTeacherId(Long teacherId);
}
