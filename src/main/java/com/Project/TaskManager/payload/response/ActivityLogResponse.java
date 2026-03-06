package com.Project.TaskManager.payload.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.Project.TaskManager.model.ActivityLog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogResponse {
    private UUID id;
    private String action;
    private String description;
    private String oldValue;
    private String newValue;

    // Task info
    private UUID taskId;
    private String taskKey;
    private String taskTitle;

    // Actor info — who performed the action
    private UUID actorId;
    private String actorName;

    private LocalDateTime createdAt;

    public static ActivityLogResponse from(ActivityLog log) {
        return ActivityLogResponse.builder()
                .id(log.getId())
                .action(log.getAction())
                .description(log.getDescription())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .taskId(log.getTask().getId())
                .taskKey(log.getTask().getTaskKey())
                .taskTitle(log.getTask().getTitle())
                .actorId(log.getActor().getId())
                .actorName(log.getActor().getFullName())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
