package com.Project.TaskManager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Project.TaskManager.enums.TaskStatus;
import com.Project.TaskManager.payload.request.CreateTaskRequest;
import com.Project.TaskManager.payload.response.TaskResponse;
import com.Project.TaskManager.security.service.UserDetailsImpl;

public interface TaskService {
    
    TaskResponse createTask(UserDetailsImpl currentUser,
                            UUID projectId,
                            CreateTaskRequest request);

    // List all tasks in a project — paginated
    Page<TaskResponse> getTasksByProject(UserDetailsImpl currentUser,
                                         UUID projectId,
                                         Pageable pageable);

    // Get a single task by ID
    TaskResponse getTaskById(UserDetailsImpl currentUser,
                             UUID projectId,
                             UUID taskId);

    // Update task details
    TaskResponse updateTask(UserDetailsImpl currentUser,
                            UUID projectId,
                            UUID taskId,
                            CreateTaskRequest request);

    // Delete a task — hard delete, task is removed permanently
    void deleteTask(UserDetailsImpl currentUser,
                    UUID projectId,
                    UUID taskId);

    // Update task status — FSM: TODO → IN_PROGRESS → IN_REVIEW → DONE
    TaskResponse updateTaskStatus(UserDetailsImpl currentUser,
                                  UUID projectId,
                                  UUID taskId,
                                  TaskStatus newStatus);
}
