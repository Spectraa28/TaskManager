package com.Project.TaskManager.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiRequest {
    
    @NotBlank(message = "Task title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters ")
    private String title;

    @Size(max = 500, message = "Context must not exceed 500 characters")
    private String context;

    @Size(max = 2000, message = "Tasks summary must not exceed 2000 characters")
    private String tasksSummary;

    @Size(max=100,message = "Sprint name must not exceed 100 characters")
    private String sprintName;
}
