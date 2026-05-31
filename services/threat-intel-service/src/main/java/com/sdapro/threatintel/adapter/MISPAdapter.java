package com.sdapro.threatintel.adapter;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MISPAdapter implements ThreatIntelProvider {
    @Override
    public String getProviderName() {
        return "MISP";
    }

    @Override
    public Map<String, Object> checkReputation(String indicator) {
        Map<String, Object> result = new HashMap<>();
        result.put("provider", "MISP");
        result.put("indicator", indicator);
        result.put("events", 12);
        result.put("last_seen", "2026-05-28");
        result.put("threat_level", "High");
        log.debug("MISP check for indicator: {}", indicator);
        return result;
    }

    @Override
    public double getConfidenceScore(String indicator) {
        return 0.78;
    }

    @Override
    public boolean isKnownThreat(String indicator) {
        return getConfidenceScore(indicator) > 0.5;
    }
}
