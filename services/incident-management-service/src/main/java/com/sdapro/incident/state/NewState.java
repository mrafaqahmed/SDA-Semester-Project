package com.sdapro.incident.state;

import com.sdapro.incident.domain.Incident;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NewState implements IncidentState {
    @Override
    public String getStateName() {
        return "NEW";
    }

    @Override
    public void beginTriage(Incident incident) {
        log.info("Incident {} transitioning from NEW to UNDER_TRIAGE", incident.getId());
        incident.setState(new UnderTriageState());
    }

    @Override
    public void initiateContainment(Incident incident) {
        throw new IllegalStateException("Cannot initiate containment on NEW incident");
    }

    @Override
    public void initiateEradication(Incident incident) {
        throw new IllegalStateException("Cannot initiate eradication on NEW incident");
    }

    @Override
    public void initiateRecovery(Incident incident) {
        throw new IllegalStateException("Cannot initiate recovery on NEW incident");
    }

    @Override
    public void completePostIncidentReview(Incident incident) {
        throw new IllegalStateException("Cannot complete PIR on NEW incident");
    }

    @Override
    public void close(Incident incident) {
        throw new IllegalStateException("Cannot close NEW incident");
    }

    @Override
    public String getAllowedActions() {
        return "beginTriage";
    }
}
