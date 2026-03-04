package com.Project.TaskManager.controller;

import com.Project.TaskManager.enums.TaskStatus;
import com.Project.TaskManager.payload.request.CreateTaskRequest;
import com.Project.TaskManager.payload.response.ApiResponse;
import com.Project.TaskManager.payload.response.TaskResponse;
import com.Project.TaskManager.security.CurrentUser;
import com.Project.TaskManager.security.service.UserDetailsImpl;
import com.Project.TaskManager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTaskRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created",
                        taskService.createTask(currentUser, projectId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getTasks(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(ApiResponse.success("Tasks retrieved",
                taskService.getTasksByProject(currentUser, projectId, pageable)));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {

        return ResponseEntity.ok(ApiResponse.success("Task found",
                taskService.getTaskById(currentUser, projectId, taskId)));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody CreateTaskRequest request) {

        return ResponseEntity.ok(ApiResponse.success("Task updated",
                taskService.updateTask(currentUser, projectId, taskId, request)));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {

        taskService.deleteTask(currentUser, projectId, taskId);

        return ResponseEntity.ok(ApiResponse.success("Task deleted"));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateStatus(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @RequestParam TaskStatus status) {

        return ResponseEntity.ok(ApiResponse.success("Task status updated",
                taskService.updateTaskStatus(currentUser, projectId, taskId, status)));
    }
}