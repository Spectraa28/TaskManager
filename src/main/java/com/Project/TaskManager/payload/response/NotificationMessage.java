package com.Project.TaskManager.payload.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.Project.TaskManager.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    
    private NotificationType type;

    private String message;

    private UUID taskId;
    private String taskKey;
    private String taskTitle;

        private UUID projectId;

    private  UUID actorId;
    private  String actorName;

    private LocalDateTime timestamp;

    private String payload;
}
