package com.Project.TaskManager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Project.TaskManager.payload.request.CommentRequest;
import com.Project.TaskManager.payload.request.TimeLogRequest;
import com.Project.TaskManager.payload.response.CommentResponse;
import com.Project.TaskManager.payload.response.TimeLogResponse;
import com.Project.TaskManager.security.service.UserDetailsImpl;

public interface TaskExtrasService {
     // ─── Comments ───────────────────────────────────────────────────────────

    CommentResponse addComment(UserDetailsImpl currentUser,
                               UUID taskId,
                               CommentRequest request);

    Page<CommentResponse> getComments(UserDetailsImpl currentUser,
                                      UUID taskId,
                                      Pageable pageable);

    void deleteComment(UserDetailsImpl currentUser,
                       UUID taskId,
                       UUID commentId);

    // ─── Watchers ───────────────────────────────────────────────────────────

    void watchTask(UserDetailsImpl currentUser,
                   UUID taskId);

    void unwatchTask(UserDetailsImpl currentUser,
                     UUID taskId);

    // ─── Time Logs ──────────────────────────────────────────────────────────

    TimeLogResponse logTime(UserDetailsImpl currentUser,
                            UUID taskId,
                            TimeLogRequest request);

    Page<TimeLogResponse> getTimeLogs(UserDetailsImpl currentUser,
                                      UUID taskId,
                                      Pageable pageable);

    // Total minutes spent on a task — used for summary display
    Integer getTotalMinutes(UserDetailsImpl currentUser,
                            UUID taskId);
}
