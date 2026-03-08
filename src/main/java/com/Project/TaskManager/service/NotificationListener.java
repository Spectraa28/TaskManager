package com.Project.TaskManager.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.Project.TaskManager.exceptions.ResourceNotFoundException;
import com.Project.TaskManager.model.Task;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.payload.event.NotificationEvent;
import com.Project.TaskManager.repository.TaskRepository;
import com.Project.TaskManager.repository.TaskWatcherRepository;
import com.Project.TaskManager.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationListener {
    
    private final NotificationService notificationService;
    private final WebSocketService webSocketService;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskWatcherRepository taskWatcherRepository;


    @RabbitListener(queues = "notification.queue")
    public void handleNotificationEvent(NotificationEvent event){
        log.info("Received notification event : type='{}' task='{}'", event.getType(),event.getTaskKey());

        try{
            Task task  = taskRepository.findById(event.getTaskId())
                                    .orElseThrow(()->new ResourceNotFoundException("Task not found: " + event.getTaskId()));

            User actor = userRepository.findById(event.getActorId())
                                    .orElseThrow(()->new ResourceNotFoundException("User not found: " + event.getActorId()));
       
            
                 // Notify all task watchers
            taskWatcherRepository.findAllByTask(task)
                    .forEach(watcher -> {
                        // Save notification to DB for each watcher
                        notificationService.createNotification(
                                watcher.getUser(),
                                actor,
                                task,
                                event.getType(),
                                event.getMessage(),
                                event.getPayload());

                        // Push real time WebSocket notification
                        webSocketService.sendNotification(
                                task,
                                actor,
                                event.getType(),
                                event.getMessage(),
                                event.getPayload());
                    });

            // Also notify task reporter if not a watcher
            notificationService.createNotification(
                    task.getReporter(),
                    actor,
                    task,
                    event.getType(),
                    event.getMessage(),
                    event.getPayload());

            // Also notify task assignee if exists and not a watcher
            if (task.getAssignee() != null) {
                notificationService.createNotification(
                        task.getAssignee(),
                        actor,
                        task,
                        event.getType(),
                        event.getMessage(),
                        event.getPayload());
            }

            log.info("Notification event processed successfully " +
                    "for task '{}'", event.getTaskKey());

        } catch (Exception e) {
            log.error("Failed to process notification event " +
                    "for task '{}': {}", event.getTaskKey(), e.getMessage());
        }

            }
    }

