package com.Project.TaskManager.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Project.TaskManager.enums.WorkspaceRole;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.model.Workspace;
import com.Project.TaskManager.model.WorkspaceMember;

@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember,UUID>{

    Optional<WorkspaceMember> findByWorkspaceAndUser(
                Workspace workspace, User user
    );

    List<WorkspaceMember> findByWorkspace(
        Workspace workspace
    );

    boolean existsByWorkspaceAndUser(
        Workspace workspace, User user
    );

    boolean existsByWorkspaceAndUserAndRole(
        Workspace workspace,
        User user,
        WorkspaceRole role
    );

    void deleteByWorkspaceAndUser(Workspace workspace, User user);

    int countByWorkspace(Workspace workspace);

    
}
