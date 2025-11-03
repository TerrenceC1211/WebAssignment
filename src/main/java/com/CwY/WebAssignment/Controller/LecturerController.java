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
    private static final int DASHBOARD_QUEUE_PREVIEW_LIMIT = 5;


    @GetMapping("/lecturer/dashboard")
    @PreAuthorize("hasRole('LECTURER')")
    public String showLecturerDashboard(Model model, Principal principal) {
        User lecturer = getCurrentUser(principal);

        List<GradingQueueItem> fullQueue = buildQueueItems(lecturer);
        List<GradingQueueItem> previewQueue = fullQueue.stream()
                .limit(DASHBOARD_QUEUE_PREVIEW_LIMIT)
                .collect(Collectors.toList());

        model.addAttribute("gradingQueueItems", previewQueue);
        model.addAttribute("hasMoreGradingQueueItems", fullQueue.size() > DASHBOARD_QUEUE_PREVIEW_LIMIT);
        model.addAttribute("gradingQueuePreviewLimit", DASHBOARD_QUEUE_PREVIEW_LIMIT);
        return "lecturer-dashboard";
    }

    @GetMapping("/lecturer/grading-queue")
    @PreAuthorize("hasRole('LECTURER')")
    public String showFullGradingQueue(Model model, Principal principal) {
        User lecturer = getCurrentUser(principal);

        model.addAttribute("gradingQueueItems", buildQueueItems(lecturer));
        return "lecturer-grading-queue";
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

    private List<GradingQueueItem> buildQueueItems(User lecturer) {
        return assignmentService.getAssignmentsForLecturer(lecturer)
                .stream()
                .map(this::buildQueueItem)
                .collect(Collectors.toList());
    }

    private User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}