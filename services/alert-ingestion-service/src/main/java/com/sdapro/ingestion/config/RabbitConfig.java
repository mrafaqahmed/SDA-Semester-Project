package com.sdapro.ingestion.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PATTERN: Observer (infrastructure configuration)
 * RATIONALE: Configures RabbitMQ queues and exchanges for event-driven architecture.
 * Services publish domain events asynchronously to decouple components.
 */
@Configuration
public class RabbitConfig {

    public static final String ALERT_QUEUE = "sdapro.alerts";
    public static final String INCIDENT_EVENTS_QUEUE = "incident-events";
    public static final String INCIDENT_EVENTS_EXCHANGE = "incident-exchange";

    @Bean
    public Queue alertQueue() {
        return new Queue(ALERT_QUEUE, true);
    }

    @Bean
    public Queue incidentEventsQueue() {
        return new Queue(INCIDENT_EVENTS_QUEUE, true);
    }

    @Bean
    public DirectExchange incidentExchange() {
        return new DirectExchange(INCIDENT_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Binding incidentBinding(Queue incidentEventsQueue, DirectExchange incidentExchange) {
        return BindingBuilder.bind(incidentEventsQueue)
                .to(incidentExchange)
                .with("incident.*");
    }
}

