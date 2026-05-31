package com.sdapro.incident.state;

import com.sdapro.incident.domain.Incident;

/**
 * PATTERN: State
 * RATIONALE: Incident behavior changes based on its state. Each state handles
 * allowed transitions and actions. Eliminates complex conditional logic.
 */
public interface IncidentState {
    String getStateName();

    void beginTriage(Incident incident);
    void initiateContainment(Incident incident);
    void initiateEradication(Incident incident);
    void initiateRecovery(Incident incident);
    void completePostIncidentReview(Incident incident);
    void close(Incident incident);

    default String getAllowedActions() {
        return "No actions allowed in this state";
    }
}
