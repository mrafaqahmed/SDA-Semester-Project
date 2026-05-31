package com.sdapro.notification.service;

/**
 * PATTERN: Abstract Factory (PagerDuty Channel)
 * RATIONALE: Concrete implementation for PagerDuty incident notifications.
 */
public class PagerDutyNotificationChannel implements NotificationChannel {
    @Override
    public void sendNotification(String recipient, String message, String severity) {
        System.out.println(String.format("[PAGERDUTY] Service: %s | Severity: %s | Message: %s", 
            recipient, severity, message));
        // In production: use PagerDuty Events API v2
    }

    @Override
    public String getChannelName() {
        return "PagerDuty";
    }
}
