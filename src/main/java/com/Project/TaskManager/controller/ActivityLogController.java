package com.Project.TaskManager.controller;

import com.Project.TaskManager.payload.response.ActivityLogResponse;
import com.Project.TaskManager.payload.response.ApiResponse;
import com.Project.TaskManager.security.CurrentUser;
import com.Project.TaskManager.security.service.UserDetailsImpl;
import com.Project.TaskManager.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    // Activity feed for a specific task
    @GetMapping("/api/v1/tasks/{taskId}/activity")
    public ResponseEntity<ApiResponse<Page<ActivityLogResponse>>> getTaskActivity(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(ApiResponse.success("Task activity retrieved",
                activityLogService.getTaskActivity(currentUser, taskId, pageable)));
    }

    // Activity feed for an entire project
    @GetMapping("/api/v1/projects/{projectId}/activity")
    public ResponseEntity<ApiResponse<Page<ActivityLogResponse>>> getProjectActivity(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(ApiResponse.success("Project activity retrieved",
                activityLogService.getProjectActivity(currentUser, projectId, pageable)));
    }
}