package com.CwY.WebAssignment.Controller;

import com.CwY.WebAssignment.model.Assignment;
import com.CwY.WebAssignment.model.Submission;
import com.CwY.WebAssignment.model.User;
import com.CwY.WebAssignment.service.AssignmentService;
import com.CwY.WebAssignment.service.SubmissionService;
import com.CwY.WebAssignment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class StudentController {

    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;
    private final UserService userService;

    @GetMapping("/student/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    public String showStudentDashboard(Model model, Principal principal) {
        User student = getCurrentUser(principal);
        List<Assignment> assignments = assignmentService.getAllAssignmentsOrdered();
        Map<Long, Submission> submissions = submissionService.getSubmissionsForStudent(student);

        model.addAttribute("assignments", assignments);
        model.addAttribute("submissions", submissions);

        return "student-dashboard";
    }

    @PostMapping("/student/assignments/{assignmentId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public String submitAssignment(@PathVariable Long assignmentId,
                                   @RequestParam(required = false) String content,
                                   @RequestParam(required = false, name = "file") MultipartFile file,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        User student  = getCurrentUser(principal);
        try{
            submissionService.submitAssignment(assignmentId, student, content, file);
            redirectAttributes.addFlashAttribute("successMessage", "Assignment Submitted");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/dashboard";
    }

    private User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName()).orElseThrow(() -> new IllegalStateException("Username not found"));
    }
}