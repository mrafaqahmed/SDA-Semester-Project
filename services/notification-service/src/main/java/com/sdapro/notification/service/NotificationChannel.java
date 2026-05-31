package com.sdapro.notification.service;

/**
 * PATTERN: Abstract Factory (Notification Channel)
 * RATIONALE: Interface for creating different notification channels (Email, Slack, PagerDuty).
 */
public interface NotificationChannel {
    void sendNotification(String recipient, String message, String severity);
    String getChannelName();
}
