package com.sdapro.notification.factory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlackNotifier implements Notifier {
    @Override
    public void sendNotification(String recipient, String message) {
        log.info("Sending Slack message to {}: {}", recipient, message);
        // In real impl: use Slack API
    }

    @Override
    public String getNotificationMethod() {
        return "SLACK";
    }
}
