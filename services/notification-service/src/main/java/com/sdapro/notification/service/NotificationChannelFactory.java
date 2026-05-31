package com.sdapro.notification.service;

/**
 * PATTERN: Abstract Factory
 * RATIONALE: Creates NotificationChannel instances based on channel type.
 * Centralizes creation logic for multiple notification channels.
 */
public class NotificationChannelFactory {

    public static NotificationChannel createChannel(String channelType) {
        switch (channelType.toLowerCase()) {
            case "email":
                return new EmailNotificationChannel();
            case "slack":
                return new SlackNotificationChannel();
            case "pagerduty":
                return new PagerDutyNotificationChannel();
            default:
                throw new IllegalArgumentException("Unknown notification channel: " + channelType);
        }
    }
}
