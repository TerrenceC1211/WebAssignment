package com.CwY.WebAssignment.service;

import com.CwY.WebAssignment.model.Assignment;
import com.CwY.WebAssignment.model.Submission;
import com.CwY.WebAssignment.model.User;
import com.CwY.WebAssignment.repository.AssignmentRepository;
import com.CwY.WebAssignment.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;

    public Submission submitAssignment(Long assignmentId, User student, String content, MultipartFile file) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new IllegalArgumentException("Assignment Not Found"));

        if (assignment.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("The assignment due date has passed.");
        }

        submissionRepository.findByAssignmentAndStudent(assignment, student)
                .ifPresent(existing -> {
                    throw new IllegalStateException("You have already submitted this assignment.");
                });

        String normalizedContent = content != null ? content.trim() : null;
        boolean hasTextContent = normalizedContent != null && !normalizedContent.isEmpty();
        boolean hasFileContent = file != null && !file.isEmpty();

        if (!hasTextContent && !hasFileContent) {
            throw new IllegalArgumentException("Please provide text or attach a file before submitting.");
        }

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);

        if (hasFileContent) {
            if(file.getSize() > MAX_FILE_SIZE_BYTES) {
                throw new IllegalArgumentException("File size exceeds the allowed limit of 5 MB.");
            }
            try {
                submission.setFileName(file.getOriginalFilename());
                submission.setFileData(file.getBytes());
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to store the uploaded file.");
                }
            } else {
            submission.setFileName(null);
            submission.setFileData(null);
            }

        submission.setContent(hasTextContent ? normalizedContent : null);
        submission.setSubmittedAt(LocalDateTime.now());

        return submissionRepository.save(submission);
    }

    public Map<Long, Submission> getSubmissionsForStudent(User student) {
        List<Submission> submissions = submissionRepository.findByStudent(student);
        Map<Long, Submission> submissionsByAssignment = new HashMap<>();
        for (Submission submission : submissions) {
            submissionsByAssignment.put(submission.getAssignment().getId(), submission);
        }
        return submissionsByAssignment;
    }
}


