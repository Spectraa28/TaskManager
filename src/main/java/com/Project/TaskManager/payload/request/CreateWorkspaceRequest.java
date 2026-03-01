package com.Project.TaskManager.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateWorkspaceRequest {
    
    @NotBlank(message = "Workspace Name is required")
    @Size(min = 2,max = 100,message = "Name must be between 2 and 100")
    private String name;

    @Size(max = 500,message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "Slug is required")
    @Size(min = 2, max = 100,message = "SLug must be between 2 and 100 characters")
    @Pattern(
        regexp = "^[a-z0-9-]+$",
        message = "Slug can only contain lower letters , numbers and hyphens"
    )
    private String slug;

}
