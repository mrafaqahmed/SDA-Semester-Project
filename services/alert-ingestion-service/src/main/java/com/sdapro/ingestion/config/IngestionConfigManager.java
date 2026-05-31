package com.sdapro.ingestion.config;

import java.util.HashMap;
import java.util.Map;

/**
 * PATTERN: Singleton
 * RATIONALE: Configuration management needs single instance to ensure consistent behavior
 * across all ingestion operations. Prevents multiple config instances from conflicting.
 */
public class IngestionConfigManager {
    private static IngestionConfigManager instance;
    private final Map<String, String> config = new HashMap<>();
    private final Map<String, Integer> rateLimits = new HashMap<>();

    private IngestionConfigManager() {
        initializeDefaults();
    }

    public static synchronized IngestionConfigManager getInstance() {
        if (instance == null) {
            instance = new IngestionConfigManager();
        }
        return instance;
    }

    private void initializeDefaults() {
        config.put("ingestion.batch.size", "100");
        config.put("ingestion.timeout.seconds", "30");
        config.put("ingestion.retry.attempts", "3");
        config.put("ingestion.alert.ttl.hours", "72");

        rateLimits.put("SPLUNK", 1000);
        rateLimits.put("CROWDSTRIKE", 500);
        rateLimits.put("FIREWALL", 2000);
    }

    public String getConfig(String key) {
        return config.getOrDefault(key, null);
    }

    public void setConfig(String key, String value) {
        config.put(key, value);
    }

    public Integer getRateLimit(String source) {
        return rateLimits.getOrDefault(source, 100);
    }

    public void setRateLimit(String source, Integer limit) {
        rateLimits.put(source, limit);
    }

    public Map<String, String> getAllConfig() {
        return new HashMap<>(config);
    }
}
