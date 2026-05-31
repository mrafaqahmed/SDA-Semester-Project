package com.sdapro.ingestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Alert Ingestion Service - Primary responsibility: Student A
 *
 * PATTERNS IMPLEMENTED:
 * - Singleton: IngestionConfigManager
 * - Factory Method: AlertNormalizerFactory
 * - Composite: Alert tree structure (SingleAlert, AlertCampaign, IncidentCluster)
 *
 * ARCHITECTURE:
 * - Layered: Controllers → Services → Repositories
 * - SOA: Independent service with REST API
 * - Event-Driven: Publishes AlertIngested events to RabbitMQ
 *
 * FUNCTIONAL SCOPE:
 * Ingest security alerts from multiple sources (Splunk, CrowdStrike, Firewall, custom feeds)
 * Normalize raw alerts into canonical Alert schema regardless of source format.
 * Support both push (webhooks) and pull (polling APIs) ingestion modes.
 *
 * PORT: 8080
 * API: POST /ingest/{source} - Receive alert from source
 *      GET /alerts - Retrieve alert history
 */
@SpringBootApplication
@ComponentScan("com.sdapro")
public class AlertIngestionApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertIngestionApplication.class, args);
    }
}
