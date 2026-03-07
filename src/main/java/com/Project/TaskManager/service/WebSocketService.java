package com.Project.TaskManager.service;

import com.Project.TaskManager.enums.NotificationType;
import com.Project.TaskManager.model.Task;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.payload.response.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    // SimpMessagingTemplate is Spring's built-in WebSocket message sender
    // Spring auto-creates it when @EnableWebSocketMessageBroker is present
    private final SimpMessagingTemplate messagingTemplate;

    // ─── Send to task channel ────────────────────────────────────────────────

    // All subscribers of /topic/tasks/{taskId} receive this message
    public void sendTaskNotification(Task task,
                                     User actor,
                                     NotificationType type,
                                     String message,
                                     String payload) {
        NotificationMessage notification = NotificationMessage.builder()
                .type(type)
                .message(message)
                .taskId(task.getId())
                .taskKey(task.getTaskKey())
                .taskTitle(task.getTitle())
                .projectId(task.getProject().getId())
                .actorId(actor.getId())
                .actorName(actor.getFullName())
                .timestamp(LocalDateTime.now())
                .payload(payload)
                .build();

        String channel = "/topic/tasks/" + task.getId();

        messagingTemplate.convertAndSend(channel, notification);

        log.debug("WebSocket notification sent to '{}' — type: '{}' by '{}'",
                channel, type, actor.getEmail());
    }

    // ─── Send to project channel ─────────────────────────────────────────────

    // All subscribers of /topic/projects/{projectId} receive this message
    public void sendProjectNotification(Task task,
                                        User actor,
                                        NotificationType type,
                                        String message,
                                        String payload) {
        NotificationMessage notification = NotificationMessage.builder()
                .type(type)
                .message(message)
                .taskId(task.getId())
                .taskKey(task.getTaskKey())
                .taskTitle(task.getTitle())
                .projectId(task.getProject().getId())
                .actorId(actor.getId())
                .actorName(actor.getFullName())
                .timestamp(LocalDateTime.now())
                .payload(payload)
                .build();

        String channel = "/topic/projects/" + task.getProject().getId();

        messagingTemplate.convertAndSend(channel, notification);

        log.debug("WebSocket notification sent to '{}' — type: '{}' by '{}'",
                channel, type, actor.getEmail());
    }

    // ─── Convenience method — sends to BOTH task and project channels ────────

    public void sendNotification(Task task,
                                 User actor,
                                 NotificationType type,
                                 String message,
                                 String payload) {
        sendTaskNotification(task, actor, type, message, payload);
        sendProjectNotification(task, actor, type, message, payload);
    }
}