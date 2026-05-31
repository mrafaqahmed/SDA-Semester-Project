package com.sdapro.ingestion.factory;

import com.sdapro.shared.domain.Alert;
import com.sdapro.shared.domain.AlertSourceType;
import java.util.Map;
import java.util.UUID;

public class CrowdStrikeNormalizer implements AlertNormalizer {
    @Override
    public Alert normalize(Object rawData) {
        Map<String, Object> csEvent = (Map<String, Object>) rawData;

        return Alert.builder()
            .id(UUID.randomUUID())
            .source("CROWDSTRIKE")
            .severity((String) csEvent.getOrDefault("severity", "MEDIUM"))
            .title((String) csEvent.getOrDefault("event_type", "Detection"))
            .description((String) csEvent.getOrDefault("detection_name", ""))
            .sourceIp((String) csEvent.getOrDefault("detection_external_ip", ""))
            .destIp((String) csEvent.getOrDefault("host_ip", ""))
            .user((String) csEvent.getOrDefault("user_id", ""))
            .timestamp(System.currentTimeMillis())
            .rawPayload(csEvent.toString())
            .build();
    }

    @Override
    public AlertSourceType getSourceType() {
        return AlertSourceType.CROWDSTRIKE;
    }
}
