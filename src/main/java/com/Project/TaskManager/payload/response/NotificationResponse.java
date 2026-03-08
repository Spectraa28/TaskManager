package com.Project.TaskManager.payload.response;

import com.Project.TaskManager.enums.NotificationType;
import com.Project.TaskManager.model.Notification;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private UUID id;
    private NotificationType type;
    private String message;
    private boolean read;
    private String payload;

    // Task info
    private UUID taskId;
    private String taskKey;
    private String taskTitle;

    // Actor info
    private UUID actorId;
    private String actorName;

    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .message(n.getMessage())
                .read(n.isRead())
                .payload(n.getPayload())
                .taskId(n.getTask().getId())
                .taskKey(n.getTask().getTaskKey())
                .taskTitle(n.getTask().getTitle())
                .actorId(n.getActor().getId())
                .actorName(n.getActor().getFullName())
                .createdAt(n.getCreatedAt())
                .build();
    }
}