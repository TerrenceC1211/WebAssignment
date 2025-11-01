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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('LECTURER')")
public class GradingController {

    private final SubmissionService submissionService;
    private final AssignmentService assignmentService;
    private final UserService userService;

    @GetMapping("/lecturer/assignments/{assignmentId}/submissions")
    public String viewSubmissions(@PathVariable Long assignmentId,
                                  Principal principal,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        User lecturer = getCurrentUser(principal);
        try {
            Assignment assignment = assignmentService.getAssignmentForLecturer(assignmentId, lecturer);
            List<Submission> submissions = submissionService.getSubmissionsForAssignment(assignmentId, lecturer);

            model.addAttribute("assignment", assignment);
            model.addAttribute("submissions", submissions);

            return "lecturer-submission";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/lecturer/assignments/manage";
        }
    }

    @PostMapping("/lecturer/submissions/{submissionId}/grade")
    public String gradeSubmission(@PathVariable Long submissionId,
                                  @RequestParam Long assignmentId,
                                  @RequestParam Double grade,
                                  @RequestParam(required = false) String feedback,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        User lecturer = getCurrentUser(principal);
        try {
            submissionService.gradeSubmission(submissionId, lecturer, grade, feedback);
            redirectAttributes.addFlashAttribute("successMessage", "Submission graded successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/lecturer/assignments/" + assignmentId + "/submissions";
    }

    private User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Username not found"));
    }
}