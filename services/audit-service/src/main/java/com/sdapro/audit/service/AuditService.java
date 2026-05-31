package com.sdapro.audit.service;

import com.sdapro.audit.domain.AuditLog;
import com.sdapro.audit.repository.AuditLogRepository;
import com.sdapro.eventbus.EventBusPublisher;
import com.sdapro.shared.events.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

/**
 * Audit Service - persists all events for compliance
 * PATTERN: Observer - receives events and stores them
 */
@Slf4j
@Service
public class AuditService implements EventBusPublisher.EventSubscriber {
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository, EventBusPublisher eventBus) {
        this.auditLogRepository = auditLogRepository;
        eventBus.subscribe("AlertIngestedEvent", this);
        log.info("AuditService initialized and subscribed to events");
    }

    @Override
    public void onEvent(DomainEvent event) {
        AuditLog auditLog = new AuditLog(
            event.getEventId(),
            event.getClass().getSimpleName(),
            event.getAggregateId(),
            event.getTimestamp(),
            event.toString(),
            "SYSTEM",
            "EVENT_RECEIVED"
        );
        auditLogRepository.save(auditLog);
        log.info("Audit log persisted for event: {}", event.getClass().getSimpleName());
    }

    public List<AuditLog> getAuditLog(String aggregateId) {
        return auditLogRepository.findByAggregateId(aggregateId);
    }

    public List<AuditLog> getComplianceReport(Instant since) {
        return auditLogRepository.findRecentLogs(since);
    }

    public long getAuditLogCount() {
        return auditLogRepository.count();
    }
}
