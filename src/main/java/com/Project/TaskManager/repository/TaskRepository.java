package com.Project.TaskManager.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.Project.TaskManager.enums.TaskStatus;
import com.Project.TaskManager.model.Project;
import com.Project.TaskManager.model.Sprint;
import com.Project.TaskManager.model.Task;
import com.Project.TaskManager.model.User;

public interface TaskRepository extends JpaRepository<Task,UUID>{
    
    Page<Task> findAllByProject(Project project, Pageable pageable);

    Page<Task> findAllBySprint(Sprint sprint, Pageable pageable);

    Page<Task> findAllByProjectAndAssignee(Project project, User assognee,Pageable pageable);

    Page<Task> findAllByProjectAndStatus(Project project, TaskStatus status, Pageable pageable);

    @Query("SELECT COALESCE(MAX(t.sequenceNumber), 0) FROM Task t WHERE t.project = :project")
    Integer findMaxSequenceNumberByProject(Project project);
}
