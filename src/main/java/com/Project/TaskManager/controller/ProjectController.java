package com.Project.TaskManager.controller;

import com.Project.TaskManager.payload.request.CreateProjectRequest;
import com.Project.TaskManager.payload.response.ApiResponse;
import com.Project.TaskManager.payload.response.ProjectResponse;
import com.Project.TaskManager.security.CurrentUser;
import com.Project.TaskManager.security.service.UserDetailsImpl;
import com.Project.TaskManager.service.ProjectService;
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
@RequestMapping("/api/v1/workspaces/{workspaceId}/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID workspaceId,
            @Valid @RequestBody CreateProjectRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created",
                        projectService.createProject(currentUser, workspaceId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> getProjects(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID workspaceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(ApiResponse.success("Projects retrieved",
                projectService.getProjectsByWorkspace(currentUser, workspaceId, pageable)));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId) {

        return ResponseEntity.ok(ApiResponse.success("Project found",
                projectService.getProjectById(currentUser, workspaceId, projectId)));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateProjectRequest request) {

        return ResponseEntity.ok(ApiResponse.success("Project updated",
                projectService.updateProject(currentUser, workspaceId, projectId, request)));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> archiveProject(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId) {

        projectService.archiveProject(currentUser, workspaceId, projectId);

        return ResponseEntity.ok(ApiResponse.success("Project archived"));
    }
}