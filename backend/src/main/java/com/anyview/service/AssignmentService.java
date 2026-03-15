package com.anyview.service;

import com.anyview.entity.Assignment;
import com.anyview.entity.AssignmentQuestion;
import com.anyview.entity.Question;
import com.anyview.repository.AssignmentRepository;
import com.anyview.repository.QuestionRepository;
import com.anyview.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final QuestionRepository questionRepository;
    private final RedisUtil redisUtil;
    
    private static final String ASSIGNMENT_PREFIX = "assignment:";
    private static final String ASSIGNMENTS_LIST_PREFIX = "assignments:";
    private static final long CACHE_TIMEOUT = 30;

    public List<Assignment> getAssignments() {
        String key = ASSIGNMENTS_LIST_PREFIX + "all";
        Object cachedAssignments = redisUtil.get(key);
        
        if (cachedAssignments instanceof List) {
            return (List<Assignment>) cachedAssignments;
        }
        
        List<Assignment> assignments = assignmentRepository.findAll();
        redisUtil.set(key, assignments, CACHE_TIMEOUT, TimeUnit.MINUTES);
        
        return assignments;
    }

    public Assignment createAssignment(Assignment assignment) {
        Assignment savedAssignment = assignmentRepository.save(assignment);
        
        // 清除相关缓存
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "all");
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "teacher:" + assignment.getTeacher().getId());
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "class:" + assignment.getClassInfo().getId());
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "published");
        
        return savedAssignment;
    }

    public Assignment getAssignmentById(Long id) {
        String key = ASSIGNMENT_PREFIX + id;
        Object cachedAssignment = redisUtil.get(key);
        
        if (cachedAssignment instanceof Assignment) {
            return (Assignment) cachedAssignment;
        }
        
        Assignment assignment = assignmentRepository.findById(id).orElse(null);
        if (assignment != null) {
            redisUtil.set(key, assignment, CACHE_TIMEOUT, TimeUnit.MINUTES);
        }
        
        return assignment;
    }

    public List<Assignment> getAssignmentsByClassId(Long classId) {
        String key = ASSIGNMENTS_LIST_PREFIX + "class:" + classId;
        Object cachedAssignments = redisUtil.get(key);
        
        if (cachedAssignments instanceof List) {
            return (List<Assignment>) cachedAssignments;
        }
        
        List<Assignment> assignments = assignmentRepository.findByClassInfoId(classId);
        redisUtil.set(key, assignments, CACHE_TIMEOUT, TimeUnit.MINUTES);
        
        return assignments;
    }

    public List<Assignment> getAssignmentsByTeacher(Long teacherId) {
        String key = ASSIGNMENTS_LIST_PREFIX + "teacher:" + teacherId;
        Object cachedAssignments = redisUtil.get(key);
        
        if (cachedAssignments instanceof List) {
            return (List<Assignment>) cachedAssignments;
        }
        
        List<Assignment> assignments = assignmentRepository.findByTeacherId(teacherId);
        redisUtil.set(key, assignments, CACHE_TIMEOUT, TimeUnit.MINUTES);
        
        return assignments;
    }

    public List<Assignment> getPublishedAssignments() {
        String key = ASSIGNMENTS_LIST_PREFIX + "published";
        Object cachedAssignments = redisUtil.get(key);
        
        if (cachedAssignments instanceof List) {
            return (List<Assignment>) cachedAssignments;
        }
        
        List<Assignment> assignments = assignmentRepository.findByPublishedTrue();
        redisUtil.set(key, assignments, CACHE_TIMEOUT, TimeUnit.MINUTES);
        
        return assignments;
    }

    public Assignment updateAssignment(Assignment assignment) {
        Assignment updatedAssignment = assignmentRepository.save(assignment);
        
        // 更新缓存
        String key = ASSIGNMENT_PREFIX + assignment.getId();
        redisUtil.set(key, updatedAssignment, CACHE_TIMEOUT, TimeUnit.MINUTES);
        
        // 清除相关列表缓存
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "all");
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "teacher:" + assignment.getTeacher().getId());
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "class:" + assignment.getClassInfo().getId());
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "published");
        
        return updatedAssignment;
    }

    public void deleteAssignment(Long id) {
        Assignment assignment = assignmentRepository.findById(id).orElse(null);
        if (assignment != null) {
            assignmentRepository.deleteById(id);
            
            // 删除缓存
            String key = ASSIGNMENT_PREFIX + id;
            redisUtil.delete(key);
            
            // 清除相关列表缓存
            redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "all");
            redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "teacher:" + assignment.getTeacher().getId());
            redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "class:" + assignment.getClassInfo().getId());
            redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "published");
        }
    }

    @Transactional
    public Assignment createAssignmentWithQuestions(Assignment assignment, List<AssignmentQuestion> questions) {
        Assignment savedAssignment = assignmentRepository.save(assignment);
        for (AssignmentQuestion aq : questions) {
            aq.setAssignment(savedAssignment);
            Question question = questionRepository.findById(aq.getQuestion().getId()).orElseThrow();
            aq.setQuestion(question);
        }
        savedAssignment.setAssignmentQuestions(questions);
        Assignment result = assignmentRepository.save(savedAssignment);
        
        // 清除相关缓存
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "all");
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "teacher:" + assignment.getTeacher().getId());
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "class:" + assignment.getClassInfo().getId());
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "published");
        
        return result;
    }

    public void publishAssignment(Long id) {
        Assignment assignment = assignmentRepository.findById(id).orElseThrow();
        assignment.setPublished(true);
        assignmentRepository.save(assignment);
        
        // 更新缓存
        String key = ASSIGNMENT_PREFIX + id;
        redisUtil.set(key, assignment, CACHE_TIMEOUT, TimeUnit.MINUTES);
        
        // 清除相关列表缓存
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "all");
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "teacher:" + assignment.getTeacher().getId());
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "class:" + assignment.getClassInfo().getId());
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "published");
    }
}
