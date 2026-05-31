package com.sdapro.threatintel.adapter;

import java.util.Map;

/**
 * PATTERN: Adapter
 * RATIONALE: Different threat intel sources (VirusTotal, MISP, custom) have
 * different APIs. Adapter unifies them under a common interface.
 */
public interface ThreatIntelProvider {
    String getProviderName();
    Map<String, Object> checkReputation(String indicator);
    double getConfidenceScore(String indicator);
    boolean isKnownThreat(String indicator);
}
