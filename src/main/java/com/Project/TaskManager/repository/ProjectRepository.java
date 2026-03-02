package com.Project.TaskManager.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.Project.TaskManager.model.Project;
import com.Project.TaskManager.model.Workspace;

@Repository
public interface ProjectRepository extends JpaRepository<Project,UUID>{

    @Query("SELECT p FROM Project p WHERE p.workspace = :workspace AND p.archived = false")
    Page<Project> findAllByWorkspace(Workspace workspace, Pageable pageable);

    Optional<Project> findByWorkspaceAndKey(Workspace workspace, String key);

   // With this:
@Query("SELECT COUNT(p) > 0 FROM Project p WHERE p.workspace = :workspace AND p.key = :key")
boolean existsByWorkspaceAndKey(Workspace workspace, String key);
}
