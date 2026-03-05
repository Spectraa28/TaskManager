package com.Project.TaskManager.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.Project.TaskManager.model.Task;
import com.Project.TaskManager.model.TimeLog;
import com.Project.TaskManager.model.User;

@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, UUID>{
    

    Page<TimeLog> findAllByTask(Task task,Pageable pageable);

    Page<TimeLog> findAllByTaskAndUser(Task task, User user, Pageable pageable);

    @Query("SELECT COALESCE(SUM(t.minutesSpent), 0) FROM TimeLog t WHERE t.task = :task")
    Integer findTotalMinutesByTask(Task task);

    @Query("SELECT COALESCE(SUM(t.minutesSpent), 0) FROM TimeLog t " +
           "WHERE t.task = :task AND t.user = :user")
    Integer findTotalMinutesByTaskAndUser(Task task, User user);
}
