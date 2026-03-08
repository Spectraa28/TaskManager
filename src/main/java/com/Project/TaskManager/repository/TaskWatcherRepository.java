package com.Project.TaskManager.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Project.TaskManager.model.Task;
import com.Project.TaskManager.model.TaskWatcher;
import com.Project.TaskManager.model.User;

@Repository
public interface TaskWatcherRepository  extends JpaRepository<TaskWatcher,UUID>{
    
    boolean existsByTaskAndUser(Task task,User user);

    Optional<TaskWatcher> findByTaskAndUser(Task task,User user);

    long countByTask(Task task);

    List<TaskWatcher> findAllByTask(Task task);
}
