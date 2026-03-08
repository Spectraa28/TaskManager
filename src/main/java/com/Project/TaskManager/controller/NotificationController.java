package com.Project.TaskManager.controller;

import com.Project.TaskManager.payload.response.ApiResponse;
import com.Project.TaskManager.payload.response.NotificationResponse;
import com.Project.TaskManager.security.CurrentUser;
import com.Project.TaskManager.security.service.UserDetailsImpl;
import com.Project.TaskManager.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // Get all notifications — paginated
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>>
    getMyNotifications(
            @CurrentUser UserDetailsImpl currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(
                "Notifications retrieved",
                notificationService.getMyNotifications(
                        currentUser, pageable)));
    }

    // Get unread notifications only
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>>
    getUnreadNotifications(
            @CurrentUser UserDetailsImpl currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(
                "Unread notifications retrieved",
                notificationService.getMyUnreadNotifications(
                        currentUser, pageable)));
    }

    // Count unread — for notification badge
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> countUnread(
            @CurrentUser UserDetailsImpl currentUser) {

        return ResponseEntity.ok(ApiResponse.success(
                "Unread count retrieved",
                notificationService.countUnread(currentUser)));
    }

    // Mark single notification as read
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID notificationId) {

        notificationService.markAsRead(currentUser, notificationId);
        return ResponseEntity.ok(
                ApiResponse.success("Notification marked as read", null));
    }

    // Mark all notifications as read
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @CurrentUser UserDetailsImpl currentUser) {

        notificationService.markAllAsRead(currentUser);
        return ResponseEntity.ok(
                ApiResponse.success("All notifications marked as read", null));
    }
}