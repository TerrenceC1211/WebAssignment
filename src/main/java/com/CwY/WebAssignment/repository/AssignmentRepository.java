package com.CwY.WebAssignment.repository;

import com.CwY.WebAssignment.model.Assignment;
import com.CwY.WebAssignment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCreatedByOrderByDueDateAsc(User createdBy);
}