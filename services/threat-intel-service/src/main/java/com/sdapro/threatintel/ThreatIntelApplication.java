package com.sdapro.threatintel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Threat Intelligence Service - Primary responsibility: Student B
 *
 * PATTERNS: Adapter, Proxy
 * Integrates external threat intel (VirusTotal, MISP) with caching and rate limiting.
 */
@SpringBootApplication
public class ThreatIntelApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThreatIntelApplication.class, args);
    }
}
