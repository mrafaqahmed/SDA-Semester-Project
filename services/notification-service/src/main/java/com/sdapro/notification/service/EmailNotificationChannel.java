package com.sdapro.notification.service;

/**
 * PATTERN: Abstract Factory (Email Channel)
 * RATIONALE: Concrete implementation for email notifications.
 */
public class EmailNotificationChannel implements NotificationChannel {
    @Override
    public void sendNotification(String recipient, String message, String severity) {
        System.out.println(String.format("[EMAIL] To: %s | Severity: %s | Message: %s", 
            recipient, severity, message));
        // In production: use JavaMailSender or SendGrid API
    }

    @Override
    public String getChannelName() {
        return "Email";
    }
}
