package com.Project.TaskManager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog extends BaseEntity{
    
    @Column(nullable = false)
    private String action;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column
    private String oldValue;

    @Column
    private String newValue;

   @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
@JoinColumn(name = "task_id", nullable = false)
private Task task;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

}
