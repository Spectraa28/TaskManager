package com.Project.TaskManager.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.Project.TaskManager.model.ActivityLog;
import com.Project.TaskManager.model.Project;
import com.Project.TaskManager.model.Task;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog,UUID>{
    Page<ActivityLog> findAllByTask(Task task, Pageable pageable);

    @Query("SELECT a FROM ActivityLog a WHERE a.task.project = :project " +
       "ORDER BY a.createdAt DESC")
    Page<ActivityLog> findAllByProject(Project project, Pageable pageable);
}
