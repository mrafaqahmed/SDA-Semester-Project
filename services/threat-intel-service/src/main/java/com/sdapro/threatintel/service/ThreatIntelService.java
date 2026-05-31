package com.sdapro.threatintel.service;

import com.sdapro.threatintel.adapter.ThreatIntelProvider;
import com.sdapro.threatintel.adapter.VirusTotalAdapter;
import com.sdapro.threatintel.adapter.MISPAdapter;
import com.sdapro.threatintel.proxy.ThreatIntelProxy;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * PATTERN: Adapter, Proxy
 * RATIONALE: Integrates multiple threat intelligence providers (VirusTotal, MISP).
 * Uses Adapter pattern to normalize different APIs.
 * Uses Proxy pattern for caching with TTL.
 */
@Service
public class ThreatIntelService {

    private final ThreatIntelProxy cachedProvider;

    public ThreatIntelService() {
        ThreatIntelProvider virusTotalAdapter = new VirusTotalAdapter();
        this.cachedProvider = new ThreatIntelProxy(virusTotalAdapter);
    }

    public Map<String, Object> checkReputation(String indicator) {
        return cachedProvider.checkReputation(indicator);
    }

    public boolean isKnownThreat(String indicator) {
        return cachedProvider.isKnownThreat(indicator);
    }

    public double getConfidenceScore(String indicator) {
        return cachedProvider.getConfidenceScore(indicator);
    }
}
