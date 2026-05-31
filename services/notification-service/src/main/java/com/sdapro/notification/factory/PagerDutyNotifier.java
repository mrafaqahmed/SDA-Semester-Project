package com.sdapro.notification.factory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PagerDutyNotifier implements Notifier {
    @Override
    public void sendNotification(String recipient, String message) {
        log.info("Sending PagerDuty incident to {}: {}", recipient, message);
        // In real impl: use PagerDuty API
    }

    @Override
    public String getNotificationMethod() {
        return "PAGERDUTY";
    }
}
