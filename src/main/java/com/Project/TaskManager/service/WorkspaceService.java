package com.Project.TaskManager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Project.TaskManager.payload.request.CreateWorkspaceRequest;
import com.Project.TaskManager.payload.request.InviteMemberRequest;
import com.Project.TaskManager.payload.response.WorkspaceResponse;

public interface WorkspaceService {
    WorkspaceResponse createWorkspace(
        CreateWorkspaceRequest request,UUID userId
    );

    WorkspaceResponse getWorkspaceBySlug(String slug, UUID userId);

    WorkspaceResponse updatedWorkspace(
        UUID workspaceId,
        CreateWorkspaceRequest request,
        UUID userId
    );

    void deleteWorkspace(UUID workspaceId, UUID userId);

    Page<WorkspaceResponse> getMyWorkspaces(
        UUID userId, Pageable pageable
    );

    WorkspaceResponse inviteMember(
        UUID workspaceId,
        InviteMemberRequest request,
        UUID userID
    );

    void removeMember(
        UUID workspaceId,
        UUID memeberId,
        UUID userID
    );
}
