package com.sdapro.ingestion;

import com.sdapro.ingestion.config.IngestionConfigManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IngestionConfigManagerTest {

    @Test
    public void testSingletonReturnsInstance() {
        IngestionConfigManager manager1 = IngestionConfigManager.getInstance();
        IngestionConfigManager manager2 = IngestionConfigManager.getInstance();
        
        assertSame(manager1, manager2, "Should return same instance");
    }

    @Test
    public void testConfigPersistenceAcrossInstances() {
        IngestionConfigManager manager1 = IngestionConfigManager.getInstance();
        manager1.setConfig("test-key", "test-value");
        
        IngestionConfigManager manager2 = IngestionConfigManager.getInstance();
        assertEquals("test-value", manager2.getConfig("test-key"));
    }

    @Test
    public void testRateLimitConfiguration() {
        IngestionConfigManager manager = IngestionConfigManager.getInstance();
        
        Integer splunkLimit = manager.getRateLimit("SPLUNK");
        assertEquals(1000, splunkLimit);
        
        manager.setRateLimit("SPLUNK", 2000);
        assertEquals(2000, manager.getRateLimit("SPLUNK"));
    }
}
