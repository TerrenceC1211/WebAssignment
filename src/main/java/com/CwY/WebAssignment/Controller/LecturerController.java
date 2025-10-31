package com.CwY.WebAssignment.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LecturerController {

    @GetMapping("/lecturer/dashboard")
    @PreAuthorize("hasRole('LECTURER')")
    // Step 5: Guard this controller method so only lecturers can access the dashboard
    public String showLecturerDashboard() {
        return "lecturer-dashboard";
    }
}