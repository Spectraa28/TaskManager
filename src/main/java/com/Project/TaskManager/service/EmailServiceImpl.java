package com.Project.TaskManager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.Project.TaskManager.model.Notification;
import com.Project.TaskManager.model.Task;
import com.Project.TaskManager.model.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{
    
    private final JavaMailSender mailSender;
    private  final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name}")
    private String appName;
  
    @Value("${app.frontend.url}")
    private String frontendUrl;
    
    
    
    
    @Override
    @Async
    public void sendTaskStatusChangedEmail(User user, User actor, Task task, String oldStatus, String newStatus) {
    Context context = new Context();
    context.setVariable("recipientName", user.getFullName());
    context.setVariable("actorName", actor.getFullName());
    context.setVariable("taskKey", task.getTaskKey());
    context.setVariable("taskTitle", task.getTitle());
    context.setVariable("oldStatus", oldStatus);
    context.setVariable("newStatus", newStatus);
    context.setVariable("taskUrl", frontendUrl + "/tasks/" + task.getId());
    context.setVariable("appName", appName);

    String html = templateEngine.process("email/task-status-changed", context);

    sendEmail(user.getEmail(), task.getTaskKey() + " status changed to" + newStatus, html);

}

    @Override
    @Async
    public void sendTaskAssignedEmail(User recipient, User actor, Task task) {
    Context context = new Context();
    context.setVariable("recipientName", recipient.getFullName());
    context.setVariable("actorName", actor.getFullName());
    context.setVariable("taskKey", task.getTaskKey());
    context.setVariable("taskTitle", task.getTitle());
    context.setVariable("taskUrl", frontendUrl+ "/tasks/" + task.getId());
    context.setVariable("appName", appName);

    String html = templateEngine.process("email/task-assigned", context);

    sendEmail(recipient.getEmail(), "YOu have been assigned to " + task.getTaskKey(), html);
    }

   // ─── Comment Added ───────────────────────────────────────────────────────

    @Override
    @Async
    public void sendCommentAddedEmail(User recipient,
                                      User actor,
                                      Task task,
                                      String commentContent) {
        Context context = new Context();
        context.setVariable("recipientName", recipient.getFullName());
        context.setVariable("actorName", actor.getFullName());
        context.setVariable("taskKey", task.getTaskKey());
        context.setVariable("taskTitle", task.getTitle());
        context.setVariable("commentContent", commentContent);
        context.setVariable("taskUrl", frontendUrl +
                "/tasks/" + task.getId());
        context.setVariable("appName", appName);

        String html = templateEngine
                .process("email/comment-added", context);

        sendEmail(recipient.getEmail(),
                actor.getFullName() + " commented on " +
                task.getTaskKey(),
                html);
    }

    // ─── Daily Digest ────────────────────────────────────────────────────────

    @Override
    @Async
    public void sendDailyDigestEmail(User recipient,
                                     List<Notification> notifications) {
        if (notifications.isEmpty()) return;

        Context context = new Context();
        context.setVariable("recipientName", recipient.getFullName());
        context.setVariable("notifications", notifications);
        context.setVariable("count", notifications.size());
        context.setVariable("appUrl", frontendUrl);
        context.setVariable("appName", appName);

        String html = templateEngine
                .process("email/daily-digest", context);

        sendEmail(recipient.getEmail(),
                "Your daily digest — " + notifications.size() +
                " updates",
                html);
    }

    // ─── Generic Sender ──────────────────────────────────────────────────────

    @Override
    @Async
    public void sendEmail(String to,
                          String subject,
                          String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);
            log.info("Email sent to '{}' — subject: '{}'", to, subject);

        } catch (MessagingException e) {
            log.error("Failed to send email to '{}': {}",
                    to, e.getMessage());
        }
    }
}

