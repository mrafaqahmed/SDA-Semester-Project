package com.sdapro.enrichment.handler;

import com.sdapro.shared.domain.Alert;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassificationHandler extends EnrichmentHandler {
    @Override
    protected void doEnrich(Alert alert) {
        // Classify alert based on title, description, source
        String classification = classifyAlert(alert);
        alert.setClassification(classification);
        log.debug("Alert classified as: {}", classification);
    }

    private String classifyAlert(Alert alert) {
        String title = alert.getTitle().toLowerCase();

        if (title.contains("malware") || title.contains("trojan")) {
            return "MALWARE";
        } else if (title.contains("exploit") || title.contains("vulnerability")) {
            return "EXPLOITATION";
        } else if (title.contains("brute") || title.contains("login")) {
            return "CREDENTIAL_ATTACK";
        } else if (title.contains("ddos") || title.contains("flood")) {
            return "DENIAL_OF_SERVICE";
        } else {
            return "SUSPICIOUS_ACTIVITY";
        }
    }
}
