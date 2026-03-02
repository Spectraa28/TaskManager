package com.Project.TaskManager.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Project.TaskManager.model.Project;
import com.Project.TaskManager.model.ProjectMember;
import com.Project.TaskManager.model.User;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember,UUID>{

    Optional<ProjectMember> findByProjectAndUser(Project project, User user);

    boolean existsByProjectAndUser(Project project, User user);

    void deleteByProjectAndUser(Project project, User user);
}
