package com.sdapro.incident;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Incident Management Service - Primary responsibility: Student A
 *
 * PATTERN: State
 * Tracks incidents through 7-state lifecycle with state-dependent behavior.
 */
@SpringBootApplication
public class IncidentApplication {
    public static void main(String[] args) {
        SpringApplication.run(IncidentApplication.class, args);
    }
}
