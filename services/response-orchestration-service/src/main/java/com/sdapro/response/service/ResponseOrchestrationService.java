package com.sdapro.response.service;

import com.sdapro.domain.*;
import com.sdapro.response.decorator.AuditLogDecorator;
import com.sdapro.response.decorator.ApprovalGateDecorator;
import com.sdapro.response.decorator.MetricsDecorator;
import com.sdapro.shared.events.IncidentStateChangedEvent;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.*;
import java.util.UUID;

/**
 * PATTERN: Facade
 * RATIONALE: Orchestrates response by coordinating:
 * - State validation
 * - Strategy selection  
 * - Action factory
 * - Decorator stacking
 * - Execution
 * - Event publishing
 */
@Service
public class ResponseOrchestrationService {

    private final RabbitTemplate rabbitTemplate;
    private final ThreatIntelService threatIntelService;

    public ResponseOrchestrationService(RabbitTemplate rabbitTemplate, ThreatIntelService threatIntelService) {
        this.rabbitTemplate = rabbitTemplate;
        this.threatIntelService = threatIntelService;
    }

    /**
     * PATTERN: Facade - Single entry point for response orchestration
     */
    public void assessAndRespond(Incident incident) {
        // Step 1: Select strategy based on severity
        ResponseStrategy strategy = selectStrategy(incident);
        
        // Step 2: Get response actions from strategy
        List<ResponseAction> actions = strategy.selectActions(incident);
        
        // Step 3: Decorate actions with approval, audit, metrics
        List<ResponseAction> decoratedActions = decorateActions(actions);
        
        // Step 4: Execute actions
        for (ResponseAction action : decoratedActions) {
            ResponseActionResult result = action.execute(incident);
            if (!result.isSuccess()) {
                // Rollback on failure
                action.rollback(incident);
            }
        }
        
        // Step 5: Publish response event
        publishResponseEvent(incident);
    }

    /**
     * PATTERN: Strategy
     * RATIONALE: Select response algorithm based on severity and criticality
     */
    private ResponseStrategy selectStrategy(Incident incident) {
        switch (incident.getSeverity()) {
            case CRITICAL:
                return new AggressiveResponseStrategy();
            case HIGH:
                return new BalancedResponseStrategy();
            case MEDIUM:
                return new ConservativeResponseStrategy();
            default:
                return new WatchAndWaitResponseStrategy();
        }
    }

    /**
     * PATTERN: Decorator
     * RATIONALE: Stack decorators to add approval, audit, logging
     */
    private List<ResponseAction> decorateActions(List<ResponseAction> baseActions) {
        List<ResponseAction> decorated = new ArrayList<>();
        for (ResponseAction action : baseActions) {
            ResponseAction decoratedAction = action;
            // Stack decorators
            decoratedAction = new AuditLogDecorator(decoratedAction);
            decoratedAction = new ApprovalGateDecorator(decoratedAction);
            decoratedAction = new MetricsDecorator(decoratedAction);
            decorated.add(decoratedAction);
        }
        return decorated;
    }

    private void publishResponseEvent(Incident incident) {
        IncidentStateChangedEvent event = new IncidentStateChangedEvent(
            UUID.randomUUID(), java.time.Instant.now(), incident.getId().toString(),
            "Triage", "Response-Executing", "Automated response started");
        rabbitTemplate.convertAndSend("incident-events", event);
    }
}
