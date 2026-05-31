package com.sdapro.shared.events;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Domain Event - Base class for all events published via Observer pattern.
 * PATTERN: Observer - Events enable loose coupling between services.
 */
@Data
@AllArgsConstructor
public abstract class DomainEvent {
    private UUID eventId;
    private Instant timestamp;
    protected String aggregateId;

    public DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.timestamp = Instant.now();
    }

    public DomainEvent(UUID eventId, Instant timestamp, String aggregateId) {
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.aggregateId = aggregateId;
    }
}
