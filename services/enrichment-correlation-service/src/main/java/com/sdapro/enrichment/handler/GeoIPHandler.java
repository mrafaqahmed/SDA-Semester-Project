package com.sdapro.enrichment.handler;

import com.sdapro.shared.domain.Alert;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeoIPHandler extends EnrichmentHandler {
    @Override
    protected void doEnrich(Alert alert) {
        if (alert.getSourceIp() != null && !alert.getSourceIp().isEmpty()) {
            String geoLocation = lookupGeoIP(alert.getSourceIp());
            alert.setGeoLocation(geoLocation);
            log.debug("Enriched source IP {} with geolocation: {}", alert.getSourceIp(), geoLocation);
        }

        if (alert.getDestIp() != null && !alert.getDestIp().isEmpty()) {
            String geoLocation = lookupGeoIP(alert.getDestIp());
            log.debug("Destination IP {} maps to: {}", alert.getDestIp(), geoLocation);
        }
    }

    private String lookupGeoIP(String ip) {
        // Implementation would call MaxMind or similar GeoIP service
        return "Unknown Location (IP: " + ip + ")";
    }
}
