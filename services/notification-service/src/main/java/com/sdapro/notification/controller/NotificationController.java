package com.sdapro.notification.controller;

import com.sdapro.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * PATTERN: Abstract Factory, Observer
 * RATIONALE: REST API for triggering notifications.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/notifications/send")
    public ResponseEntity<String> sendNotification(@RequestBody Map<String, String> request) {
        String channel = request.get("channel");
        String recipient = request.get("recipient");
        String message = request.get("message");
        String severity = request.getOrDefault("severity", "INFO");

        if (channel == null || recipient == null || message == null) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

        try {
            notificationService.sendNotification(channel, recipient, message, severity);
            return ResponseEntity.ok("Notification sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send notification: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("notification-service is healthy");
    }
}
