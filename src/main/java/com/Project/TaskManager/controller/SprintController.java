package com.Project.TaskManager.controller;

import com.Project.TaskManager.payload.request.CreateSprintRequest;
import com.Project.TaskManager.payload.response.ApiResponse;
import com.Project.TaskManager.payload.response.SprintResponse;
import com.Project.TaskManager.security.CurrentUser;
import com.Project.TaskManager.security.service.UserDetailsImpl;
import com.Project.TaskManager.service.SprintService;
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
@RequestMapping("/api/v1/projects/{projectId}/sprints")
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;

    @PostMapping
    public ResponseEntity<ApiResponse<SprintResponse>> createSprint(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateSprintRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sprint created",
                        sprintService.createSprint(currentUser, projectId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SprintResponse>>> getSprints(
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

        return ResponseEntity.ok(ApiResponse.success("Sprints retrieved",
                sprintService.getSprintByProject(currentUser, projectId, pageable)));
    }

    @PutMapping("/{sprintId}")
    public ResponseEntity<ApiResponse<SprintResponse>> updateSprint(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID projectId,
            @PathVariable UUID sprintId,
            @Valid @RequestBody CreateSprintRequest request) {

        return ResponseEntity.ok(ApiResponse.success("Sprint updated",
                sprintService.updateSprint(currentUser, projectId, sprintId, request)));
    }

    @PostMapping("/{sprintId}/start")
    public ResponseEntity<ApiResponse<SprintResponse>> startSprint(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID projectId,
            @PathVariable UUID sprintId) {

        return ResponseEntity.ok(ApiResponse.success("Sprint started",
                sprintService.startSprint(currentUser, projectId, sprintId)));
    }

    @PostMapping("/{sprintId}/complete")
    public ResponseEntity<ApiResponse<SprintResponse>> completeSprint(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID projectId,
            @PathVariable UUID sprintId) {

        return ResponseEntity.ok(ApiResponse.success("Sprint completed",
                sprintService.completeSprint(currentUser, projectId, sprintId)));
    }
}