package com.sdapro.ingestion.service;

import com.sdapro.ingestion.domain.AlertEntity;
import com.sdapro.ingestion.repo.AlertRepository;
import com.sdapro.domain.*;
import com.sdapro.events.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * PATTERN: Factory Method, Chain of Responsibility, Observer
 * RATIONALE: Uses AlertNormalizerFactory to normalize different alert sources.
 * Applies enrichment handler chain. Publishes events to subscribers.
 */
@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final RabbitTemplate rabbitTemplate;

    public AlertService(AlertRepository alertRepository, RabbitTemplate rabbitTemplate) {
        this.alertRepository = alertRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public AlertEntity ingest(Map<String, Object> payload) {
        String source = String.valueOf(payload.getOrDefault("source", "splunk"));
        String severity = String.valueOf(payload.getOrDefault("severity", "MEDIUM"));
        String status = String.valueOf(payload.getOrDefault("status", "OPEN"));
        String rawPayload = payload.toString();

        // PATTERN: Factory Method - Get appropriate normalizer
        AlertNormalizer normalizer = AlertNormalizerFactory.getNormalizer(source);
        Alert alert = normalizer.normalize(payload);

        // PATTERN: Chain of Responsibility - Apply enrichment pipeline
        EnrichmentHandler pipeline = buildEnrichmentPipeline();
        pipeline.handle(alert);

        // Save entity
        AlertEntity entity = new AlertEntity(UUID.randomUUID(), source, severity, status, rawPayload);
        AlertEntity saved = alertRepository.save(entity);

        // PATTERN: Observer - Publish event to subscribers
        publishAlertIngestedEvent(alert, saved);

        return saved;
    }

    private EnrichmentHandler buildEnrichmentPipeline() {
        EnrichmentHandler deduplication = new DeduplicationHandler();
        EnrichmentHandler geoip = new GeoIPEnrichmentHandler();
        EnrichmentHandler threatIntel = new ThreatIntelEnrichmentHandler();
        EnrichmentHandler assetContext = new AssetContextHandler();
        EnrichmentHandler classification = new ClassificationHandler();

        deduplication.setNext(geoip);
        geoip.setNext(threatIntel);
        threatIntel.setNext(assetContext);
        assetContext.setNext(classification);

        return deduplication;
    }

    private void publishAlertIngestedEvent(Alert alert, AlertEntity entity) {
        AlertIngestedEvent event = new AlertIngestedEvent(
            entity.getId().toString(),
            alert.getSource(),
            alert.getSeverity().toString()
        );
        rabbitTemplate.convertAndSend("incident-events", event);
    }
}

