package com.anyview.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "assignment_questions")
public class AssignmentQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    @JsonIgnore
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    @JsonIgnore
    private Question question;

    @Column(name = "question_id")
    private Long questionId;

    @Column(nullable = false)
    private Integer orderIndex;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "question_title")
    private String questionTitle;

    @Column(name = "question_type")
    private String questionType;

    @Column(name = "question_difficulty")
    private String questionDifficulty;

    @Column(name = "question_description")
    private String questionDescription;

    @Column(name = "question_options")
    private String questionOptions;

    @Column(name = "question_template_code")
    private String questionTemplateCode;

    @Column(name = "question_sample_input")
    private String questionSampleInput;

    @Column(name = "question_sample_output")
    private String questionSampleOutput;

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionDifficulty() {
        return questionDifficulty;
    }

    public void setQuestionDifficulty(String questionDifficulty) {
        this.questionDifficulty = questionDifficulty;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public String getQuestionOptions() {
        return questionOptions;
    }

    public void setQuestionOptions(String questionOptions) {
        this.questionOptions = questionOptions;
    }

    public String getQuestionTemplateCode() {
        return questionTemplateCode;
    }

    public void setQuestionTemplateCode(String questionTemplateCode) {
        this.questionTemplateCode = questionTemplateCode;
    }

    public String getQuestionSampleInput() {
        return questionSampleInput;
    }

    public void setQuestionSampleInput(String questionSampleInput) {
        this.questionSampleInput = questionSampleInput;
    }

    public String getQuestionSampleOutput() {
        return questionSampleOutput;
    }

    public void setQuestionSampleOutput(String questionSampleOutput) {
        this.questionSampleOutput = questionSampleOutput;
    }
}