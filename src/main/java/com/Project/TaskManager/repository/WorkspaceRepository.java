package com.Project.TaskManager.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.Project.TaskManager.model.User;
import com.Project.TaskManager.model.Workspace;


@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace,UUID>{
    Optional<Workspace> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("""
            SELECT w FROM Workspace w
            WHERE w.owner = :user
            OR w.id IN (
            SELECT wm.workspace.id
            FROM WorkspaceMember wm
            WHERE wm.user = :user
            )
            AND w.archived = false
            """)
    Page<Workspace> findAllByMemberOrOwner(User user, Pageable pageable);

    boolean existsBySlugAndArchivedFalse(String slug);
}
