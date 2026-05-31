package com.sdapro.response.strategy;

import com.sdapro.incident.domain.Incident;
import java.util.Arrays;
import java.util.List;

public class BalancedResponseStrategy implements ResponseStrategy {
    @Override
    public String getStrategyName() {
        return "BALANCED_RESPONSE";
    }

    @Override
    public List<String> generateActions(Incident incident) {
        return Arrays.asList(
            "ISOLATE_SUSPICIOUS_PROCESS",
            "ENABLE_ENHANCED_MONITORING",
            "COLLECT_FORENSIC_DATA",
            "ALERT_INCIDENT_RESPONSE_TEAM",
            "DOCUMENT_TIMELINE"
        );
    }

    @Override
    public int getPriority() {
        return 2;  // Medium priority
    }
}
