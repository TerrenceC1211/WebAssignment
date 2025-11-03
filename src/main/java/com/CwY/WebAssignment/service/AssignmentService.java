package com.CwY.WebAssignment.service;

import com.CwY.WebAssignment.dto.AssignmentUpdateForm;
import com.CwY.WebAssignment.model.Assignment;
import com.CwY.WebAssignment.model.User;
import com.CwY.WebAssignment.repository.AssignmentRepository;
import com.CwY.WebAssignment.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
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

    @Transactional
    public void deleteAssignment(Long assignmentId, User lecturer) {
        Assignment assignment = assignmentRepository.findByIdAndCreatedBy(assignmentId, lecturer)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found or access denied."));

        long submissionCount = submissionRepository.countByAssignment(assignment);
        if (submissionCount > 0) {
            log.warn("Deleting assignment {} will also remove {} submission(s).", assignmentId, submissionCount);
            submissionRepository.deleteByAssignment(assignment);
        }

        assignmentRepository.delete(assignment);
        notificationService.notifyAssignmentRemoved(assignment);
    }
}
