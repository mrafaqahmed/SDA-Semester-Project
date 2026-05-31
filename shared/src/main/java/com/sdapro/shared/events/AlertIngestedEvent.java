package com.sdapro.shared.events;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlertIngestedEvent extends DomainEvent {
    private String source;
    private String title;
    private String severity;

    public AlertIngestedEvent(UUID eventId, Instant timestamp, String aggregateId,
                              String source, String title, String severity) {
        super(eventId, timestamp, aggregateId);
        this.source = source;
        this.title = title;
        this.severity = severity;
    }
}
