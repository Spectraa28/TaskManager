package com.Project.TaskManager.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Project.TaskManager.enums.SprintStatus;
import com.Project.TaskManager.enums.TaskStatus;
import com.Project.TaskManager.exceptions.ResourceNotFoundException;
import com.Project.TaskManager.exceptions.UnauthorizedException;
import com.Project.TaskManager.model.Project;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.model.Workspace;
import com.Project.TaskManager.payload.response.DashboardResponse;
import com.Project.TaskManager.payload.response.DashboardResponse.UserDashboard;
import com.Project.TaskManager.payload.response.DashboardResponse.WorkspaceDashboard;
import com.Project.TaskManager.repository.ProjectRepository;
import com.Project.TaskManager.repository.SprintRepository;
import com.Project.TaskManager.repository.TaskRepository;
import com.Project.TaskManager.repository.UserRepository;
import com.Project.TaskManager.repository.WorkspaceMemberRepository;
import com.Project.TaskManager.repository.WorkspaceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService{
   
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final SprintRepository sprintRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;
   
   
   
   @Override
    @Transactional(readOnly = true)
    public DashboardResponse.UserDashboard getUserDashboard(UUID userId){

        log.info("Building user dashboard for user {}", userId);

        User currentUser = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        LocalDate today = LocalDate.now();
        TaskStatus doneStatus = TaskStatus.DONE;

        // Task counts by status
        long totalAssigned = taskRepository.countByAssigneeId(userId);
        long todoTasks = taskRepository.countByAssigneeIdAndStatus(userId, TaskStatus.TODO);
        long inProgressTasks = taskRepository.countByAssigneeIdAndStatus(userId, TaskStatus.IN_PROGRESS);
        long inReviewTasks = taskRepository.countByAssigneeIdAndStatus(userId, TaskStatus.IN_REVIEW);
        long doneTasks = taskRepository.countByAssigneeIdAndStatus(userId, doneStatus);

        // Overdue and due soon
        long overdueTasks = taskRepository.countOverdueTasksForUser(userId, today, doneStatus);
        long dueSoonTasks = taskRepository.countDueSoonTasksForUser(
                userId, today, today.plusDays(7), doneStatus);

        // Workspace and project counts
        long totalWorkspaces = workspaceMemberRepository.countByUser(currentUser);
        long totalProjects = projectRepository.countProjectsForUser(userId);

        // Recent tasks — last 5
        List<com.Project.TaskManager.model.Task> recentTasks =
                taskRepository.findRecentTasksForUser(userId, PageRequest.of(0, 5));

        List<DashboardResponse.RecentTask> recentTaskDTOs = recentTasks.stream()
                .map(t -> DashboardResponse.RecentTask.builder()
                        .id(t.getId().toString())
                        .title(t.getTitle())
                        .taskKey(t.getTaskKey())
                        .status(t.getStatus().name())
                        .priority(t.getPriority().name())
                        .projectName(t.getProject().getName())
                        .build())
                .toList();

        return DashboardResponse.UserDashboard.builder()
                .totalAssignedTasks(totalAssigned)
                .todoTasks(todoTasks)
                .inProgressTasks(inProgressTasks)
                .inReviewTasks(inReviewTasks)
                .doneTasks(doneTasks)
                .overdueTasks(overdueTasks)
                .dueSoonTasks(dueSoonTasks)
                .totalWorkspaces(totalWorkspaces)
                .totalProjects(totalProjects)
                .recentTasks(recentTaskDTOs)
                .build();
    }

    // ─── Workspace Dashboard ──────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse.WorkspaceDashboard getWorkspaceDashboard(UUID workspaceId, UUID currentUserId) {

        log.info("Building workspace dashboard for workspace {}", workspaceId);

        User currentUser = userRepository.findById(currentUserId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify workspace exists
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Workspace not found with id: " + workspaceId));

        // Verify user is a member
        boolean isMember = workspaceMemberRepository
                .existsByWorkspaceAndUser(workspace, currentUser);
        if (!isMember) {
            throw new UnauthorizedException("You are not a member of this workspace");
        }

        LocalDate today = LocalDate.now();
        TaskStatus doneStatus = TaskStatus.DONE;

        // Member and project counts
        long totalMembers = workspaceMemberRepository.countByWorkspace(workspace);
        long totalProjects = projectRepository.countProjectsInWorkspace(workspaceId);

        // Task stats
        long totalTasks = taskRepository.countTasksInWorkspace(workspaceId);
        long completedTasks = taskRepository.countTasksInWorkspaceByStatus(
                workspaceId, doneStatus);
        long inProgressTasks = taskRepository.countTasksInWorkspaceByStatus(
                workspaceId, TaskStatus.IN_PROGRESS);
        long overdueTasks = taskRepository.countOverdueTasksInWorkspace(
                workspaceId, today, doneStatus);

        // Sprint stats
        long activeSprints = sprintRepository.countByWorkspaceIdAndStatus(
                workspaceId, SprintStatus.ACTIVE);
        long completedSprints = sprintRepository.countCompletedByWorkspaceId(
                workspaceId, SprintStatus.COMPLETED);

        // Completion percentage
        double completionPercentage = totalTasks == 0 ? 0.0
                : Math.round((completedTasks * 100.0 / totalTasks) * 10.0) / 10.0;

        // Top projects
        List<DashboardResponse.ProjectStat> topProjects =
                buildTopProject(workspaceId, doneStatus);

        return DashboardResponse.WorkspaceDashboard.builder()
                .workspaceName(workspace.getName())
                .totalMembers(totalMembers)
                .totalProjects(totalProjects)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .inProgressTasks(inProgressTasks)
                .overdueTasks(overdueTasks)
                .activeSprints(activeSprints)
                .completedSprints(completedSprints)
                .completionPercentage(completionPercentage)
                .topProjects(topProjects)
                .build();
    }



    private List<DashboardResponse.ProjectStat> buildTopProject(UUID workspaceId, TaskStatus doneStatus){
        List<Project> projects = projectRepository.findAllByWorkspaceId(workspaceId);

        List<Object[]> taskCounts = taskRepository
                                    .countTasksGroupedByProject(workspaceId);
        
        List<Object[]> completedCounts =taskRepository
                                        .countCompletedTasksGroupedByProject(workspaceId, doneStatus);

        Map<UUID,Long> totalMap = new HashMap<>();
        for (Object[] row : taskCounts) {
            totalMap.put((UUID) row[0], (Long) row[1]);
        }

        Map<UUID, Long> completedMap = new HashMap<>();
        for(Object[] row: completedCounts){
            totalMap.put((UUID) row[0], (Long) row[1]);
        }


        List<DashboardResponse.ProjectStat> stats = new ArrayList<>();
        for (Project project : projects) {
            long total = totalMap.getOrDefault(project.getId(), 0L);
            long completed = completedMap.getOrDefault(project.getId(), 0L);
            double pct = total == 0 ? 0.0
                    : Math.round((completed * 100.0 / total) * 10.0) / 10.0;

            stats.add(DashboardResponse.ProjectStat.builder()
                    .id(project.getId().toString())
                    .name(project.getName())
                    .projectKey(project.getKey())
                    .totalTasks(total)
                    .completedTasks(completed)
                    .completionPercentage(pct)
                    .build());
        }

            // Sort by totalTasks descending — most active projects first
        stats.sort((a, b) -> Long.compare(b.getTotalTasks(), a.getTotalTasks()));

        // Return top 5 only
        return stats.stream().limit(5).toList();

    }
    
}
