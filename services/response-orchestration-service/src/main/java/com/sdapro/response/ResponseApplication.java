package com.sdapro.response;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Response Orchestration Service - Primary responsibility: Student B
 *
 * PATTERNS: Facade, Strategy, Decorator, Proxy
 * Orchestrates automated incident response actions with approval gates and rollback.
 */
@SpringBootApplication
public class ResponseApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResponseApplication.class, args);
    }
}
