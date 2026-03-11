package com.Project.TaskManager.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Project.TaskManager.enums.SprintStatus;
import com.Project.TaskManager.model.Project;
import com.Project.TaskManager.model.Sprint;

@Repository
public interface SprintRepository extends JpaRepository<Sprint,UUID>{

    Page<Sprint> findAllByProject(Project project, Pageable pageable);

    boolean existsByProjectAndStatus(Project project, SprintStatus status);

    Optional<Sprint> findByProjectAndStatus(Project project, SprintStatus status);
    
    // Count active sprints in a workspace
@Query("""
        SELECT COUNT(s) FROM Sprint s
        WHERE s.project.workspace.id = :workspaceId
        AND s.status = :status
        """)
long countByWorkspaceIdAndStatus(@Param("workspaceId") UUID workspaceId,
                                  @Param("status") SprintStatus status);

// Count completed sprints in a workspace
@Query("""
        SELECT COUNT(s) FROM Sprint s
        WHERE s.project.workspace.id = :workspaceId
        AND s.status = :status
        """)
long countCompletedByWorkspaceId(@Param("workspaceId") UUID workspaceId,
                                  @Param("status") SprintStatus status);
}
