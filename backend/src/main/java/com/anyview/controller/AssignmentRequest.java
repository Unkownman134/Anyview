package com.anyview.controller;

import com.anyview.entity.Assignment;
import com.anyview.entity.AssignmentQuestion;
import lombok.Data;

import java.util.List;

@Data
public class AssignmentRequest {
    private Assignment assignment;
    private List<AssignmentQuestion> questions;
}
