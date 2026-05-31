package com.sdapro.shared.events;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IncidentStateChangedEvent extends DomainEvent {
    private String previousState;
    private String newState;
    private String reason;

    public IncidentStateChangedEvent(UUID eventId, Instant timestamp, String aggregateId,
                                     String previousState, String newState, String reason) {
        super(eventId, timestamp, aggregateId);
        this.previousState = previousState;
        this.newState = newState;
        this.reason = reason;
    }
}
