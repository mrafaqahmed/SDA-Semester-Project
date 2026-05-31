package com.sdapro.enrichment.controller;

import com.sdapro.enrichment.service.EnrichmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * PATTERN: Chain of Responsibility, Observer
 * RATIONALE: REST API for enrichment service. Receives alerts for enrichment.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class EnrichmentController {

    private final EnrichmentService enrichmentService;

    public EnrichmentController(EnrichmentService enrichmentService) {
        this.enrichmentService = enrichmentService;
    }

    @PostMapping("/enrich")
    public ResponseEntity<String> enrichAlert(@RequestBody Map<String, Object> alert) {
        try {
            enrichmentService.enrichAlert(alert.toString());
            return ResponseEntity.ok("Alert enriched successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Enrichment failed: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("enrichment-correlation-service is healthy");
    }
}
