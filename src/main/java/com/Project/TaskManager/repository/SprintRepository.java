package com.Project.TaskManager.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Project.TaskManager.enums.SprintStatus;
import com.Project.TaskManager.model.Project;
import com.Project.TaskManager.model.Sprint;

@Repository
public interface SprintRepository extends JpaRepository<Sprint,UUID>{

    Page<Sprint> findAllByProject(Project project, Pageable pageable);

    boolean existsByProjectAndStatus(Project project, SprintStatus status);

    Optional<Sprint> findByProjectAndStatus(Project project, SprintStatus status);
    
}
