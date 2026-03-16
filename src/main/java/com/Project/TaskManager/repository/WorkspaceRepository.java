package com.Project.TaskManager.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Project.TaskManager.model.User;
import com.Project.TaskManager.model.Workspace;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {

    Optional<Workspace> findBySlugAndArchivedFalse(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndArchivedFalse(String slug);

    // Fixed query — archived=false applies to the whole result
    @Query("""
            SELECT w FROM Workspace w
            WHERE w.archived = false
            AND (
                w.owner = :user
                OR w.id IN (
                    SELECT wm.workspace.id
                    FROM WorkspaceMember wm
                    WHERE wm.user = :user
                )
            )
            """)
    Page<Workspace> findAllByMemberOrOwner(@Param("user") User user, Pageable pageable);

    // Simple derived query — no @Query needed
    Optional<Workspace> findByIdAndArchivedFalse(UUID id);

    // For viewing archived workspaces
    @Query("""
            SELECT w FROM Workspace w
            WHERE w.archived = true
            AND (
                w.owner = :user
                OR w.id IN (
                    SELECT wm.workspace.id
                    FROM WorkspaceMember wm
                    WHERE wm.user = :user
                )
            )
            """)
    Page<Workspace> findArchivedWorkspacesForUser(@Param("user") User user, Pageable pageable);
}