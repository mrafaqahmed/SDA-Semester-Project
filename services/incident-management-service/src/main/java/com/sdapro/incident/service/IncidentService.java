package com.sdapro.incident.service;

import com.sdapro.incident.domain.IncidentEntity;
import com.sdapro.incident.repo.IncidentRepository;
import com.sdapro.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.sdapro.events.*;

import java.util.List;
import java.util.UUID;

/**
 * PATTERN: Facade, State, Strategy
 * RATIONALE: Service orchestrates incident lifecycle with state management
 * and strategy-based response selection
 */
@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final RabbitTemplate rabbitTemplate;

    public IncidentService(IncidentRepository incidentRepository, RabbitTemplate rabbitTemplate) {
        this.incidentRepository = incidentRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<IncidentEntity> findAll() {
        return incidentRepository.findAll();
    }

    public IncidentEntity createIncident(String title, String status, String priority, String details) {
        IncidentEntity incident = new IncidentEntity(UUID.randomUUID(), title, status, priority, details);
        IncidentEntity saved = incidentRepository.save(incident);
        
        // PATTERN: Observer - Publish IncidentCreatedEvent
        publishEvent(new IncidentCreatedEvent(
            saved.getId().toString(),
            title,
            status,
            priority
        ));
        
        return saved;
    }

    public IncidentEntity transitionState(UUID incidentId, String targetState) {
        IncidentEntity entity = incidentRepository.findById(incidentId)
            .orElseThrow(() -> new IllegalArgumentException("Incident not found"));
        
        String previousState = entity.getStatus();
        entity.setStatus(targetState);
        IncidentEntity updated = incidentRepository.save(entity);
        
        // PATTERN: Observer - Publish state change event
        publishEvent(new IncidentStateChangedEvent(
            incidentId.toString(),
            previousState,
            targetState
        ));
        
        return updated;
    }

    private void publishEvent(DomainEvent event) {
        rabbitTemplate.convertAndSend("incident-events", event);
    }
}
