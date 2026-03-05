package com.Project.TaskManager.payload.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TimeLogRequest {
    
    @NotNull(message = "Minutes spent is required")
    @Min(value = 1, message = "Minute Spent must be at least 1")
    @Max(value =1440, message = "Cannot log more than 1440 minutes(24 hours) at once ")
    private Integer minutesSpent;

    @NotNull(message = "Log date is required")
    private LocalDate logDate;

    @Size(max = 500, message = "Note cannot exceed 500 characters")
    private String note;
}
