package com.sdapro.response.service;

/**
 * PATTERN: Strategy (selector)
 * RATIONALE: Selects appropriate response strategy based on severity x criticality matrix
 */
public class ResponseStrategySelector {
    
    public static ResponseStrategy selectStrategy(Incident incident) {
        IncidentSeverity severity = incident.getSeverity();
        IncidentPriority priority = incident.getPriority();

        // Severity x Criticality Matrix
        if (severity == IncidentSeverity.CRITICAL) {
            return new AggressiveResponseStrategy();
        } else if (severity == IncidentSeverity.HIGH || priority == IncidentPriority.CRITICAL) {
            return new BalancedResponseStrategy();
        } else if (severity == IncidentSeverity.MEDIUM) {
            return new ConservativeResponseStrategy();
        } else {
            return new WatchAndWaitResponseStrategy();
        }
    }
}
