package com.Project.TaskManager.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.Project.TaskManager.payload.response.NotificationMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {
    
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/task.subscibe/{taskId}")
    @SendTo("/topic/tasks/{taskId}")
    public NotificationMessage subsciberToTask(@DestinationVariable UUID taskId){
         log.debug("Client subscibed to task channel: ", taskId);

         return NotificationMessage.builder()
                                  .type(null)
                                  .message("Conected to task channel : " + taskId)
                                  .taskId(taskId)
                                  .timestamp(LocalDateTime.now())
                                  .build();
    }

    @MessageMapping("/project.subscribe/{projectId}")
    @SendTo("/topic/projects/{projectId}")
    public NotificationMessage subscibeToProject(@DestinationVariable UUID projectId){
        log.debug("Client subscribed to project channel : {}",projectId);

        return NotificationMessage.builder()
                                  .type(null)
                                  .message("Connected to project channel: "+ projectId)
                                  .projectId(projectId)
                                  .timestamp(LocalDateTime.now())
                                  .build();
    }
}
