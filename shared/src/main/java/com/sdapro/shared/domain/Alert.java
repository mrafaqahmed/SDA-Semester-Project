package com.sdapro.shared.domain;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PATTERN: Composite
 * Canonical alert schema - normalized from all sources
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {
    private UUID id;
    private String source;           // splunk, crowdstrike, firewall, etc.
    private String severity;          // CRITICAL, HIGH, MEDIUM, LOW
    private String title;
    private String description;
    private String sourceIp;
    private String destIp;
    private String user;
    private Long timestamp;
    private String rawPayload;

    // Enrichment fields (populated later)
    private String geoLocation;
    private String threatReputation;
    private String assetCriticality;
    private String classification;

    public static Alert normalize(String source, Object rawData) {
        // Implemented by AlertNormalizerFactory
        return Alert.builder()
            .id(UUID.randomUUID())
            .source(source)
            .timestamp(System.currentTimeMillis())
            .build();
    }
}
