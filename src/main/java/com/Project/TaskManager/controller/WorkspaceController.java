package com.Project.TaskManager.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Project.TaskManager.payload.request.CreateWorkspaceRequest;
import com.Project.TaskManager.payload.request.InviteMemberRequest;
import com.Project.TaskManager.payload.response.ApiResponse;
import com.Project.TaskManager.payload.response.WorkspaceResponse;
import com.Project.TaskManager.security.CurrentUser;
import com.Project.TaskManager.security.service.UserDetailsImpl;
import com.Project.TaskManager.service.WorkspaceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {
    
    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<ApiResponse<WorkspaceResponse>> createWorkspace(
        @Valid @RequestBody CreateWorkspaceRequest request,
        @CurrentUser UserDetailsImpl currentUser
    ){
        WorkspaceResponse response = workspaceService.createWorkspace(request, currentUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Workspace created successfully",response));
    }

   @GetMapping
    public ResponseEntity<ApiResponse<Page<WorkspaceResponse>>> getMyWorkspaces(
            @CurrentUser UserDetailsImpl currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WorkspaceResponse> response = workspaceService
                .getMyWorkspaces(currentUser.getId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(
                "Workspaces fetched successfully",
                response
        ));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> getWorkspaceBySlug(
            @PathVariable String slug,
            @CurrentUser UserDetailsImpl currentUser) {

        WorkspaceResponse response = workspaceService
                .getWorkspaceBySlug(slug, currentUser.getId());

        return ResponseEntity.ok(ApiResponse.success(
                "Workspace fetched successfully",
                response
        ));
    }

    @PutMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> updateWorkspace(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody CreateWorkspaceRequest request,
            @CurrentUser UserDetailsImpl currentUser) {

        WorkspaceResponse response = workspaceService
                .updatedWorkspace(
                        workspaceId,
                        request,
                        currentUser.getId()
                );

        return ResponseEntity.ok(ApiResponse.success(
                "Workspace updated successfully",
                response
        ));
    }

    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<Void>> deleteWorkspace(
            @PathVariable UUID workspaceId,
            @CurrentUser UserDetailsImpl currentUser) {

        workspaceService.deleteWorkspace(
                workspaceId,
                currentUser.getId()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Workspace archived successfully")
        );
    }

    @PostMapping("/{workspaceId}/members")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> inviteMember(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody InviteMemberRequest request,
            @CurrentUser UserDetailsImpl currentUser) {

        WorkspaceResponse response = workspaceService
                .inviteMember(
                        workspaceId,
                        request,
                        currentUser.getId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Member invited successfully",
                        response
                ));
    }

    @DeleteMapping("/{workspaceId}/members/{memberId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable UUID workspaceId,
            @PathVariable UUID memberId,
            @CurrentUser UserDetailsImpl currentUser) {

        workspaceService.removeMember(
                workspaceId,
                memberId,
                currentUser.getId()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Member removed successfully")
        );
    }
}
    

