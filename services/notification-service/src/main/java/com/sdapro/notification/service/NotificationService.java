package com.sdapro.notification.service;

import com.sdapro.eventbus.EventBusPublisher;
import com.sdapro.notification.factory.NotificationFactory;
import com.sdapro.notification.factory.Notifier;
import com.sdapro.shared.events.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Notification Service with Observer pattern
 * Receives events and dispatches notifications
 */
@Slf4j
@Service
public class NotificationService implements EventBusPublisher.EventSubscriber {
    
    public NotificationService(EventBusPublisher eventBus) {
        // Subscribe to all critical events
        eventBus.subscribe("AlertIngestedEvent", this);
        log.info("NotificationService subscribed to events");
    }

    @Override
    public void onEvent(DomainEvent event) {
        log.info("Notification triggered for event: {}", event.getClass().getSimpleName());
        dispatchNotifications(event);
    }

    private void dispatchNotifications(DomainEvent event) {
        // Create notifiers using Abstract Factory
        Notifier emailNotifier = NotificationFactory.createNotifier(
            NotificationFactory.NotificationType.EMAIL);
        Notifier slackNotifier = NotificationFactory.createNotifier(
            NotificationFactory.NotificationType.SLACK);
        Notifier pdNotifier = NotificationFactory.createNotifier(
            NotificationFactory.NotificationType.PAGERDUTY);

        // Send notifications
        emailNotifier.sendNotification("security@company.com", 
            "Event: " + event.getClass().getSimpleName());
        slackNotifier.sendNotification("#security-alerts", 
            "Event: " + event.getClass().getSimpleName());
        pdNotifier.sendNotification("incident-commander", 
            "Event: " + event.getClass().getSimpleName());
    }

    public void sendNotification(String method, String recipient, String message) {
        NotificationFactory.NotificationType type = NotificationFactory.NotificationType.valueOf(method);
        Notifier notifier = NotificationFactory.createNotifier(type);
        notifier.sendNotification(recipient, message);
    }
}
