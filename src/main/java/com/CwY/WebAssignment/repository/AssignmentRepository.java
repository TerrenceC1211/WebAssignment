package com.CwY.WebAssignment.repository;

import com.CwY.WebAssignment.model.Assignment;
import com.CwY.WebAssignment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCreatedByOrderByDueDateAsc(User createdBy);
    Optional<Assignment> findByIdAndCreatedBy(Long id, User createdBy);
}