package com.Project.TaskManager.payload.request;

import java.util.UUID;

import com.Project.TaskManager.enums.TaskPriority;
import com.Project.TaskManager.enums.TaskType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTaskRequest {
    
    @NotBlank(message = "Task title is required")
    @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    // Defaults to MEDIUM if not provided
    private TaskPriority priority = TaskPriority.MEDIUM;

    // Defaults to TASK if not provided
    private TaskType type = TaskType.TASK;

    // Optional — which sprint this task belongs to
    // Null means task goes to backlog
    private UUID sprintId;

    // Optional — who is responsible for this task
    private UUID assigneeId;

    // Optional — story points for sprint planning
    @Min(value = 0, message = "Story points cannot be negative")
    @Max(value = 100, message = "Story points cannot exceed 100")
    private Integer storyPoints;
}
