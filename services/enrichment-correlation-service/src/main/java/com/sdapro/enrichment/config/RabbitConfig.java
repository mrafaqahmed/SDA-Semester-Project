package com.sdapro.enrichment.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PATTERN: Observer (infrastructure configuration)
 * RATIONALE: Configures RabbitMQ listeners for event-driven enrichment pipeline.
 */
@Configuration
public class RabbitConfig {

    public static final String ALERT_QUEUE = "sdapro.alerts";
    public static final String ENRICHED_EVENTS_EXCHANGE = "enrichment-exchange";

    @Bean
    public Queue alertQueue() {
        return new Queue(ALERT_QUEUE, true);
    }

    @Bean
    public DirectExchange enrichmentExchange() {
        return new DirectExchange(ENRICHED_EVENTS_EXCHANGE, true, false);
    }
}
