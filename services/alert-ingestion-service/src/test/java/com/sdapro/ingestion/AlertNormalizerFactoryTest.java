package com.sdapro.ingestion;

import com.sdapro.ingestion.factory.*;
import com.sdapro.shared.domain.Alert;
import com.sdapro.shared.domain.AlertSourceType;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AlertNormalizerFactoryTest {

    @Test
    public void testFactoryCreatesCorrectNormalizer() {
        AlertNormalizer splunkNormalizer = AlertNormalizerFactory.createNormalizer(AlertSourceType.SPLUNK);
        assertNotNull(splunkNormalizer);
        assertEquals(AlertSourceType.SPLUNK, splunkNormalizer.getSourceType());
    }

    @Test
    public void testSplunkNormalizerNormalizesCorrectly() {
        AlertNormalizer normalizer = AlertNormalizerFactory.createNormalizer(AlertSourceType.SPLUNK);
        
        Map<String, Object> rawData = new HashMap<>();
        rawData.put("severity", "CRITICAL");
        rawData.put("name", "Test Alert");
        rawData.put("src_ip", "192.168.1.100");
        rawData.put("dest_ip", "10.0.0.1");
        
        Alert alert = normalizer.normalize(rawData);
        
        assertEquals("CRITICAL", alert.getSeverity());
        assertEquals("Test Alert", alert.getTitle());
        assertEquals("192.168.1.100", alert.getSourceIp());
        assertEquals("SPLUNK", alert.getSource());
    }

    @Test
    public void testCrowdStrikeNormalizerNormalizesCorrectly() {
        AlertNormalizer normalizer = AlertNormalizerFactory.createNormalizer(AlertSourceType.CROWDSTRIKE);
        
        Map<String, Object> rawData = new HashMap<>();
        rawData.put("severity", "HIGH");
        rawData.put("event_type", "Detection");
        rawData.put("detection_external_ip", "203.0.113.45");
        
        Alert alert = normalizer.normalize(rawData);
        
        assertEquals("HIGH", alert.getSeverity());
        assertEquals("Detection", alert.getTitle());
        assertEquals("CROWDSTRIKE", alert.getSource());
    }

    @Test
    public void testFactoryThrowsExceptionForUnknownSource() {
        assertThrows(IllegalArgumentException.class, 
            () -> AlertNormalizerFactory.createNormalizer("UNKNOWN_SOURCE"));
    }
}
