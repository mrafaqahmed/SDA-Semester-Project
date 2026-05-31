package com.sdapro.eventbus.observers;

import com.sdapro.eventbus.EventBusPublisher;
import com.sdapro.shared.events.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import java.time.Instant;

/**
 * PATTERN: Observer
 * Logs all domain events for audit trail
 */
@Slf4j
public class AuditEventLogger implements EventBusPublisher.EventSubscriber {
    @Override
    public void onEvent(DomainEvent event) {
        String logEntry = String.format("[AUDIT] %s: EventType=%s, AggregateId=%s, Timestamp=%s",
            Instant.now(),
            event.getClass().getSimpleName(),
            event.getAggregateId(),
            event.getTimestamp());
        log.info(logEntry);
    }
}
