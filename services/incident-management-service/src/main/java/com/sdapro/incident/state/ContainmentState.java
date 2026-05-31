package com.sdapro.incident.state;

import com.sdapro.incident.domain.Incident;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContainmentState implements IncidentState {
    @Override
    public String getStateName() {
        return "CONTAINMENT";
    }

    @Override
    public void beginTriage(Incident incident) {
        throw new IllegalStateException("Cannot go back to triage");
    }

    @Override
    public void initiateContainment(Incident incident) {
        throw new IllegalStateException("Containment already initiated");
    }

    @Override
    public void initiateEradication(Incident incident) {
        log.info("Incident {} transitioning from CONTAINMENT to ERADICATION", incident.getId());
        incident.setState(new EradicationState());
    }

    @Override
    public void initiateRecovery(Incident incident) {
        throw new IllegalStateException("Cannot skip eradication phase");
    }

    @Override
    public void completePostIncidentReview(Incident incident) {
        throw new IllegalStateException("Cannot do PIR before recovery");
    }

    @Override
    public void close(Incident incident) {
        throw new IllegalStateException("Cannot close incident in containment phase");
    }

    @Override
    public String getAllowedActions() {
        return "initiateEradication";
    }
}
