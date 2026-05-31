package com.sdapro.ingestion.factory;

import com.sdapro.shared.domain.Alert;
import com.sdapro.shared.domain.AlertSourceType;
import java.util.Map;
import java.util.UUID;

public class FirewallNormalizer implements AlertNormalizer {
    @Override
    public Alert normalize(Object rawData) {
        Map<String, Object> fwEvent = (Map<String, Object>) rawData;

        return Alert.builder()
            .id(UUID.randomUUID())
            .source("FIREWALL")
            .severity((String) fwEvent.getOrDefault("priority", "MEDIUM"))
            .title((String) fwEvent.getOrDefault("action", "Blocked"))
            .description((String) fwEvent.getOrDefault("rule_name", ""))
            .sourceIp((String) fwEvent.getOrDefault("src", ""))
            .destIp((String) fwEvent.getOrDefault("dst", ""))
            .user("")
            .timestamp(System.currentTimeMillis())
            .rawPayload(fwEvent.toString())
            .build();
    }

    @Override
    public AlertSourceType getSourceType() {
        return AlertSourceType.FIREWALL;
    }
}
