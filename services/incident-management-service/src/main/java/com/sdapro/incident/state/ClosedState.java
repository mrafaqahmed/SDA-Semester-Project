package com.sdapro.incident.state;

import com.sdapro.incident.domain.Incident;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClosedState implements IncidentState {
    @Override
    public String getStateName() {
        return "CLOSED";
    }

    @Override
    public void beginTriage(Incident incident) {
        throw new IllegalStateException("Cannot reopen closed incident");
    }

    @Override
    public void initiateContainment(Incident incident) {
        throw new IllegalStateException("Incident is closed");
    }

    @Override
    public void initiateEradication(Incident incident) {
        throw new IllegalStateException("Incident is closed");
    }

    @Override
    public void initiateRecovery(Incident incident) {
        throw new IllegalStateException("Incident is closed");
    }

    @Override
    public void completePostIncidentReview(Incident incident) {
        throw new IllegalStateException("Incident is closed");
    }

    @Override
    public void close(Incident incident) {
        throw new IllegalStateException("Incident already closed");
    }

    @Override
    public String getAllowedActions() {
        return "No actions available - incident closed";
    }
}
