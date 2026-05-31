package com.sdapro.incident.listener;

import com.sdapro.incident.service.IncidentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AlertEventListener {

    private final IncidentService incidentService;

    public AlertEventListener(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @RabbitListener(queues = "sdapro.alerts")
    public void onAlert(String message) {
        incidentService.createIncident(
                "Generated from alert ingestion",
                "OPEN",
                "HIGH",
                message
        );
    }
}
