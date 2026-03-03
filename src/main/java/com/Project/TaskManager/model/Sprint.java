package com.Project.TaskManager.model;

import java.time.LocalDate;

import com.Project.TaskManager.enums.SprintStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sprints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sprint extends BaseEntity{
    
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String goal;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SprintStatus status= SprintStatus.PLANNED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id",nullable = false)
    private Project project;
}
