package com.CwY.WebAssignment.service;

import com.CwY.WebAssignment.dto.AssignmentUpdateForm;
import com.CwY.WebAssignment.model.Assignment;
import com.CwY.WebAssignment.model.User;
import com.CwY.WebAssignment.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final NotificationService notificationService;


    public Assignment createAssignment(Assignment assignment, User createdBy) {
        assignment.setCreatedBy(createdBy);
        Assignment savedAssignment = assignmentRepository.save(assignment);
        notificationService.notifyAssignmentCreated(savedAssignment);
        return savedAssignment;
    }


    public List<Assignment> getAssignmentsForLecturer(User lecturer) {
        return assignmentRepository.findByCreatedByOrderByDueDateAsc(lecturer);
    }

    public Assignment getAssignmentForLecturer(Long assignmentId, User lecturer) {
        return assignmentRepository.findByIdAndCreatedBy(assignmentId, lecturer)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found or access denied."));
    }



    public List<Assignment> getAllAssignmentsOrdered() {
        return assignmentRepository.findAll(Sort.by(Sort.Direction.ASC, "dueDate"));
    }


    public Assignment updateAssignment(Long assignmentId, User lecturer, AssignmentUpdateForm form) {
        Assignment assignment = assignmentRepository.findByIdAndCreatedBy(assignmentId, lecturer)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found or access denied."));

        assignment.setTitle(form.getTitle());
        assignment.setDescription(form.getDescription());
        assignment.setDueDate(form.getDueDate());

        return assignmentRepository.save(assignment);
    }
}