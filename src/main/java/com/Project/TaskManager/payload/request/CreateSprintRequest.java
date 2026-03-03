package com.Project.TaskManager.payload.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSprintRequest {


@NotBlank(message = "Sprint name is Required")
@Size(min = 3,max = 100, message = "Sprint name must be between 2 and 100 characters")
private String name;


@Size(max = 500, message = "Goal  cannot exceed 500 characters")
private String goal;

@NotNull(message = "Start date is required")
private LocalDate startDate;

@NotNull(message = "End date is Required")
private LocalDate endDate;
    
}
