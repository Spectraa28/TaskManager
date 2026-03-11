package com.Project.TaskManager.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponse {
    
    private int  totalResults;

    private List<TaskResult>  tasks;

    private List<ProjectResult> projects;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaskResult {
        private String id;
        private String title;
        private String taskKey;       // e.g. BACK-1
        private String status;
        private String priority;
        private String projectName;
        private String workspaceName;
        private String assigneeName;  // null if unassigned
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectResult {
        private String id;
        private String name;
        private String projectKey;    // e.g. BACK
        private String workspaceName;
        private int totalTasks;
        private int activeSprints;
    }

    // Static factory — builds the response and calculates totalResults
    public static SearchResponse of(List<TaskResult> tasks, List<ProjectResult> projects) {
        return SearchResponse.builder()
                .tasks(tasks)
                .projects(projects)
                .totalResults(tasks.size() + projects.size())
                .build();
    }
}
