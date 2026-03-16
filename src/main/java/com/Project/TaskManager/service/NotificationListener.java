package com.Project.TaskManager.service;

import com.Project.TaskManager.exceptions.ResourceNotFoundException;
import com.Project.TaskManager.model.Task;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.payload.event.NotificationEvent;
import com.Project.TaskManager.repository.TaskRepository;
import com.Project.TaskManager.repository.TaskWatcherRepository;
import com.Project.TaskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;
    private final WebSocketService webSocketService;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskWatcherRepository taskWatcherRepository;
    private final EmailService emailService;

    @Transactional
    @RabbitListener(queues = "notification.queue")
    public void handleNotificationEvent(NotificationEvent event) {

        log.info("Received notification event: type='{}' task='{}'",
                event.getType(), event.getTaskKey());

        try {
            Task task = taskRepository.findById(event.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Task not found: " + event.getTaskId()));

            User actor = userRepository.findById(event.getActorId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found: " + event.getActorId()));

            // ── Notify all task watchers ──────────────────────────────
            taskWatcherRepository.findAllByTask(task)
                    .forEach(watcher -> {
                        User recipient = watcher.getUser();

                        // Skip if watcher is the actor
                        if (recipient.getId().equals(actor.getId())) return;

                        notificationService.createNotification(
                                recipient, actor, task,
                                event.getType(),
                                event.getMessage(),
                                event.getPayload());

                        webSocketService.sendNotification(
                                task, actor,
                                event.getType(),
                                event.getMessage(),
                                event.getPayload());

                        sendEmailForEvent(event, task, actor, recipient);
                    });

            // ── Notify reporter (skip if reporter is the actor) ───────
            if (!task.getReporter().getId().equals(actor.getId())) {
                notificationService.createNotification(
                        task.getReporter(), actor, task,
                        event.getType(),
                        event.getMessage(),
                        event.getPayload());

                sendEmailForEvent(event, task, actor, task.getReporter());
            }

            // ── Notify assignee via recipientIds ──────────────────────
            if (event.getRecipientIds() != null && !event.getRecipientIds().isEmpty()) {
                event.getRecipientIds().forEach(recipientId -> {
                    // Skip actor and reporter (already notified above)
                    if (recipientId.equals(actor.getId())) return;
                    if (recipientId.equals(task.getReporter().getId())) return;

                    userRepository.findById(recipientId).ifPresent(recipient -> {
                        notificationService.createNotification(
                                recipient, actor, task,
                                event.getType(),
                                event.getMessage(),
                                event.getPayload());

                        sendEmailForEvent(event, task, actor, recipient);
                    });
                });
            }

            log.info("Notification event processed successfully for task '{}'",
                    event.getTaskKey());

        } catch (Exception e) {
            log.error("Failed to process notification event for task '{}': {}",
                    event.getTaskKey(), e.getMessage());
        }
    }

    // ── Private helper ────────────────────────────────────────────────────
    private void sendEmailForEvent(NotificationEvent event,
                                   Task task,
                                   User actor,
                                   User recipient) {
        log.info("Attempting email — type: '{}' recipient: '{}' actor: '{}'",
                event.getType(), recipient.getEmail(), actor.getEmail());

        if (recipient.getId().equals(actor.getId())) {
            log.info("Skipping email — self notification");
            return;
        }

        try {
            switch (event.getType()) {
                case TASK_ASSIGNED ->
                    emailService.sendTaskAssignedEmail(
                            recipient, actor, task);

                case TASK_UPDATED ->
                    emailService.sendTaskStatusChangedEmail(
                            recipient, actor, task,
                            event.getOldValue() != null
                                    ? event.getOldValue() : "UNKNOWN",
                            event.getPayload() != null
                                    ? event.getPayload() : "UNKNOWN");

                case COMMENT_ADDED ->
                    emailService.sendCommentAddedEmail(
                            recipient, actor, task,
                            event.getPayload() != null
                                    ? event.getPayload() : "");

                default ->
                    log.debug("No email template for event type: {}",
                            event.getType());
            }
        } catch (Exception e) {
            log.error("Failed to send email to '{}': {}",
                    recipient.getEmail(), e.getMessage());
        }
    }
}