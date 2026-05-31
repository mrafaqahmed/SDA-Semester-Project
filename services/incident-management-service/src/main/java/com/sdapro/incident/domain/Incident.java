package com.sdapro.incident.domain;

import com.sdapro.incident.state.IncidentState;
import com.sdapro.incident.state.NewState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * PATTERN: State
 * Aggregate root that delegates behavior to current state.
 * State transitions are handled by each state implementation.
 */
@Slf4j
@Data
public class Incident {
    private UUID id;
    private String title;
    private String description;
    private String severity;
    private IncidentState state;
    private List<UUID> relatedAlertIds = new ArrayList<>();
    private UUID assignedAnalyst;
    private Instant createdAt;
    private Instant updatedAt;

    public Incident(String title, String description, String severity) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.state = new NewState();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void setState(IncidentState newState) {
        log.debug("Incident {} state changed from {} to {}",
            this.id, this.state.getStateName(), newState.getStateName());
        this.state = newState;
        this.updatedAt = Instant.now();
    }

    // Delegate state transitions
    public void beginTriage() {
        state.beginTriage(this);
    }

    public void initiateContainment() {
        state.initiateContainment(this);
    }

    public void initiateEradication() {
        state.initiateEradication(this);
    }

    public void initiateRecovery() {
        state.initiateRecovery(this);
    }

    public void completePostIncidentReview() {
        state.completePostIncidentReview(this);
    }

    public void close() {
        state.close(this);
    }

    public String getCurrentState() {
        return state.getStateName();
    }

    public String getAllowedActions() {
        return state.getAllowedActions();
    }

    public void addRelatedAlert(UUID alertId) {
        relatedAlertIds.add(alertId);
    }
}
