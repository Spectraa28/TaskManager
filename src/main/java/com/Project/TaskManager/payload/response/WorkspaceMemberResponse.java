package com.Project.TaskManager.payload.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.Project.TaskManager.enums.WorkspaceRole;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkspaceMemberResponse {
     private UUID id;
    private UUID userId;
    private String fullName;
    private String email;
    private WorkspaceRole role;
    private LocalDateTime joinedAt;
}
