package com.sdapro.shared.events;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IncidentCreatedEvent extends DomainEvent {
    private String incidentName;
    private String severity;
    private String description;

    public IncidentCreatedEvent(UUID eventId, Instant timestamp, String aggregateId,
                                String incidentName, String severity, String description) {
        super(eventId, timestamp, aggregateId);
        this.incidentName = incidentName;
        this.severity = severity;
        this.description = description;
    }
}
