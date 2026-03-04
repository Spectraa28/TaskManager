package com.Project.TaskManager.payload.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.Project.TaskManager.enums.TaskPriority;
import com.Project.TaskManager.enums.TaskStatus;
import com.Project.TaskManager.enums.TaskType;
import com.Project.TaskManager.model.Task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private UUID id;
    private String taskKey;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private TaskType type;
    private Integer storyPoints;

    private UUID projectId;
    private String projectName;
    private String projectKey;

    private UUID sprintId;
    private  String sprintName;

    private UUID reporterId;
    private String reporterName;

    private UUID assigneeId;
    private String assigneeName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskResponse from(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .taskKey(task.getTaskKey())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .type(task.getType())
                .storyPoints(task.getStoryPoints())
                .projectId(task.getProject().getId())
                .projectName(task.getProject().getName())
                .projectKey(task.getProject().getKey())
                .sprintId(task.getSprint() != null
                        ? task.getSprint().getId() : null)
                .sprintName(task.getSprint() != null
                        ? task.getSprint().getName() : null)
                .reporterId(task.getReporter().getId())
                .reporterName(task.getReporter().getFullName())
                .assigneeId(task.getAssignee() != null
                        ? task.getAssignee().getId() : null)
                .assigneeName(task.getAssignee() != null
                        ? task.getAssignee().getFullName() : null)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
