package com.Project.TaskManager.payload.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
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
public class NotificationEvent implements Serializable{
    
    private NotificationType type;

    private String message;

    private UUID taskId;
    private String taskKey;
    private  String taskTitle;

    private UUID projectId;

    private UUID workspaceId;

    private UUID actorId;
    private String actorName;

    private LocalDateTime timestamp;

    private String payload;

    private List<UUID> recipientIds;
}
