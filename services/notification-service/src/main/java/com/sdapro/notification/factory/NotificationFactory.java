package com.sdapro.notification.factory;

/**
 * PATTERN: Abstract Factory
 * RATIONALE: Creates families of notifiers (Email, Slack, PagerDuty)
 * without specifying concrete classes. Easy to add new notification types.
 */
public class NotificationFactory {
    public static Notifier createNotifier(NotificationType type) {
        return switch (type) {
            case EMAIL -> new EmailNotifier();
            case SLACK -> new SlackNotifier();
            case PAGERDUTY -> new PagerDutyNotifier();
            default -> throw new IllegalArgumentException("Unknown notification type: " + type);
        };
    }

    public enum NotificationType {
        EMAIL,
        SLACK,
        PAGERDUTY
    }
}
