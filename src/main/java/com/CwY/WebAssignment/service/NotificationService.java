package com.CwY.WebAssignment.service;

import com.CwY.WebAssignment.model.*;
import com.CwY.WebAssignment.repository.NotificationRepository;
import com.CwY.WebAssignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public Notification createNotification(User user, String title, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public void notifyAssignmentCreated(Assignment assignment) {
        List<User> students = userRepository.findByRole(Role.STUDENT);
        if (students.isEmpty()) {
            return;
        }
        String title = "New Assignment";
        String message = String.format("%s is due on %s.",
                assignment.getTitle(), assignment.getDueDate().format(DATE_FORMATTER));
        students.forEach(student -> createNotification(student, title, message, NotificationType.ASSIGNMENT_CREATED));
    }

    public void notifySubmissionCreated(Submission submission) {
        User lecturer = submission.getAssignment().getCreatedBy();
        if (lecturer == null) {
            return;
        }
        String title = "New Submission";
        String message = String.format("%s submitted %s.",
                submission.getStudent().getUserName(), submission.getAssignment().getTitle());
        createNotification(lecturer, title, message, NotificationType.ASSIGNMENT_SUBMITTED);
    }

    public void notifySubmissionGraded(Submission submission) {
        User student = submission.getStudent();
        if (student == null) {
            return;
        }
        Double grade = submission.getGrade();
        String gradeText = grade != null ? String.format("Score: %.1f", grade) : "Graded";
        String title = "Submission Graded";
        String message = String.format("%s - %s.", submission.getAssignment().getTitle(), gradeText);
        createNotification(student, title, message, NotificationType.SUBMISSION_GRADED);
    }

    public List<Notification> getRecentNotifications(User user) {
        return notificationRepository.findTop10ByUserOrderByCreatedAtDesc(user);
    }

    public long countUnreadNotifications(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }

    public void markAsRead(Long notificationId, User user) {
        notificationRepository.findByIdAndUser(notificationId, user)
                .ifPresent(notification -> {
                    if (!notification.isRead()) {
                        notification.setRead(true);
                        notificationRepository.save(notification);
                    }
                });
    }

    public void markAllAsRead(User user) {
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        boolean updated = false;
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.setRead(true);
                updated = true;
            }
        }
        if (updated) {
            notificationRepository.saveAll(notifications);
        }
    }
}