package com.CwY.WebAssignment.repository;

import com.CwY.WebAssignment.model.Assignment;
import com.CwY.WebAssignment.model.Submission;
import com.CwY.WebAssignment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByAssignmentAndStudent(Assignment assignment, User student);
    List<Submission> findByStudent(User student);
}