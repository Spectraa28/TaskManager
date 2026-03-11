package com.Project.TaskManager.service;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Project.TaskManager.config.CacheConfig;
import com.Project.TaskManager.model.Project;
import com.Project.TaskManager.model.Task;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.payload.response.SearchResponse;
import com.Project.TaskManager.repository.ProjectRepository;
import com.Project.TaskManager.repository.TaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService{

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    
    
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.SEARCH_CACHE, key = "#userId + ':' + #keyword.toLowerCase()")
    public SearchResponse search(String keyword, UUID userId) {
    
        log.info("Searching for '{}' for user {}",keyword,userId);

        String trimmed = keyword == null ? "" : keyword.trim();

        if(trimmed.length() < 2){
            return SearchResponse.of(List.of(), List.of());
        }

        List<Task> tasks = taskRepository.searchByTitleForUser(userId,trimmed);

        // Search projects
        List<Project> projects = projectRepository.searchByNameForUser(
                userId, trimmed);

        // Map tasks to TaskResult DTOs
        List<SearchResponse.TaskResult> taskResults = tasks.stream()
                .map(this::mapToTaskResult)
                .toList();

        // Map projects to ProjectResult DTOs
        List<SearchResponse.ProjectResult> projectResults = projects.stream()
                .map(this::mapToProjectResult)
                .toList();

        log.info("Search for '{}' found {} tasks and {} projects",
                trimmed, taskResults.size(), projectResults.size());

        return SearchResponse.of(taskResults, projectResults);
    
    }

    // ─── Mappers ──────────────────────────────────────────────────────────────

    private SearchResponse.TaskResult mapToTaskResult(Task task) {
        return SearchResponse.TaskResult.builder()
                .id(task.getId().toString())
                .title(task.getTitle())
                .taskKey(task.getTaskKey())
                .status(task.getStatus().name())
                .priority(task.getPriority().name())
                .projectName(task.getProject().getName())
                .workspaceName(task.getProject().getWorkspace().getName())
                .assigneeName(task.getAssignee() != null
                        ? task.getAssignee().getFullName()
                        : null)
                .build();
    }

    private SearchResponse.ProjectResult mapToProjectResult(Project project) {
        return SearchResponse.ProjectResult.builder()
                .id(project.getId().toString())
                .name(project.getName())
                .projectKey(project.getKey())
                .workspaceName(project.getWorkspace().getName())
                .totalTasks(project.getTasks() != null
                        ? project.getTasks().size()
                        : 0)
                .activeSprints(project.getSprints() != null
                        ? (int) project.getSprints().stream()
                                .filter(s -> s.getStatus().name().equals("ACTIVE"))
                                .count()
                        : 0)
                .build();
    }
    
}
