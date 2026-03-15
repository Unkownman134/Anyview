package com.anyview.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "assignment_questions")
@Data
public class AssignmentQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(nullable = false)
    private Integer orderIndex;

    @Column(nullable = false)
    private Integer score;
}
