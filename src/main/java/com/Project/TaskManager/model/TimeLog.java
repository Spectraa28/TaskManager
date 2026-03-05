package com.Project.TaskManager.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "time_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Time spent in minutes — e.g. 90 = 1.5 hours
    @Column(nullable = false)
    private Integer minutesSpent;

    // The date the work was done — not when it was logged
    @Column(nullable = false)
    private LocalDate logDate;

    // Optional note about what was done
    @Column(columnDefinition = "TEXT")
    private String note;
}