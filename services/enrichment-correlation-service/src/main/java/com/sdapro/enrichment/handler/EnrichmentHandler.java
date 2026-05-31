package com.sdapro.enrichment.handler;

import com.sdapro.shared.domain.Alert;

/**
 * PATTERN: Chain of Responsibility
 * RATIONALE: Multiple enrichment operations must be applied in sequence.
 * Each handler processes alert and passes to next handler in chain.
 */
public abstract class EnrichmentHandler {
    protected EnrichmentHandler next;

    public void setNext(EnrichmentHandler next) {
        this.next = next;
    }

    public final void handle(Alert alert) {
        doEnrich(alert);
        if (next != null) {
            next.handle(alert);
        }
    }

    protected abstract void doEnrich(Alert alert);
}
