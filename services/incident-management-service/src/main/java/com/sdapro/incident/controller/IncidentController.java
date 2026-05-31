package com.sdapro.incident.controller;

import com.sdapro.incident.domain.Incident;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Incident Management Controller
 * Uses State pattern for incident lifecycle management
 */
@Slf4j
@RestController
@RequestMapping("/incidents")
public class IncidentController {
    private final Map<UUID, Incident> incidentStore = new HashMap<>();

    @PostMapping
    public ResponseEntity<?> createIncident(@RequestBody CreateIncidentRequest request) {
        Incident incident = new Incident(request.title(), request.description(), request.severity());
        incidentStore.put(incident.getId(), incident);
        log.info("Incident created: {} with state: {}", incident.getId(), incident.getCurrentState());
        return ResponseEntity.ok(new IncidentResponse(incident));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIncident(@PathVariable UUID id) {
        Incident incident = incidentStore.get(id);
        if (incident == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new IncidentResponse(incident));
    }

    @PutMapping("/{id}/triage")
    public ResponseEntity<?> beginTriage(@PathVariable UUID id) {
        Incident incident = incidentStore.get(id);
        if (incident == null) {
            return ResponseEntity.notFound().build();
        }
        incident.beginTriage();
        return ResponseEntity.ok(new IncidentResponse(incident));
    }

    @PutMapping("/{id}/containment")
    public ResponseEntity<?> initiateContainment(@PathVariable UUID id) {
        Incident incident = incidentStore.get(id);
        if (incident == null) {
            return ResponseEntity.notFound().build();
        }
        incident.initiateContainment();
        return ResponseEntity.ok(new IncidentResponse(incident));
    }

    @PutMapping("/{id}/eradication")
    public ResponseEntity<?> initiateEradication(@PathVariable UUID id) {
        Incident incident = incidentStore.get(id);
        if (incident == null) {
            return ResponseEntity.notFound().build();
        }
        incident.initiateEradication();
        return ResponseEntity.ok(new IncidentResponse(incident));
    }

    @PutMapping("/{id}/recovery")
    public ResponseEntity<?> initiateRecovery(@PathVariable UUID id) {
        Incident incident = incidentStore.get(id);
        if (incident == null) {
            return ResponseEntity.notFound().build();
        }
        incident.initiateRecovery();
        return ResponseEntity.ok(new IncidentResponse(incident));
    }

    @PutMapping("/{id}/pir")
    public ResponseEntity<?> completePIR(@PathVariable UUID id) {
        Incident incident = incidentStore.get(id);
        if (incident == null) {
            return ResponseEntity.notFound().build();
        }
        incident.completePostIncidentReview();
        return ResponseEntity.ok(new IncidentResponse(incident));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<?> closeIncident(@PathVariable UUID id) {
        Incident incident = incidentStore.get(id);
        if (incident == null) {
            return ResponseEntity.notFound().build();
        }
        incident.close();
        return ResponseEntity.ok(new IncidentResponse(incident));
    }

    @GetMapping("/{id}/allowed-actions")
    public ResponseEntity<?> getAllowedActions(@PathVariable UUID id) {
        Incident incident = incidentStore.get(id);
        if (incident == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of(
            "incidentId", id,
            "currentState", incident.getCurrentState(),
            "allowedActions", incident.getAllowedActions()
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "Incident Management"));
    }

    record CreateIncidentRequest(String title, String description, String severity) {}
    record IncidentResponse(Incident incident) {}
}
