package com.CwY.WebAssignment.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentController {

    @GetMapping("/student/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    public String showStudentDashboard() {
        return "student-dashboard";
    }
}