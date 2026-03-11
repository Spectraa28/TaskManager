package com.Project.TaskManager.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Project.TaskManager.enums.TaskStatus;
import com.Project.TaskManager.model.Project;
import com.Project.TaskManager.model.Sprint;
import com.Project.TaskManager.model.Task;
import com.Project.TaskManager.model.User;

public interface TaskRepository extends JpaRepository<Task,UUID>{
    
    Page<Task> findAllByProject(Project project, Pageable pageable);

    Page<Task> findAllBySprint(Sprint sprint, Pageable pageable);

    Page<Task> findAllByProjectAndAssignee(Project project, User assognee,Pageable pageable);

    Page<Task> findAllByProjectAndStatus(Project project, TaskStatus status, Pageable pageable);

    @Query("SELECT COALESCE(MAX(t.sequenceNumber), 0) FROM Task t WHERE t.project = :project")
    Integer findMaxSequenceNumberByProject(Project project);

    @Query("""
            SELECT t FROM Task t
            JOIN t.project p
            JOIN p.members pm
            WHERE pm.user.id = :userId
            AND LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            AND t.archived = false
            ORDER BY t.createdAt DESC
            """)
    List<Task> searchByTitleForUser(@Param("userId") UUID userId,
                                 @Param("keyword") String keyword);

    // Count tasks assigned to user by status
@Query("SELECT COUNT(t) FROM Task t WHERE t.assignee.id = :userId AND t.status = :status AND t.archived = false")
long countByAssigneeIdAndStatus(@Param("userId") UUID userId,
                                 @Param("status") TaskStatus status);

// Count ALL tasks assigned to user
@Query("SELECT COUNT(t) FROM Task t WHERE t.assignee.id = :userId AND t.archived = false")
long countByAssigneeId(@Param("userId") UUID userId);

// Count overdue tasks — past due date and not DONE
@Query("""
        SELECT COUNT(t) FROM Task t
        WHERE t.assignee.id = :userId
        AND t.dueDate < :today
        AND t.status != :doneStatus
        AND t.archived = false
        """)
long countOverdueTasksForUser(@Param("userId") UUID userId,
                               @Param("today") java.time.LocalDate today,
                               @Param("doneStatus") TaskStatus doneStatus);

// Count tasks due within next 7 days — not done yet
@Query("""
        SELECT COUNT(t) FROM Task t
        WHERE t.assignee.id = :userId
        AND t.dueDate BETWEEN :today AND :sevenDaysLater
        AND t.status != :doneStatus
        AND t.archived = false
        """)
long countDueSoonTasksForUser(@Param("userId") UUID userId,
                               @Param("today") java.time.LocalDate today,
                               @Param("sevenDaysLater") java.time.LocalDate sevenDaysLater,
                               @Param("doneStatus") TaskStatus doneStatus);

// Get recently assigned tasks — last 5
@Query("""
        SELECT t FROM Task t
        WHERE t.assignee.id = :userId
        AND t.archived = false
        ORDER BY t.createdAt DESC
        """)
List<Task> findRecentTasksForUser(@Param("userId") UUID userId, Pageable pageable);

// ─── Dashboard — Workspace Stats ──────────────────────────────────────────────

// Count all tasks in a workspace
@Query("""
        SELECT COUNT(t) FROM Task t
        JOIN t.project p
        WHERE p.workspace.id = :workspaceId
        AND t.archived = false
        """)
long countTasksInWorkspace(@Param("workspaceId") UUID workspaceId);

// Count tasks in workspace by status
@Query("""
        SELECT COUNT(t) FROM Task t
        JOIN t.project p
        WHERE p.workspace.id = :workspaceId
        AND t.status = :status
        AND t.archived = false
        """)
long countTasksInWorkspaceByStatus(@Param("workspaceId") UUID workspaceId,
                                    @Param("status") TaskStatus status);

// Count overdue tasks in workspace
@Query("""
        SELECT COUNT(t) FROM Task t
        JOIN t.project p
        WHERE p.workspace.id = :workspaceId
        AND t.dueDate < :today
        AND t.status != :doneStatus
        AND t.archived = false
        """)
long countOverdueTasksInWorkspace(@Param("workspaceId") UUID workspaceId,
                                   @Param("today") java.time.LocalDate today,
                                   @Param("doneStatus") TaskStatus doneStatus);

// Count tasks per project in a workspace — for topProjects stat
@Query("""
        SELECT t.project.id, COUNT(t) FROM Task t
        JOIN t.project p
        WHERE p.workspace.id = :workspaceId
        AND t.archived = false
        GROUP BY t.project.id
        ORDER BY COUNT(t) DESC
        """)
List<Object[]> countTasksGroupedByProject(@Param("workspaceId") UUID workspaceId);

// Count completed tasks per project in a workspace
@Query("""
        SELECT t.project.id, COUNT(t) FROM Task t
        JOIN t.project p
        WHERE p.workspace.id = :workspaceId
        AND t.status = :doneStatus
        AND t.archived = false
        GROUP BY t.project.id
        """)
List<Object[]> countCompletedTasksGroupedByProject(@Param("workspaceId") UUID workspaceId,
                                                    @Param("doneStatus") TaskStatus doneStatus);

}
