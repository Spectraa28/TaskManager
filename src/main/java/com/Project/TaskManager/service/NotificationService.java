package com.Project.TaskManager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Project.TaskManager.enums.NotificationType;
import com.Project.TaskManager.model.Task;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.payload.response.NotificationResponse;
import com.Project.TaskManager.security.service.UserDetailsImpl;

public interface NotificationService {
    
    void createNotification(User recipient,
                            User actor,
                            Task task,
                            NotificationType type,
                        String message,
                    String payload );


    // Get all notifications for current user
    Page<NotificationResponse> getMyNotifications(
            UserDetailsImpl currentUser,
            Pageable pageable);

    // Get unread notifications only
    Page<NotificationResponse> getMyUnreadNotifications(
            UserDetailsImpl currentUser,
            Pageable pageable);

    // Count unread — for badge
    long countUnread(UserDetailsImpl currentUser);

    // Mark single notification as read
    void markAsRead(UserDetailsImpl currentUser, UUID notificationId);

    // Mark all as read
    void markAllAsRead(UserDetailsImpl currentUser);                
}
