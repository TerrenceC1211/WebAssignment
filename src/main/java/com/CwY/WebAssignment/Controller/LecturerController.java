package com.CwY.WebAssignment.Controller;

import com.CwY.WebAssignment.dto.GradingQueueItem;
import com.CwY.WebAssignment.model.Assignment;
import com.CwY.WebAssignment.model.User;
import com.CwY.WebAssignment.service.AssignmentService;
import com.CwY.WebAssignment.service.SubmissionService;
import com.CwY.WebAssignment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class LecturerController {

    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;
    private final UserService userService;

    @GetMapping("/lecturer/dashboard")
    @PreAuthorize("hasRole('LECTURER')")
    public String showLecturerDashboard(Model model, Principal principal) {
        User lecturer = getCurrentUser(principal);

        List<GradingQueueItem> gradingQueueItems = assignmentService.getAssignmentsForLecturer(lecturer)
                .stream()
                .map(assignment -> buildQueueItem(assignment))
                .collect(Collectors.toList());

        model.addAttribute("gradingQueueItems", gradingQueueItems);
        return "lecturer-dashboard";
    }

    private GradingQueueItem buildQueueItem(Assignment assignment) {
        long totalSubmissions = submissionService.countSubmissionsForAssignment(assignment);
        long pendingCount = submissionService.countPendingSubmissionsForAssignment(assignment);
        return new GradingQueueItem(
                assignment.getId(),
                assignment.getTitle(),
                totalSubmissions,
                pendingCount
        );
    }

    private User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}