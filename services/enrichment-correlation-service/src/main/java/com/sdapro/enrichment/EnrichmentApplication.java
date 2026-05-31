package com.sdapro.enrichment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Enrichment & Correlation Service - Primary responsibility: Student A
 *
 * PATTERNS: Chain of Responsibility, Composite, Strategy, Abstract Factory
 * Enriches alerts through configurable pipeline (5 handlers).
 * Correlates individual alerts into security incidents.
 */
@SpringBootApplication
public class EnrichmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnrichmentApplication.class, args);
    }
}
