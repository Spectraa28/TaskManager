package com.Project.TaskManager.payload.request;

import java.time.LocalDate;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateProjectRequest {
    
    @NotBlank(message = "Project name is required")
    @Size(min=2,max = 100,message = "Project name must be between 2 ad 100 characters")
    private String name;

    @NotBlank(message = "Project key is required")
    @Size(min=2,max=10,message = "Project key must be between 2 and 10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$" , message = "Project key must be uppercase Lettersand Numbers only")
    private String key;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

}
