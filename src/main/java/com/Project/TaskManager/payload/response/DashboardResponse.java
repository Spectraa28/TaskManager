package com.Project.TaskManager.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


public class DashboardResponse {

    // ─── User Personal Dashboard ──────────────────────────────────────────────

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserDashboard {

        // Task counts by status
        private long totalAssignedTasks;
        private long todoTasks;
        private long inProgressTasks;
        private long inReviewTasks;
        private long doneTasks;

        // Overdue tasks — past due date and not done
        private long overdueTasks;

        // Tasks due soon — due within next 7 days
        private long dueSoonTasks;

        // Workspaces and projects user belongs to
        private long totalWorkspaces;
        private long totalProjects;

        // Recently assigned tasks — last 5
        private List<RecentTask> recentTasks;
    }

    // ─── Workspace Dashboard ──────────────────────────────────────────────────

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WorkspaceDashboard {

        // Workspace overview
        private String workspaceName;
        private long totalMembers;
        private long totalProjects;

        // Task stats across entire workspace
        private long totalTasks;
        private long completedTasks;
        private long inProgressTasks;
        private long overdueTasks;

        // Sprint stats
        private long activeSprints;
        private long completedSprints;

        // Completion percentage — calculated field
        private double completionPercentage;

        // Top projects by task count
        private List<ProjectStat> topProjects;
    }

    // ─── Supporting Inner Classes ─────────────────────────────────────────────

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentTask {
        private String id;
        private String title;
        private String taskKey;
        private String status;
        private String priority;
        private String projectName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectStat {
        private String id;
        private String name;
        private String projectKey;
        private long totalTasks;
        private long completedTasks;
        private double completionPercentage;
    }
}