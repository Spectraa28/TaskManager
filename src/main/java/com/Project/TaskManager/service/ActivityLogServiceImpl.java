package com.Project.TaskManager.service;

import com.Project.TaskManager.exceptions.ResourceNotFoundException;
import com.Project.TaskManager.exceptions.UnauthorizedException;
import com.Project.TaskManager.model.*;
import com.Project.TaskManager.payload.response.ActivityLogResponse;
import com.Project.TaskManager.repository.*;
import com.Project.TaskManager.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;

    // ─── Log Events ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void log(Task task,
                    User actor,
                    String action,
                    String description,
                    String oldValue,
                    String newValue) {

        ActivityLog activityLog = ActivityLog.builder()
                .task(task)
                .actor(actor)
                .action(action)
                .description(description)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();

        activityLogRepository.save(activityLog);
    }

    @Override
    @Transactional
    public void log(Task task,
                    User actor,
                    String action,
                    String description) {
        log(task, actor, action, description, null, null);
    }

    // ─── Read ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public Page<ActivityLogResponse> getTaskActivity(UserDetailsImpl currentUser,
                                                     UUID taskId,
                                                     Pageable pageable) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found: " + taskId));

        validateWorkspaceMembership(currentUser,
                task.getProject().getWorkspace());

        return activityLogRepository.findAllByTask(task, pageable)
                .map(ActivityLogResponse::from);
    }

    @Override
    @Transactional
    public Page<ActivityLogResponse> getProjectActivity(UserDetailsImpl currentUser,
                                                        UUID projectId,
                                                        Pageable pageable) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found: " + projectId));

        validateWorkspaceMembership(currentUser,
                project.getWorkspace());

        return activityLogRepository.findAllByProject(project, pageable)
                .map(ActivityLogResponse::from);
    }

    // ─── Private Helpers ────────────────────────────────────────────────────

    private void validateWorkspaceMembership(UserDetailsImpl currentUser,
                                             Workspace workspace) {

        boolean isOwner = workspace.getOwner().getId()
                .equals(currentUser.getId());

        if (isOwner) return;

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"));

        boolean isMember = workspaceMemberRepository
                .existsByWorkspaceAndUser(workspace, user);

        if (!isMember) {
            throw new UnauthorizedException(
                    "You are not a member of this workspace");
        }
    }
}