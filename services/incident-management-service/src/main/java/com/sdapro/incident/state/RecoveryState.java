package com.sdapro.incident.state;

import com.sdapro.incident.domain.Incident;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecoveryState implements IncidentState {
    @Override
    public String getStateName() {
        return "RECOVERY";
    }

    @Override
    public void beginTriage(Incident incident) {
        throw new IllegalStateException("Cannot go back to triage");
    }

    @Override
    public void initiateContainment(Incident incident) {
        throw new IllegalStateException("Already past containment");
    }

    @Override
    public void initiateEradication(Incident incident) {
        throw new IllegalStateException("Already past eradication");
    }

    @Override
    public void initiateRecovery(Incident incident) {
        throw new IllegalStateException("Recovery already in progress");
    }

    @Override
    public void completePostIncidentReview(Incident incident) {
        log.info("Incident {} transitioning from RECOVERY to POST_INCIDENT_REVIEW", incident.getId());
        incident.setState(new PostIncidentReviewState());
    }

    @Override
    public void close(Incident incident) {
        throw new IllegalStateException("Cannot close incident before PIR");
    }

    @Override
    public String getAllowedActions() {
        return "completePostIncidentReview";
    }
}
