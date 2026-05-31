package com.sdapro.ingestion.factory;

import com.sdapro.shared.domain.Alert;
import com.sdapro.shared.domain.AlertSourceType;
import java.util.Map;
import java.util.UUID;

public class SplunkNormalizer implements AlertNormalizer {
    @Override
    public Alert normalize(Object rawData) {
        Map<String, Object> splunkEvent = (Map<String, Object>) rawData;

        return Alert.builder()
            .id(UUID.randomUUID())
            .source("SPLUNK")
            .severity((String) splunkEvent.getOrDefault("severity", "LOW"))
            .title((String) splunkEvent.getOrDefault("name", "Unnamed Alert"))
            .description((String) splunkEvent.getOrDefault("search_name", ""))
            .sourceIp((String) splunkEvent.getOrDefault("src_ip", ""))
            .destIp((String) splunkEvent.getOrDefault("dest_ip", ""))
            .user((String) splunkEvent.getOrDefault("user", ""))
            .timestamp(System.currentTimeMillis())
            .rawPayload(splunkEvent.toString())
            .build();
    }

    @Override
    public AlertSourceType getSourceType() {
        return AlertSourceType.SPLUNK;
    }
}
