package com.sdapro.audit.controller;

import com.sdapro.audit.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PATTERN: Observer
 * RATIONALE: REST API for audit log queries and compliance reporting.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/audit/logs")
    public ResponseEntity<List<AuditService.AuditLogEntry>> getAuditLogs() {
        return ResponseEntity.ok(auditService.getAuditLog());
    }

    @GetMapping("/audit/logs/event-type/{eventType}")
    public ResponseEntity<List<AuditService.AuditLogEntry>> getAuditLogsByEventType(@PathVariable String eventType) {
        return ResponseEntity.ok(auditService.getAuditLogByEventType(eventType));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("audit-service is healthy");
    }
}
