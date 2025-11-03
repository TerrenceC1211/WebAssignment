package com.CwY.WebAssignment.Controller;

import com.CwY.WebAssignment.dto.AssignmentUpdateForm;
import com.CwY.WebAssignment.model.Assignment;
import com.CwY.WebAssignment.model.User;
import com.CwY.WebAssignment.service.AssignmentService;
import com.CwY.WebAssignment.service.SubmissionService;
import com.CwY.WebAssignment.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/lecturer/assignments")
@PreAuthorize("hasRole('LECTURER')")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final UserService userService;
    private final SubmissionService submissionService;

    @GetMapping
    public String showAssignments(Model model, Principal principal) {
        User lecturer = getCurrentUser(principal);

        if (!model.containsAttribute("assignment")) {
            model.addAttribute("assignment", new Assignment());
        }

        model.addAttribute("assignments", assignmentService.getAssignmentsForLecturer(lecturer));
        return "lecturer-assignment";
    }

    @GetMapping("/manage")
    public String manageAssignments(Model model, Principal principal) {
        User lecturer = getCurrentUser(principal);
        List<Assignment> assignments = assignmentService.getAssignmentsForLecturer(lecturer);
        model.addAttribute("assignments", assignments);
        Map<Long, Long> submissionCounts = assignments.stream()
                .collect(Collectors.toMap(Assignment::getId,
                        assignment -> submissionService.countSubmissionsForAssignment(assignment)));
        model.addAttribute("submissionCounts", submissionCounts);
        return "lecturer-manage-assignments";
    }


    @PostMapping
    public String createAssignment(@Valid @ModelAttribute("assignment") Assignment assignment,
                                   BindingResult bindingResult,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        User lecturer = getCurrentUser(principal);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.assignment", bindingResult);
            redirectAttributes.addFlashAttribute("assignment", assignment);
            return "redirect:/lecturer/assignments";
        }

        assignmentService.createAssignment(assignment, lecturer);
        redirectAttributes.addFlashAttribute("successMessage", "Assignment created successfully.");
        return "redirect:/lecturer/assignments";
    }

    @PostMapping("/{assignmentId}/update")
    public String updateAssignment(@PathVariable Long assignmentId,
                                   @Valid @ModelAttribute AssignmentUpdateForm assignmentForm,
                                   BindingResult bindingResult,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please correct the highlighted errors and try again.");
            return "redirect:/lecturer/assignments/manage";
        }

        if (!StringUtils.hasText(assignmentForm.getTitle())
                || !StringUtils.hasText(assignmentForm.getDescription())
                || assignmentForm.getDueDate() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "All fields are required to update an assignment.");
            return "redirect:/lecturer/assignments/manage";
        }

        User lecturer = getCurrentUser(principal);

        try {
            assignmentService.updateAssignment(assignmentId, lecturer, assignmentForm);
            redirectAttributes.addFlashAttribute("successMessage", "Assignment updated successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/lecturer/assignments/manage";
    }

    @PostMapping("/{assignmentId}/delete")
    public String deleteAssignment(@PathVariable Long assignmentId,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        User lecturer = getCurrentUser(principal);

        try {
            assignmentService.deleteAssignment(assignmentId, lecturer);
            redirectAttributes.addFlashAttribute("successMessage", "Assignment removed successfully.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/lecturer/assignments/manage";
    }

    private User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}