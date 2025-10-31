package com.CwY.WebAssignment.Controller;

import com.CwY.WebAssignment.model.Assignment;
import com.CwY.WebAssignment.model.User;
import com.CwY.WebAssignment.service.AssignmentService;
import com.CwY.WebAssignment.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/lecturer/assignments")
@PreAuthorize("hasRole('LECTURER')")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final UserService userService;

    @GetMapping
    public String showAssignments(Model model, Principal principal) {
        User lecturer = getCurrentUser(principal);

        if (!model.containsAttribute("assignment")) {
            model.addAttribute("assignment", new Assignment());
        }

        model.addAttribute("assignments", assignmentService.getAssignmentsForLecturer(lecturer));
        return "lecturer-assignment";
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

    private User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}