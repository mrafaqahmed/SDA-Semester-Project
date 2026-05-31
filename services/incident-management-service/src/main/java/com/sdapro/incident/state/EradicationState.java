package com.sdapro.incident.state;

import com.sdapro.incident.domain.Incident;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EradicationState implements IncidentState {
    @Override
    public String getStateName() {
        return "ERADICATION";
    }

    @Override
    public void beginTriage(Incident incident) {
        throw new IllegalStateException("Cannot go back to triage");
    }

    @Override
    public void initiateContainment(Incident incident) {
        throw new IllegalStateException("Already past containment phase");
    }

    @Override
    public void initiateEradication(Incident incident) {
        throw new IllegalStateException("Eradication already in progress");
    }

    @Override
    public void initiateRecovery(Incident incident) {
        log.info("Incident {} transitioning from ERADICATION to RECOVERY", incident.getId());
        incident.setState(new RecoveryState());
    }

    @Override
    public void completePostIncidentReview(Incident incident) {
        throw new IllegalStateException("Cannot do PIR before recovery");
    }

    @Override
    public void close(Incident incident) {
        throw new IllegalStateException("Cannot close incident in eradication phase");
    }

    @Override
    public String getAllowedActions() {
        return "initiateRecovery";
    }
}
