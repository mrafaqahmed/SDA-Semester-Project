package com.sdapro.enrichment.handler;

import com.sdapro.shared.domain.Alert;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AssetContextHandler extends EnrichmentHandler {
    @Override
    protected void doEnrich(Alert alert) {
        // Lookup asset criticality for destination IP
        if (alert.getDestIp() != null && !alert.getDestIp().isEmpty()) {
            String criticality = lookupAssetCriticality(alert.getDestIp());
            alert.setAssetCriticality(criticality);
            log.debug("Asset {} has criticality: {}", alert.getDestIp(), criticality);
        }
    }

    private String lookupAssetCriticality(String ip) {
        // Implementation would query CMDB or asset inventory
        return "MEDIUM";
    }
}
