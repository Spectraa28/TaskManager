package com.Project.TaskManager.service;

import java.util.List;

import com.Project.TaskManager.model.Notification;
import com.Project.TaskManager.model.Task;
import com.Project.TaskManager.model.User;

public interface EmailService {
    void sendTaskStatusChangedEmail(User user, User actor,Task task, String oldStatus,String newStatus);

    void sendTaskAssignedEmail(User recipient, User actor, Task task);

    void sendCommentAddedEmail(
        User recipient,
        User actor,
        Task task,
        String commentContent
    );

    void sendDailyDigestEmail(
        User recipient,
        List<Notification> notifications
    );

    void sendEmail(String to,
        String subject,
        String htmlContent
    );
}
