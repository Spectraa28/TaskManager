package com.Project.TaskManager.service;

import com.Project.TaskManager.model.Notification;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.repository.NotificationRepository;
import com.Project.TaskManager.repository.RefreshTokenRepository;
import com.Project.TaskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledJobs {

    private final RefreshTokenRepository refreshTokenRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    // ─── Cleanup Expired Refresh Tokens ──────────────────────────────────────
    // Runs every day at midnight
    // Cron: second minute hour day month weekday
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredRefreshTokens() {
        log.info("Running scheduled job: cleanupExpiredRefreshTokens");

        try {
            // Delete all tokens that expired before now
            int deleted = refreshTokenRepository
                    .deleteAllByExpiresAtBefore(Instant.now());

            log.info("Cleaned up {} expired refresh tokens", deleted);
        } catch (Exception e) {
            log.error("Failed to cleanup expired tokens: {}",
                    e.getMessage());
        }
    }

    // ─── Daily Digest Email ───────────────────────────────────────────────────
    // Runs every day at 8 AM
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void sendDailyDigestEmails() {
        log.info("Running scheduled job: sendDailyDigestEmails");

        try {
            // Get all users
            List<User> users = userRepository.findAll();

            users.forEach(user -> {
                // Get unread notifications for this user
                List<Notification> unread = notificationRepository
                        .findByRecipientAndReadFalseOrderByCreatedAtDesc(
                                user, PageRequest.of(0, 50))
                        .getContent();

                // Only send digest if there are unread notifications
                if (!unread.isEmpty()) {
                    emailService.sendDailyDigestEmail(user, unread);
                    log.debug("Daily digest sent to '{}'",
                            user.getEmail());
                }
            });

            log.info("Daily digest job completed for {} users",
                    users.size());
        } catch (Exception e) {
            log.error("Failed to send daily digest emails: {}",
                    e.getMessage());
        }
    }

    // ─── Log System Health ────────────────────────────────────────────────────
    // Runs every hour — simple health check log
    @Scheduled(fixedRate = 3600000) // every 1 hour in milliseconds
    public void logSystemHealth() {
        log.info("System health check — application running normally");
    }
}