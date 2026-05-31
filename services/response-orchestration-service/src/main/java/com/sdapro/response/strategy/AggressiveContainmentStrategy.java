package com.sdapro.response.strategy;

import com.sdapro.incident.domain.Incident;
import java.util.Arrays;
import java.util.List;

public abstract class AggressiveContainmentStrategy implements ResponseStrategy {
    @Override
    public String getStrategyName() {
        return "AGGRESSIVE_CONTAINMENT";
    }

    @Override
    public List<String> generateActions(Incident incident) {
        return Arrays.asList(
            "ISOLATE_HOST_FROM_NETWORK",
            "KILL_MALICIOUS_PROCESSES",
            "BLOCK_NETWORK_CONNECTIONS",
            "REVOKE_CREDENTIALS",
            "DISABLE_USER_ACCOUNT",
            "PRESERVE_EVIDENCE"
        );
    }

    @Override
    public int getPriority() {
        return 3;  // Highest priority, executed immediately
    }
}
