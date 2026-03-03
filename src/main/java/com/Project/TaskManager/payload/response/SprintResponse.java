package com.Project.TaskManager.payload.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.Project.TaskManager.enums.SprintStatus;
import com.Project.TaskManager.model.Sprint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SprintResponse {
    
    private UUID id;
    private String name;
    private String goal;
    private LocalDate startDate;
    private LocalDate endDate;
    private SprintStatus status;

    private UUID projectId;
    private String projectName;
    private String projectKey;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SprintResponse from(Sprint sprint){
        return SprintResponse.builder().
                    id(sprint.getId()).
                    name(sprint.getName()).
                    goal(sprint.getGoal()).
                    startDate(sprint.getStartDate()).
                    endDate(sprint.getEndDate()).
                    status(sprint.getStatus()).
                    projectId(sprint.getProject().getId()).
                    projectName(sprint.getProject().getName()).
                    projectKey(sprint.getProject().getKey()).
                    createdAt(sprint.getCreatedAt()).
                    updatedAt(sprint.getUpdatedAt())
                    .build();
                }
}
