package com.sdapro.enrichment.service;

import com.sdapro.enrichment.handler.*;
import com.sdapro.shared.domain.Alert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Enrichment Pipeline using Chain of Responsibility
 * Orchestrates multiple enrichment handlers in sequence
 */
@Slf4j
@Service
public class EnrichmentService {
    private final EnrichmentHandler enrichmentChain;

    public EnrichmentService() {
        // Build the chain in order
        EnrichmentHandler dedup = new DeduplicationHandler();
        EnrichmentHandler geoip = new GeoIPHandler();
        EnrichmentHandler threatIntel = new ThreatIntelHandler();
        EnrichmentHandler assetContext = new AssetContextHandler();
        EnrichmentHandler classification = new ClassificationHandler();

        // Link handlers
        dedup.setNext(geoip);
        geoip.setNext(threatIntel);
        threatIntel.setNext(assetContext);
        assetContext.setNext(classification);

        this.enrichmentChain = dedup;
    }

    public Alert enrichAlert(Alert alert) {
        log.info("Starting enrichment pipeline for alert: {}", alert.getId());
        enrichmentChain.handle(alert);
        log.info("Enrichment pipeline completed for alert: {}", alert.getId());
        return alert;
    }
}
