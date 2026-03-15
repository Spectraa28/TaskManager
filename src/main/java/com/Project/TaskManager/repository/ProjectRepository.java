package com.Project.TaskManager.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Project.TaskManager.model.Project;
import com.Project.TaskManager.model.Workspace;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("SELECT p FROM Project p WHERE p.workspace = :workspace AND p.archived = false")
    Page<Project> findAllByWorkspace(Workspace workspace, Pageable pageable);

    // Only find active projects by key
    @Query("SELECT p FROM Project p WHERE p.workspace = :workspace AND p.key = :key AND p.archived = false")
    Optional<Project> findByWorkspaceAndKey(Workspace workspace, String key);

    // Only check active projects for duplicate key
    @Query("SELECT COUNT(p) > 0 FROM Project p WHERE p.workspace = :workspace AND p.key = :key AND p.archived = false")
    boolean existsByWorkspaceAndKey(Workspace workspace, String key);

    // Archived projects list
    @Query("SELECT p FROM Project p WHERE p.workspace = :workspace AND p.archived = true")
    Page<Project> findAllArchivedByWorkspace(Workspace workspace, Pageable pageable);

    // ─── Search ───────────────────────────────────────────────────────
    @Query("""
            SELECT p FROM Project p
            JOIN p.members pm
            WHERE pm.user.id = :userId
            AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            AND p.archived = false
            ORDER BY p.createdAt DESC
            """)
    List<Project> searchByNameForUser(@Param("userId") UUID userId,
                                       @Param("keyword") String keyword);

    // ─── Dashboard — User Stats ───────────────────────────────────────
    @Query("""
            SELECT COUNT(p) FROM Project p
            JOIN p.members pm
            WHERE pm.user.id = :userId
            AND p.archived = false
            """)
    long countProjectsForUser(@Param("userId") UUID userId);

    // ─── Dashboard — Workspace Stats ─────────────────────────────────
    @Query("""
            SELECT COUNT(p) FROM Project p
            WHERE p.workspace.id = :workspaceId
            AND p.archived = false
            """)
    long countProjectsInWorkspace(@Param("workspaceId") UUID workspaceId);

    @Query("""
            SELECT p FROM Project p
            WHERE p.workspace.id = :workspaceId
            AND p.archived = false
            ORDER BY p.createdAt DESC
            """)
    List<Project> findAllByWorkspaceId(@Param("workspaceId") UUID workspaceId);
}