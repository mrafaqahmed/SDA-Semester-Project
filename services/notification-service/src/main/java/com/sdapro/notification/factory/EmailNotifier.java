package com.sdapro.notification.factory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailNotifier implements Notifier {
    @Override
    public void sendNotification(String recipient, String message) {
        log.info("Sending email to {}: {}", recipient, message);
        // In real impl: use JavaMail or SendGrid
    }

    @Override
    public String getNotificationMethod() {
        return "EMAIL";
    }
}
