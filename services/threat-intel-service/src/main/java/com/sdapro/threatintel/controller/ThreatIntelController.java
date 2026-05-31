package com.sdapro.threatintel.controller;

import com.sdapro.threatintel.service.ThreatIntelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ThreatIntelController {

    private final ThreatIntelService threatIntelService;

    public ThreatIntelController(ThreatIntelService threatIntelService) {
        this.threatIntelService = threatIntelService;
    }

    @PostMapping("/threat-intel/query")
    public ResponseEntity<Map<String, Object>> queryThreatIntel(@RequestBody Map<String, String> request) {
        String indicator = request.get("indicator");
        if (indicator == null || indicator.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Map<String, Object> result = threatIntelService.checkReputation(indicator);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/threat-intel/check/{indicator}")
    public ResponseEntity<Map<String, Object>> checkIndicator(@PathVariable String indicator) {
        try {
            return ResponseEntity.ok(Map.of(
                "indicator", indicator,
                "isKnownThreat", threatIntelService.isKnownThreat(indicator),
                "confidenceScore", threatIntelService.getConfidenceScore(indicator),
                "details", threatIntelService.checkReputation(indicator)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("threat-intel-service is healthy");
    }
}
