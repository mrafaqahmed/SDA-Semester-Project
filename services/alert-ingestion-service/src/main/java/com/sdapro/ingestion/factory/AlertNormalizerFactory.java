package com.sdapro.ingestion.factory;

import com.sdapro.shared.domain.AlertSourceType;
import java.util.HashMap;
import java.util.Map;

/**
 * PATTERN: Factory Method
 * RATIONALE: Creates appropriate AlertNormalizer implementations based on source type.
 * Centralizes object creation and makes it easy to add new source types.
 */
public class AlertNormalizerFactory {
    private static final Map<AlertSourceType, AlertNormalizer> normalizers = new HashMap<>();

    static {
        normalizers.put(AlertSourceType.SPLUNK, new SplunkNormalizer());
        normalizers.put(AlertSourceType.CROWDSTRIKE, new CrowdStrikeNormalizer());
        normalizers.put(AlertSourceType.FIREWALL, new FirewallNormalizer());
    }

    public static AlertNormalizer createNormalizer(AlertSourceType sourceType) {
        AlertNormalizer normalizer = normalizers.get(sourceType);
        if (normalizer == null) {
            throw new IllegalArgumentException("No normalizer registered for source type: " + sourceType);
        }
        return normalizer;
    }

    public static AlertNormalizer createNormalizer(String sourceTypeStr) {
        try {
            AlertSourceType sourceType = AlertSourceType.valueOf(sourceTypeStr.toUpperCase());
            return createNormalizer(sourceType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown alert source: " + sourceTypeStr);
        }
    }

    public static void registerNormalizer(AlertSourceType sourceType, AlertNormalizer normalizer) {
        normalizers.put(sourceType, normalizer);
    }
}
