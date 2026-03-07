package com.Project.TaskManager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Project.TaskManager.enums.TaskStatus;
import com.Project.TaskManager.enums.WorkspaceRole;
import com.Project.TaskManager.exceptions.BadRequestException;
import com.Project.TaskManager.exceptions.ResourceNotFoundException;
import com.Project.TaskManager.exceptions.UnauthorizedException;
import com.Project.TaskManager.model.Project;
import com.Project.TaskManager.model.Sprint;
import com.Project.TaskManager.model.Task;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.model.Workspace;
import com.Project.TaskManager.model.WorkspaceMember;
import com.Project.TaskManager.payload.request.CreateTaskRequest;
import com.Project.TaskManager.payload.response.TaskResponse;
import com.Project.TaskManager.repository.ProjectRepository;
import com.Project.TaskManager.repository.SprintRepository;
import com.Project.TaskManager.repository.TaskRepository;
import com.Project.TaskManager.repository.UserRepository;
import com.Project.TaskManager.repository.WorkspaceMemberRepository;
import com.Project.TaskManager.repository.WorkspaceRepository;
import com.Project.TaskManager.security.RequiresWorkspaceRole;
import com.Project.TaskManager.security.service.UserDetailsImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService{

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final SprintRepository sprintRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;



    @RequiresWorkspaceRole(WorkspaceRole.DEVELOPER) 
    @Override
    @Transactional
    public TaskResponse createTask(UserDetailsImpl currentUser,
                                   UUID projectId,
                                   CreateTaskRequest request) {

        Project project = getProjectById(projectId);

        // Any workspace member can create a task

        User reporter = getUserById(currentUser.getId());

        // Generate task key — e.g. "BACK-1", "BACK-2"
        int nextSequence = taskRepository
                .findMaxSequenceNumberByProject(project) + 1;
        String taskKey = project.getKey() + "-" + nextSequence;

        // Resolve sprint if provided
        Sprint sprint = null;
        if (request.getSprintId() != null) {
            sprint = sprintRepository.findById(request.getSprintId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Sprint not found: " + request.getSprintId()));

            // Sprint must belong to the same project
            if (!sprint.getProject().getId().equals(project.getId())) {
                throw new BadRequestException(
                        "Sprint does not belong to this project");
            }
        }

        // Resolve assignee if provided
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = getUserById(request.getAssigneeId());

            // Assignee must be a member of the workspace
        }

        Task task = Task.builder()
                .taskKey(taskKey)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(TaskStatus.TODO)
                .priority(request.getPriority())
                .type(request.getType())
                .project(project)
                .sprint(sprint)
                .reporter(reporter)
                .assignee(assignee)
                .storyPoints(request.getStoryPoints())
                .sequenceNumber(nextSequence)
                .build();

        Task saved = taskRepository.save(task);

        activityLogService.log(
        saved,
        reporter,
        "TASK_CREATED",
        reporter.getFullName() + " created task " + saved.getTaskKey());


        log.info("Task '{}' created in project '{}' by '{}'",
                taskKey, project.getKey(), reporter.getEmail());

        return TaskResponse.from(saved);
    }

    // ─── Read ───────────────────────────────────────────────────────────────

    @RequiresWorkspaceRole(WorkspaceRole.DEVELOPER) 
    @Override
    @Transactional
    public Page<TaskResponse> getTasksByProject(UserDetailsImpl currentUser,
                                                UUID projectId,
                                                Pageable pageable) {

        Project project = getProjectById(projectId);

        return taskRepository.findAllByProject(project, pageable)
                .map(TaskResponse::from);
    }

    @RequiresWorkspaceRole(WorkspaceRole.DEVELOPER) 
    @Override
    @Transactional
    public TaskResponse getTaskById(UserDetailsImpl currentUser,
                                    UUID projectId,
                                    UUID taskId) {

        Project project = getProjectById(projectId);

        Task task = getTaskByIdAndProject(taskId, project);

        return TaskResponse.from(task);
    }

    // ─── Update ─────────────────────────────────────────────────────────────

    @RequiresWorkspaceRole(WorkspaceRole.DEVELOPER) 
    @Override
    @Transactional
    public TaskResponse updateTask(UserDetailsImpl currentUser,
                                   UUID projectId,
                                   UUID taskId,
                                   CreateTaskRequest request) {

        Project project = getProjectById(projectId);

        Task task = getTaskByIdAndProject(taskId, project);

        // Resolve sprint if provided
        Sprint sprint = null;
        if (request.getSprintId() != null) {
            sprint = sprintRepository.findById(request.getSprintId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Sprint not found: " + request.getSprintId()));

            if (!sprint.getProject().getId().equals(project.getId())) {
                throw new BadRequestException(
                        "Sprint does not belong to this project");
            }
        }

        // Resolve assignee if provided
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = getUserById(request.getAssigneeId());
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setType(request.getType());
        task.setSprint(sprint);
        task.setAssignee(assignee);
        task.setStoryPoints(request.getStoryPoints());

        Task updated = taskRepository.save(task);

        activityLogService.log(
        updated,
        getUserById(currentUser.getId()),
        "TASK_UPDATED",
        currentUser.getEmail() + " updated task " + updated.getTaskKey());

        log.info("Task '{}' updated by '{}'",
                updated.getTaskKey(), currentUser.getEmail());

        return TaskResponse.from(updated);
    }

    // ─── Delete ─────────────────────────────────────────────────────────────

    @RequiresWorkspaceRole(WorkspaceRole.MANAGER) 
    @Override
    @Transactional
    public void deleteTask(UserDetailsImpl currentUser,
                           UUID projectId,
                           UUID taskId) {

        Project project = getProjectById(projectId);

        // Only ADMIN or MANAGER can delete tasks

        Task task = getTaskByIdAndProject(taskId, project);

        taskRepository.delete(task);

        activityLogService.log(
        task,
        getUserById(currentUser.getId()),
        "TASK_DELETED",
        currentUser.getEmail() + " deleted task " + task.getTaskKey());

        log.info("Task '{}' deleted by '{}'",
                task.getTaskKey(), currentUser.getEmail());
    }

    // ─── Status Transition ──────────────────────────────────────────────────

    @RequiresWorkspaceRole(WorkspaceRole.DEVELOPER) 
    @Override
    @Transactional
    public TaskResponse updateTaskStatus(UserDetailsImpl currentUser,
                                         UUID projectId,
                                         UUID taskId,
                                         TaskStatus newStatus) {

        Project project = getProjectById(projectId);

        Task task = getTaskByIdAndProject(taskId, project);

        // Validate status transition is legal
        validateStatusTransition(task.getStatus(), newStatus);


        TaskStatus oldStatus = task.getStatus();

        task.setStatus(newStatus);
        Task updated = taskRepository.save(task);

        activityLogService.log(
        updated,
        getUserById(currentUser.getId()),
        "STATUS_CHANGED",
        currentUser.getEmail() + " changed status of " + task.getTaskKey()
                + " from " + oldStatus + " to " + newStatus,
        oldStatus.name(),
        newStatus.name());

        activityLogService.log(
        updated,
        getUserById(currentUser.getId()),
        "STATUS_CHANGED",
        currentUser.getEmail() + " changed status of " + task.getTaskKey()
                + " from " + task.getStatus() + " to " + newStatus,
        task.getStatus().name(),
        newStatus.name());

        log.info("Task '{}' status changed from '{}' to '{}' by '{}'",
                task.getTaskKey(), task.getStatus(),
                newStatus, currentUser.getEmail());

        return TaskResponse.from(updated);
    }
        // helpers



    private Project getProjectById(UUID projectId){
        return projectRepository.findById(projectId)
                                .orElseThrow(()->new ResourceNotFoundException("Project not found : "+ projectId));

    }

    private Task getTaskByIdAndProject(UUID taskId, Project project){
        Task task = taskRepository.findById(taskId)
                            .orElseThrow(()-> new ResourceNotFoundException("Task not found: "+ taskId));

        if(!task.getProject().getId().equals(project.getId())){
            throw new ResourceNotFoundException("Taask not found in this project");
        }

        return task;
    }

    private User getUserById(UUID userId){
        return userRepository.findById(userId)
                            .orElseThrow(()-> new ResourceNotFoundException("User not found: " + userId));
    }

    private void validateStatusTransition(TaskStatus current,
                                          TaskStatus next) {
        boolean valid = switch (current) {
            case TODO -> next == TaskStatus.IN_PROGRESS
                    || next == TaskStatus.CANCELLED;
            case IN_PROGRESS -> next == TaskStatus.IN_REVIEW
                    || next == TaskStatus.TODO
                    || next == TaskStatus.CANCELLED;
            case IN_REVIEW -> next == TaskStatus.DONE
                    || next == TaskStatus.IN_PROGRESS
                    || next == TaskStatus.CANCELLED;
            case DONE, CANCELLED -> false;
        };

        if (!valid) {
            throw new BadRequestException(
                    "Invalid status transition: "
                    + current + " → " + next);
        }
    }

   


}
