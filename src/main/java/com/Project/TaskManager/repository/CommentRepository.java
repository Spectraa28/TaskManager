package com.Project.TaskManager.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Project.TaskManager.model.Comment;
import com.Project.TaskManager.model.Task;

@Repository
public interface CommentRepository  extends JpaRepository<Comment,UUID>{

    Page<Comment> findAllByTask(Task task, Pageable pageable);
    
}
