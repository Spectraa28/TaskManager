package com.Project.TaskManager.payload.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.Project.TaskManager.enums.WorkspaceRole;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkspaceResponse {
    
    private  UUID id;
    private String name;
    private String description;
    private String slug;
    private boolean archived;

    private UUID ownerId;
    private String ownerName;
    private String ownerEmail;

    private WorkspaceRole currentUserRole;
    private int memberCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
