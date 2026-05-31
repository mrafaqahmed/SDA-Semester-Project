package com.sdapro.response.strategy;

import com.sdapro.incident.domain.Incident;
import java.util.Arrays;
import java.util.List;

public class ConservativeStrategy implements ResponseStrategy {
    @Override
    public String getStrategyName() {
        return "CONSERVATIVE";
    }

    @Override
    public List<String> generateActions(Incident incident) {
        return Arrays.asList(
            "INCREASE_MONITORING_LEVEL",
            "COLLECT_LOGS",
            "ALERT_ANALYSTS",
            "CREATE_INCIDENT_TICKET",
            "REQUEST_APPROVAL_FOR_ACTIONS"
        );
    }

    @Override
    public int getPriority() {
        return 1;  // Low priority, minimal actions
    }
}
