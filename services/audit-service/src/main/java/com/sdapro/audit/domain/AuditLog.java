package com.sdapro.audit.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

/**
 * Audit Log Entity - immutable event record for compliance
 * Persisted to PostgreSQL
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_event_type", columnList = "event_type"),
    @Index(name = "idx_aggregate_id", columnList = "aggregate_id"),
    @Index(name = "idx_timestamp", columnList = "timestamp")
})
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID eventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(columnDefinition = "TEXT")
    private String eventData;

    @Column(nullable = false)
    private String actor;

    @Column(nullable = false)
    private String action;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(nullable = false)
    private Instant createdAt;

    public AuditLog(UUID eventId, String eventType, String aggregateId, Instant timestamp, 
                   String eventData, String actor, String action) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.timestamp = timestamp;
        this.eventData = eventData;
        this.actor = actor;
        this.action = action;
        this.createdAt = Instant.now();
    }
}
