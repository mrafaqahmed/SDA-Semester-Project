package com.sdapro.enrichment.handler;

import com.sdapro.shared.domain.Alert;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreatIntelHandler extends EnrichmentHandler {
    @Override
    protected void doEnrich(Alert alert) {
        // Check threat reputation for source IP
        if (alert.getSourceIp() != null && !alert.getSourceIp().isEmpty()) {
            String reputation = queryThreatReputation(alert.getSourceIp());
            alert.setThreatReputation(reputation);
            log.debug("Threat reputation for {}: {}", alert.getSourceIp(), reputation);
        }
    }

    private String queryThreatReputation(String ip) {
        // Implementation would call VirusTotal, AlienVault, MISP, etc.
        return "Not Previously Seen";
    }
}
