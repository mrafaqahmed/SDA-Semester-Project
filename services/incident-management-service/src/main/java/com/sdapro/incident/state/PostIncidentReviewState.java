package com.sdapro.incident.state;

import com.sdapro.incident.domain.Incident;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostIncidentReviewState implements IncidentState {
    @Override
    public String getStateName() {
        return "POST_INCIDENT_REVIEW";
    }

    @Override
    public void beginTriage(Incident incident) {
        throw new IllegalStateException("Cannot go back to triage");
    }

    @Override
    public void initiateContainment(Incident incident) {
        throw new IllegalStateException("Cannot restart containment");
    }

    @Override
    public void initiateEradication(Incident incident) {
        throw new IllegalStateException("Cannot restart eradication");
    }

    @Override
    public void initiateRecovery(Incident incident) {
        throw new IllegalStateException("Cannot restart recovery");
    }

    @Override
    public void completePostIncidentReview(Incident incident) {
        throw new IllegalStateException("PIR already complete");
    }

    @Override
    public void close(Incident incident) {
        log.info("Incident {} transitioning from POST_INCIDENT_REVIEW to CLOSED", incident.getId());
        incident.setState(new ClosedState());
    }

    @Override
    public String getAllowedActions() {
        return "close";
    }
}
