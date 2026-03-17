package com.anyview.repository;

import com.anyview.entity.AssignmentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentQuestionRepository extends JpaRepository<AssignmentQuestion, Long> {
    
    List<AssignmentQuestion> findByAssignmentId(Long assignmentId);
    
    void deleteByAssignmentId(Long assignmentId);
}
