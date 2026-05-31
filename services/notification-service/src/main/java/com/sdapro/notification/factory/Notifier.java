package com.sdapro.notification.factory;

/**
 * PATTERN: Abstract Factory - Product interface
 * Defines notification contract
 */
public interface Notifier {
    void sendNotification(String recipient, String message);
    String getNotificationMethod();
}
