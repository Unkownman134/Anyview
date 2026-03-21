package com.anyview.repository;

import com.anyview.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByClassInfoId(Long classId);
    List<Assignment> findByTeacherId(Long teacherId);
    List<Assignment> findByPublishedTrue();
    long countByTeacherId(Long teacherId);
    
    @Query("SELECT a FROM Assignment a LEFT JOIN FETCH a.assignmentQuestions WHERE a.id = :id")
    Optional<Assignment> findByIdWithQuestions(@org.springframework.data.repository.query.Param("id") Long id);
    
    @Query("SELECT DISTINCT a FROM Assignment a LEFT JOIN FETCH a.assignmentQuestions WHERE a.published = true")
    List<Assignment> findPublishedWithQuestions();
    
    @Query("SELECT DISTINCT a FROM Assignment a LEFT JOIN FETCH a.assignmentQuestions")
    List<Assignment> findAllWithQuestions();
    
    @Query("SELECT DISTINCT a FROM Assignment a LEFT JOIN FETCH a.assignmentQuestions WHERE a.classId = :classId")
    List<Assignment> findByClassInfoIdWithQuestions(@org.springframework.data.repository.query.Param("classId") Long classId);
}
