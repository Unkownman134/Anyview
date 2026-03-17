package com.anyview.service;

import com.anyview.entity.*;
import com.anyview.repository.AssignmentQuestionRepository;
import com.anyview.repository.AssignmentRepository;
import com.anyview.repository.QuestionRepository;
import com.anyview.repository.SubmissionRepository;
import com.anyview.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class AssignmentService {
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private AssignmentQuestionRepository assignmentQuestionRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private RedisUtil redisUtil;

    private static final String ASSIGNMENT_PREFIX = "assignment:";
    private static final String ASSIGNMENTS_LIST_PREFIX = "assignments:";
    private static final int CACHE_TIMEOUT = 10;

    public List<Assignment> getAssignments() {
        String key = ASSIGNMENTS_LIST_PREFIX + "all";
        Object cachedAssignments = redisUtil.get(key);
        if (cachedAssignments instanceof List) {
            return (List<Assignment>) cachedAssignments;
        }
        List<Assignment> assignments = assignmentRepository.findAllWithQuestions();
        redisUtil.set(key, assignments, CACHE_TIMEOUT, TimeUnit.MINUTES);
        return assignments;
    }

    public Assignment getAssignmentById(Long id) {
        String key = ASSIGNMENT_PREFIX + id;
        Object cachedAssignment = redisUtil.get(key);
        if (cachedAssignment instanceof Assignment) {
            return (Assignment) cachedAssignment;
        }
        Optional<Assignment> assignmentOptional = assignmentRepository.findByIdWithQuestions(id);
        Assignment assignment = assignmentOptional.orElse(null);
        if (assignment != null) {
            redisUtil.set(key, assignment, CACHE_TIMEOUT, TimeUnit.MINUTES);
        }
        return assignment;
    }

    public List<Assignment> getAssignmentsByTeacherId(Long teacherId) {
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
        List<Assignment> assignments = assignmentRepository.findPublishedWithQuestions();
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
        if (assignment.getTeacher() != null) {
            redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "teacher:" + assignment.getTeacher().getId());
        }
        if (assignment.getClassInfo() != null) {
            redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "class:" + assignment.getClassInfo().getId());
        }
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "published");
        return updatedAssignment;
    }

    @Transactional
    public Assignment updateAssignmentWithQuestions(Long assignmentId, Assignment assignment, List<AssignmentQuestion> questions) {
        // 先查找现有作业
        Assignment existingAssignment = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("作业不存在，ID: " + assignmentId));
        
        // 更新现有作业的字段
        existingAssignment.setTitle(assignment.getTitle());
        existingAssignment.setDescription(assignment.getDescription());
        existingAssignment.setStartTime(assignment.getStartTime());
        existingAssignment.setEndTime(assignment.getEndTime());
        existingAssignment.setTotalScore(assignment.getTotalScore());
        existingAssignment.setClassId(assignment.getClassId());
        existingAssignment.setTeacherId(assignment.getTeacherId());
        existingAssignment.setPublished(assignment.getPublished() != null ? assignment.getPublished() : existingAssignment.getPublished());
        
        // 保存更新后的作业
        Assignment updatedAssignment = assignmentRepository.save(existingAssignment);
        
        // 删除旧的题目
        assignmentQuestionRepository.deleteByAssignmentId(assignmentId);
        
        // 添加新的题目
        for (AssignmentQuestion aq : questions) {
            aq.setAssignment(updatedAssignment);
            
            Long questionId = aq.getQuestionId();
            
            if (questionId == null) {
                throw new RuntimeException("题目ID不能为空");
            }
            
            Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("题目不存在，ID: " + questionId));
            
            aq.setQuestion(question);
            aq.setQuestionTitle(question.getTitle());
            aq.setQuestionType(question.getType());
            aq.setQuestionDifficulty(question.getDifficulty());
            aq.setQuestionDescription(question.getDescription());
            aq.setQuestionOptions(question.getOptions());
            aq.setQuestionTemplateCode(question.getTemplateCode());
            aq.setQuestionSampleInput(question.getSampleInput());
            aq.setQuestionSampleOutput(question.getSampleOutput());
        }
        
        // 清空旧集合并添加新题目
        updatedAssignment.getAssignmentQuestions().clear();
        updatedAssignment.getAssignmentQuestions().addAll(questions);
        
        Assignment result = assignmentRepository.save(updatedAssignment);
        
        // 清除相关缓存
        redisUtil.delete(ASSIGNMENT_PREFIX + assignmentId);
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "all");
        if (updatedAssignment.getTeacher() != null) {
            redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "teacher:" + updatedAssignment.getTeacher().getId());
        }
        if (updatedAssignment.getClassInfo() != null) {
            redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "class:" + updatedAssignment.getClassInfo().getId());
        }
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "published");
        
        return result;
    }

    @Transactional
    public void deleteAssignment(Long id) {
        Assignment assignment = assignmentRepository.findById(id).orElse(null);
        if (assignment != null) {
            // 先删除相关的提交记录
            submissionRepository.deleteByAssignmentId(id);
            
            // 删除作业-题目关联
            assignmentQuestionRepository.deleteByAssignmentId(id);
            
            // 删除作业
            assignmentRepository.deleteById(id);
            
            // 删除缓存
            String key = ASSIGNMENT_PREFIX + id;
            redisUtil.delete(key);
            
            // 清除相关列表缓存
            redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "all");
            if (assignment.getTeacher() != null) {
                redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "teacher:" + assignment.getTeacher().getId());
            }
            if (assignment.getClassInfo() != null) {
                redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "class:" + assignment.getClassInfo().getId());
            }
            redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "published");
        }
    }

    public Assignment createAssignmentWithQuestions(Assignment assignment, List<AssignmentQuestion> questions) {
        Assignment savedAssignment = assignmentRepository.save(assignment);
        
        for (AssignmentQuestion aq : questions) {
            aq.setAssignment(savedAssignment);
            
            Long questionId = aq.getQuestionId();
            if (questionId == null) {
                throw new RuntimeException("题目ID不能为空");
            }
            
            Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("题目不存在，ID: " + questionId));
            
            aq.setQuestion(question);
            aq.setQuestionTitle(question.getTitle());
            aq.setQuestionType(question.getType());
            aq.setQuestionDifficulty(question.getDifficulty());
            aq.setQuestionDescription(question.getDescription());
            aq.setQuestionOptions(question.getOptions());
            aq.setQuestionTemplateCode(question.getTemplateCode());
            aq.setQuestionSampleInput(question.getSampleInput());
            aq.setQuestionSampleOutput(question.getSampleOutput());
        }
        
        savedAssignment.setAssignmentQuestions(questions);
        Assignment result = assignmentRepository.save(savedAssignment);
        
        // 清除相关缓存
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "all");
        if (result.getTeacher() != null) {
            redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "teacher:" + result.getTeacher().getId());
        }
        if (result.getClassInfo() != null) {
            redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "class:" + result.getClassInfo().getId());
        }
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "published");
        
        return result;
    }

    public Assignment publishAssignment(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("作业不存在，ID: " + id));
        
        assignment.setPublished(true);
        Assignment updated = assignmentRepository.save(assignment);
        
        // 清除相关缓存
        redisUtil.delete(ASSIGNMENT_PREFIX + id);
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "all");
        if (updated.getTeacher() != null) {
            redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "teacher:" + updated.getTeacher().getId());
        }
        if (updated.getClassInfo() != null) {
            redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "class:" + updated.getClassInfo().getId());
        }
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "published");
        
        return updated;
    }

    // 添加缺失的方法
    public Assignment createAssignment(Assignment assignment) {
        Assignment savedAssignment = assignmentRepository.save(assignment);
        
        // 清除相关缓存
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "all");
        if (savedAssignment.getTeacher() != null) {
            redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "teacher:" + savedAssignment.getTeacher().getId());
        }
        if (savedAssignment.getClassInfo() != null) {
            redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "class:" + savedAssignment.getClassInfo().getId());
        }
        redisUtil.delete(ASSIGNMENTS_LIST_PREFIX + "published");
        
        return savedAssignment;
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
        return getAssignmentsByTeacherId(teacherId);
    }
}
