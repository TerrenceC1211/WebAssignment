package com.CwY.WebAssignment.service;

import com.CwY.WebAssignment.model.Assignment;
import com.CwY.WebAssignment.model.User;
import com.CwY.WebAssignment.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public Assignment createAssignment(Assignment assignment, User createdBy) {
        assignment.setCreatedBy(createdBy);
        return assignmentRepository.save(assignment);
    }

    public List<Assignment> getAssignmentsForLecturer(User lecturer) {
        return assignmentRepository.findByCreatedByOrderByDueDateAsc(lecturer);
    }
}