package com.sdapro.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Audit Service - Primary responsibility: Student C
 *
 * PATTERN: Observer (consumer)
 * Listens to all domain events and logs immutably for compliance (GDPR, SOC2).
 */
@SpringBootApplication
public class AuditApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuditApplication.class, args);
    }
}
