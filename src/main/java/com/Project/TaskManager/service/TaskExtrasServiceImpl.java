package com.Project.TaskManager.service;

import com.Project.TaskManager.exceptions.BadRequestException;
import com.Project.TaskManager.exceptions.ResourceNotFoundException;
import com.Project.TaskManager.exceptions.UnauthorizedException;
import com.Project.TaskManager.model.*;
import com.Project.TaskManager.payload.request.CommentRequest;
import com.Project.TaskManager.payload.request.TimeLogRequest;
import com.Project.TaskManager.payload.response.CommentResponse;
import com.Project.TaskManager.payload.response.TimeLogResponse;
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
public class TaskExtrasServiceImpl implements TaskExtrasService {

    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final TaskWatcherRepository taskWatcherRepository;
    private final TimeLogRepository timeLogRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;

    // ─── Comments ───────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CommentResponse addComment(UserDetailsImpl currentUser,
                                      UUID taskId,
                                      CommentRequest request) {

        Task task = getTaskById(taskId);
        validateWorkspaceMembership(currentUser, task.getProject().getWorkspace());

        User author = getUserById(currentUser.getId());

        Comment comment = Comment.builder()
                .content(request.getContent())
                .task(task)
                .author(author)
                .build();

        Comment saved = commentRepository.save(comment);

        log.info("Comment added on task '{}' by '{}'",
                task.getTaskKey(), author.getEmail());

        return CommentResponse.from(saved);
    }

    @Override
    @Transactional
    public Page<CommentResponse> getComments(UserDetailsImpl currentUser,
                                             UUID taskId,
                                             Pageable pageable) {

        Task task = getTaskById(taskId);
        validateWorkspaceMembership(currentUser, task.getProject().getWorkspace());

        return commentRepository.findAllByTask(task, pageable)
                .map(CommentResponse::from);
    }

    @Override
    @Transactional
    public void deleteComment(UserDetailsImpl currentUser,
                              UUID taskId,
                              UUID commentId) {

        Task task = getTaskById(taskId);
        validateWorkspaceMembership(currentUser, task.getProject().getWorkspace());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment not found: " + commentId));

        // Verify comment belongs to this task
        if (!comment.getTask().getId().equals(task.getId())) {
            throw new ResourceNotFoundException(
                    "Comment not found on this task");
        }

        // Only the author or workspace ADMIN can delete a comment
        boolean isAuthor = comment.getAuthor().getId()
                .equals(currentUser.getId());

        boolean isAdmin = isWorkspaceAdmin(currentUser,
                task.getProject().getWorkspace());

        if (!isAuthor && !isAdmin) {
            throw new UnauthorizedException(
                    "Only the comment author or ADMIN can delete this comment");
        }

        commentRepository.delete(comment);

        log.info("Comment '{}' deleted by '{}'",
                commentId, currentUser.getEmail());
    }

    // ─── Watchers ───────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void watchTask(UserDetailsImpl currentUser, UUID taskId) {

        Task task = getTaskById(taskId);
        validateWorkspaceMembership(currentUser, task.getProject().getWorkspace());

        User user = getUserById(currentUser.getId());

        // Already watching — no error, just silently ignore
        if (taskWatcherRepository.existsByTaskAndUser(task, user)) {
            throw new BadRequestException(
                    "You are already watching this task");
        }

        TaskWatcher watcher = TaskWatcher.builder()
                .task(task)
                .user(user)
                .build();

        taskWatcherRepository.save(watcher);

        log.info("User '{}' is now watching task '{}'",
                user.getEmail(), task.getTaskKey());
    }

    @Override
    @Transactional
    public void unwatchTask(UserDetailsImpl currentUser, UUID taskId) {

        Task task = getTaskById(taskId);

        User user = getUserById(currentUser.getId());

        TaskWatcher watcher = taskWatcherRepository
                .findByTaskAndUser(task, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "You are not watching this task"));

        taskWatcherRepository.delete(watcher);

        log.info("User '{}' stopped watching task '{}'",
                user.getEmail(), task.getTaskKey());
    }

    // ─── Time Logs ──────────────────────────────────────────────────────────

    @Override
    @Transactional
    public TimeLogResponse logTime(UserDetailsImpl currentUser,
                                   UUID taskId,
                                   TimeLogRequest request) {

        Task task = getTaskById(taskId);
        validateWorkspaceMembership(currentUser, task.getProject().getWorkspace());

        User user = getUserById(currentUser.getId());

        TimeLog timeLog = TimeLog.builder()
                .task(task)
                .user(user)
                .minutesSpent(request.getMinutesSpent())
                .logDate(request.getLogDate())
                .note(request.getNote())
                .build();

        TimeLog saved = timeLogRepository.save(timeLog);

        log.info("User '{}' logged {} minutes on task '{}'",
                user.getEmail(), request.getMinutesSpent(), task.getTaskKey());

        return TimeLogResponse.from(saved);
    }

    @Override
    @Transactional
    public Page<TimeLogResponse> getTimeLogs(UserDetailsImpl currentUser,
                                             UUID taskId,
                                             Pageable pageable) {

        Task task = getTaskById(taskId);
        validateWorkspaceMembership(currentUser, task.getProject().getWorkspace());

        return timeLogRepository.findAllByTask(task, pageable)
                .map(TimeLogResponse::from);
    }

    @Override
    @Transactional
    public Integer getTotalMinutes(UserDetailsImpl currentUser, UUID taskId) {

        Task task = getTaskById(taskId);
        validateWorkspaceMembership(currentUser, task.getProject().getWorkspace());

        return timeLogRepository.findTotalMinutesByTask(task);
    }

    // ─── Private Helpers ────────────────────────────────────────────────────

    private Task getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found: " + taskId));
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + userId));
    }

    private void validateWorkspaceMembership(UserDetailsImpl currentUser,
                                             Workspace workspace) {

        boolean isOwner = workspace.getOwner().getId()
                .equals(currentUser.getId());

        if (isOwner) return;

        User user = getUserById(currentUser.getId());

        boolean isMember = workspaceMemberRepository
                .existsByWorkspaceAndUser(workspace, user);

        if (!isMember) {
            throw new UnauthorizedException(
                    "You are not a member of this workspace");
        }
    }

    private boolean isWorkspaceAdmin(UserDetailsImpl currentUser,
                                     Workspace workspace) {

        boolean isOwner = workspace.getOwner().getId()
                .equals(currentUser.getId());

        if (isOwner) return true;

        User user = getUserById(currentUser.getId());

        return workspaceMemberRepository
                .findByWorkspaceAndUser(workspace, user)
                .map(m -> m.getRole().name().equals("ADMIN"))
                .orElse(false);
    }
}