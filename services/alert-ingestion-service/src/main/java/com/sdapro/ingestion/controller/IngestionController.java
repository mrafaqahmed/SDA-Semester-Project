package com.sdapro.ingestion.controller;

import com.sdapro.ingestion.config.IngestionConfigManager;
import com.sdapro.ingestion.factory.AlertNormalizerFactory;
import com.sdapro.shared.domain.Alert;
import com.sdapro.shared.domain.SingleAlert;
import com.sdapro.shared.events.AlertIngestedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Alert Ingestion Controller
 * Receives alerts from multiple sources (Splunk, CrowdStrike, Firewall)
 * Normalizes them using Factory Method pattern
 * Publishes AlertIngestedEvent using Observer pattern
 */
@Slf4j
@RestController
@RequestMapping("/ingest")
public class IngestionController {
    private final RabbitTemplate rabbitTemplate;

    public IngestionController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/{source}")
    public ResponseEntity<?> ingestAlert(@PathVariable String source, @RequestBody Object rawData) {
        try {
            IngestionConfigManager config = IngestionConfigManager.getInstance();

            // Normalize alert using Factory Method pattern
            Alert alert = AlertNormalizerFactory.createNormalizer(source).normalize(rawData);

            // Wrap in Composite pattern (SingleAlert is a leaf node)
            SingleAlert singleAlert = new SingleAlert(alert);

            // Publish AlertIngestedEvent (Observer pattern)
            AlertIngestedEvent event = new AlertIngestedEvent(
                UUID.randomUUID(),
                Instant.now(),
                alert.getId().toString(),
                source,
                alert.getTitle(),
                alert.getSeverity()
            );

            rabbitTemplate.convertAndSend("alerts.exchange", "alert.ingested", event);

            log.info("Alert ingested from {} with ID: {}", source, alert.getId());

            return ResponseEntity.ok()
                .body(new IngestResponse(alert.getId(), "Alert ingested successfully"));
        } catch (Exception e) {
            log.error("Error ingesting alert from {}: {}", source, e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Failed to ingest alert: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok().body(new HealthResponse("UP", "Alert Ingestion Service"));
    }

    @GetMapping("/config")
    public ResponseEntity<?> getConfig() {
        IngestionConfigManager config = IngestionConfigManager.getInstance();
        return ResponseEntity.ok().body(config.getAllConfig());
    }

    @PutMapping("/config/{key}")
    public ResponseEntity<?> setConfig(@PathVariable String key, @RequestBody String value) {
        IngestionConfigManager config = IngestionConfigManager.getInstance();
        config.setConfig(key, value);
        return ResponseEntity.ok().body(new ConfigUpdateResponse(key, value));
    }

    record IngestResponse(UUID alertId, String message) {}
    record ErrorResponse(String error) {}
    record HealthResponse(String status, String service) {}
    record ConfigUpdateResponse(String key, String value) {}
}
