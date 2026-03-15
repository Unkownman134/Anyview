package com.anyview.service;

import com.anyview.entity.Assignment;
import com.anyview.entity.AssignmentQuestion;
import com.anyview.entity.Question;
import com.anyview.repository.AssignmentRepository;
import com.anyview.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final QuestionRepository questionRepository;

    public List<Assignment> getAssignments() {
        return assignmentRepository.findAll();
    }

    public Assignment createAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    public Assignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id).orElse(null);
    }

    public List<Assignment> getAssignmentsByClassId(Long classId) {
        return assignmentRepository.findByClassInfoId(classId);
    }

    public List<Assignment> getAssignmentsByTeacher(Long teacherId) {
        return assignmentRepository.findByTeacherId(teacherId);
    }

    public List<Assignment> getPublishedAssignments() {
        return assignmentRepository.findByPublishedTrue();
    }

    public Assignment updateAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    public void deleteAssignment(Long id) {
        assignmentRepository.deleteById(id);
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
        return assignmentRepository.save(savedAssignment);
    }

    public void publishAssignment(Long id) {
        Assignment assignment = assignmentRepository.findById(id).orElseThrow();
        assignment.setPublished(true);
        assignmentRepository.save(assignment);
    }
}
