package com.Project.TaskManager.service;

import com.Project.TaskManager.payload.request.CreateProjectRequest;
import com.Project.TaskManager.payload.response.ProjectResponse;
import com.Project.TaskManager.security.service.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProjectService {

    // Create a new project inside a workspace
    // Only ADMIN or MANAGER of the workspace can do this
    ProjectResponse createProject(UserDetailsImpl currentUser,
                                  UUID workspaceId,
                                  CreateProjectRequest request);

    // List all active projects in a workspace — paginated
    Page<ProjectResponse> getProjectsByWorkspace(UserDetailsImpl currentUser,
                                                  UUID workspaceId,
                                                  Pageable pageable);

    // Get a single project by its ID
    ProjectResponse getProjectById(UserDetailsImpl currentUser,
                                   UUID workspaceId,
                                   UUID projectId);

    // Update project details — only ADMIN or MANAGER
    ProjectResponse updateProject(UserDetailsImpl currentUser,
                                  UUID workspaceId,
                                  UUID projectId,
                                  CreateProjectRequest request);

    // Soft delete — sets archived = true
    // Only ADMIN or MANAGER
    void archiveProject(UserDetailsImpl currentUser,
                        UUID workspaceId,
                        UUID projectId);
}