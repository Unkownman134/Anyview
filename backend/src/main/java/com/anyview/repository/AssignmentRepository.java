package com.anyview.repository;

import com.anyview.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByClassInfoId(Long classId);
    List<Assignment> findByTeacherId(Long teacherId);
    List<Assignment> findByPublishedTrue();
}
