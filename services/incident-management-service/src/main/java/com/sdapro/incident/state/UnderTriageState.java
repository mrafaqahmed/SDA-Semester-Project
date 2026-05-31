package com.sdapro.incident.state;

import com.sdapro.incident.domain.Incident;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnderTriageState implements IncidentState {
    @Override
    public String getStateName() {
        return "UNDER_TRIAGE";
    }

    @Override
    public void beginTriage(Incident incident) {
        throw new IllegalStateException("Incident already under triage");
    }

    @Override
    public void initiateContainment(Incident incident) {
        log.info("Incident {} transitioning from UNDER_TRIAGE to CONTAINMENT", incident.getId());
        incident.setState(new ContainmentState());
    }

    @Override
    public void initiateEradication(Incident incident) {
        throw new IllegalStateException("Cannot skip to eradication from triage");
    }

    @Override
    public void initiateRecovery(Incident incident) {
        throw new IllegalStateException("Cannot skip to recovery from triage");
    }

    @Override
    public void completePostIncidentReview(Incident incident) {
        throw new IllegalStateException("Cannot do PIR from triage state");
    }

    @Override
    public void close(Incident incident) {
        throw new IllegalStateException("Cannot close incident still under triage");
    }

    @Override
    public String getAllowedActions() {
        return "initiateContainment";
    }
}
