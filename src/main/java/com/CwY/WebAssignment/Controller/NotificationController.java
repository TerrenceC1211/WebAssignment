package com.CwY.WebAssignment.Controller;

import com.CwY.WebAssignment.model.User;
import com.CwY.WebAssignment.service.NotificationService;
import com.CwY.WebAssignment.service.UserService;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@ControllerAdvice
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @ModelAttribute
    public void populateNotifications(Model model, Principal principal) {
        if (principal == null) {
            return;
        }
        Optional<User> userOptional = userService.findByUsername(principal.getName());
        if (userOptional.isEmpty()) {
            return;
        }
        User user = userOptional.get();
        model.addAttribute("notifications", notificationService.getRecentNotifications(user));
        model.addAttribute("unreadNotificationCount", notificationService.countUnreadNotifications(user));
    }

    @PostMapping("/notifications/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public String markNotificationAsRead(@PathVariable Long notificationId, Principal principal, HttpServletRequest request) {
        if (principal != null) {
            userService.findByUsername(principal.getName())
                    .ifPresent(user -> notificationService.markAsRead(notificationId, user));
        }
        return "redirect:" + resolveRedirectUrl(request);
    }

    @PostMapping("/notifications/read-all")
    @PreAuthorize("isAuthenticated()")
    public String markAllNotificationsAsRead(Principal principal, HttpServletRequest request) {
        if (principal != null) {
            userService.findByUsername(principal.getName())
                    .ifPresent(notificationService::markAllAsRead);
        }
        return "redirect:" + resolveRedirectUrl(request);
    }

    private String resolveRedirectUrl(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return referer != null ? referer : "/";
    }
}