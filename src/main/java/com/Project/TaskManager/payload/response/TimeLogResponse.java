package com.Project.TaskManager.payload.response;

import com.Project.TaskManager.model.TimeLog;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeLogResponse {

    private UUID id;
    private Integer minutesSpent;
    private LocalDate logDate;
    private String note;

    // Task info
    private UUID taskId;
    private String taskKey;

    // User info
    private UUID userId;
    private String userName;

    private LocalDateTime createdAt;

    public static TimeLogResponse from(TimeLog timeLog) {
        return TimeLogResponse.builder()
                .id(timeLog.getId())
                .minutesSpent(timeLog.getMinutesSpent())
                .logDate(timeLog.getLogDate())
                .note(timeLog.getNote())
                .taskId(timeLog.getTask().getId())
                .taskKey(timeLog.getTask().getTaskKey())
                .userId(timeLog.getUser().getId())
                .userName(timeLog.getUser().getFullName())
                .createdAt(timeLog.getCreatedAt())
                .build();
    }
}