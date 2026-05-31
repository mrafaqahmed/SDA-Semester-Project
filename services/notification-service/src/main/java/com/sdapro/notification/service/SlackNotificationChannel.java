package com.sdapro.notification.service;

/**
 * PATTERN: Abstract Factory (Slack Channel)
 * RATIONALE: Concrete implementation for Slack notifications.
 */
public class SlackNotificationChannel implements NotificationChannel {
    @Override
    public void sendNotification(String recipient, String message, String severity) {
        System.out.println(String.format("[SLACK] Channel: %s | Severity: %s | Message: %s", 
            recipient, severity, message));
        // In production: use Slack WebHook API
    }

    @Override
    public String getChannelName() {
        return "Slack";
    }
}
