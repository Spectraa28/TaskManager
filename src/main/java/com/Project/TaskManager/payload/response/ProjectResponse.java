package com.Project.TaskManager.payload.response;

import com.Project.TaskManager.enums.TaskStatus;
import com.Project.TaskManager.model.Project;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private UUID id;
    private String name;
    private String key;
    private String description;
    private TaskStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean archived;

    // Workspace info — just enough, no full workspace object
    private UUID workspaceId;
    private String workspaceName;

    // Project lead info — just enough, no full user object
    private UUID projectLeadId;
    private String projectLeadName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Static factory method — same pattern as WorkspaceResponse
    public static ProjectResponse from(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .key(project.getKey())
                .description(project.getDescription())
                .status(project.getStatus())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .archived(project.isArchived())
                .workspaceId(project.getWorkspace().getId())
                .workspaceName(project.getWorkspace().getName())
                .projectLeadId(project.getProjectLead() != null
                        ? project.getProjectLead().getId() : null)
                .projectLeadName(project.getProjectLead() != null
                        ? project.getProjectLead().getFullName() : null)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}