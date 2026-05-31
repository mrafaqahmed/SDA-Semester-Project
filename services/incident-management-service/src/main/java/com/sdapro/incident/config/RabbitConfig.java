package com.sdapro.incident.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String ALERT_QUEUE = "sdapro.alerts";

    @Bean
    public Queue alertQueue() {
        return new Queue(ALERT_QUEUE, true);
    }
}
