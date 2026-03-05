package com.Project.TaskManager.controller;

import com.Project.TaskManager.payload.request.CommentRequest;
import com.Project.TaskManager.payload.request.TimeLogRequest;
import com.Project.TaskManager.payload.response.ApiResponse;
import com.Project.TaskManager.payload.response.CommentResponse;
import com.Project.TaskManager.payload.response.TimeLogResponse;
import com.Project.TaskManager.security.CurrentUser;
import com.Project.TaskManager.security.service.UserDetailsImpl;
import com.Project.TaskManager.service.TaskExtrasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}")
@RequiredArgsConstructor
public class TaskExtrasController {

    private final TaskExtrasService taskExtrasService;

    // ─── Comments ───────────────────────────────────────────────────────────

    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID taskId,
            @Valid @RequestBody CommentRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment added",
                        taskExtrasService.addComment(currentUser, taskId, request)));
    }

    @GetMapping("/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getComments(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(ApiResponse.success("Comments retrieved",
                taskExtrasService.getComments(currentUser, taskId, pageable)));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID taskId,
            @PathVariable UUID commentId) {

        taskExtrasService.deleteComment(currentUser, taskId, commentId);

        return ResponseEntity.ok(ApiResponse.success("Comment deleted"));
    }

    // ─── Watchers ───────────────────────────────────────────────────────────

    @PostMapping("/watch")
    public ResponseEntity<ApiResponse<Void>> watchTask(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID taskId) {

        taskExtrasService.watchTask(currentUser, taskId);

        return ResponseEntity.ok(ApiResponse.success("Now watching task"));
    }

    @DeleteMapping("/watch")
    public ResponseEntity<ApiResponse<Void>> unwatchTask(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID taskId) {

        taskExtrasService.unwatchTask(currentUser, taskId);

        return ResponseEntity.ok(ApiResponse.success("Stopped watching task"));
    }

    // ─── Time Logs ──────────────────────────────────────────────────────────

    @PostMapping("/time")
    public ResponseEntity<ApiResponse<TimeLogResponse>> logTime(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID taskId,
            @Valid @RequestBody TimeLogRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Time logged",
                        taskExtrasService.logTime(currentUser, taskId, request)));
    }

    @GetMapping("/time")
    public ResponseEntity<ApiResponse<Page<TimeLogResponse>>> getTimeLogs(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "logDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(ApiResponse.success("Time logs retrieved",
                taskExtrasService.getTimeLogs(currentUser, taskId, pageable)));
    }

    @GetMapping("/time/total")
    public ResponseEntity<ApiResponse<Integer>> getTotalMinutes(
            @CurrentUser UserDetailsImpl currentUser,
            @PathVariable UUID taskId) {

        return ResponseEntity.ok(ApiResponse.success("Total minutes retrieved",
                taskExtrasService.getTotalMinutes(currentUser, taskId)));
    }
}