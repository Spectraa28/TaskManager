package com.Project.TaskManager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Project.TaskManager.enums.NotificationType;
import com.Project.TaskManager.exceptions.ResourceNotFoundException;
import com.Project.TaskManager.exceptions.UnauthorizedException;
import com.Project.TaskManager.model.Notification;
import com.Project.TaskManager.model.Task;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.payload.response.NotificationResponse;
import com.Project.TaskManager.repository.NotificationRepository;
import com.Project.TaskManager.repository.TaskRepository;
import com.Project.TaskManager.repository.UserRepository;
import com.Project.TaskManager.security.service.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements  NotificationService{
    
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public void createNotification(User recipient,
                                   User actor,
                                   Task task,
                                   NotificationType type,
                                   String message,
                                   String payload) {
        if(recipient.getId().equals(actor.getId())) return;
        
        
        Notification notification = Notification.builder()
                    .recipient(recipient)
                    .actor(actor)
                    .task(task)
                    .type(type)
                    .message(message)
                    .payload(payload)
                    .read(false)
                    .build();

        notificationRepository.save(notification);
        log.debug("Notification created for user '{}' - type: '{}'", recipient.getEmail(),type);
        }

    @Override
    @Transactional(readOnly = true)
    public  Page<NotificationResponse> getMyNotifications(UserDetailsImpl currentUser, Pageable pageable){
                User user = getUserById(currentUser.getId());
                return notificationRepository.findByRecipientAndReadFalseOrderByCreatedAtDesc(user, pageable)
                .map(NotificationResponse::from);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyUnreadNotifications(
            UserDetailsImpl currentUser, Pageable pageable) {

        User user = getUserById(currentUser.getId());
        return notificationRepository
                .findByRecipientOrderByCreatedAtDesc(
                        user, pageable)
                .map(NotificationResponse::from);
    }


    @Override
    @Transactional(readOnly = true)
    public long countUnread(UserDetailsImpl currentUser){
        User user = getUserById(currentUser.getId());
        return notificationRepository.countByRecipientAndReadFalse(user);
    }

    @Override
    @Transactional
    public void markAsRead(UserDetailsImpl currentUser, UUID notificationId){
       Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found: " + notificationId));

        // Only recipient can mark as read
        if (!notification.getRecipient().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException(
                    "You can only mark your own notifications as read");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
        }

    @Override
    @Transactional
    public void markAllAsRead(UserDetailsImpl currentUser){
        User user = getUserById(currentUser.getId());
        notificationRepository.markAllAsRead(user);
        log.debug("All notification marked as read for user '{}' " , user.getEmail());
    }

    private User getUserById(UUID userId){
      return userRepository.findById(userId)
                        .orElseThrow(()-> new ResourceNotFoundException("User not found: " + userId));
    }

    
    
}



