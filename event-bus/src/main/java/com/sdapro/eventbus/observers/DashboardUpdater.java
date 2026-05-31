package com.sdapro.eventbus.observers;

import com.sdapro.eventbus.EventBusPublisher;
import com.sdapro.shared.events.DomainEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * PATTERN: Observer
 * Notifies dashboard of real-time updates via WebSocket/SSE
 */
@Slf4j
public class DashboardUpdater implements EventBusPublisher.EventSubscriber {
    @Override
    public void onEvent(DomainEvent event) {
        log.info("[DASHBOARD] Broadcasting event {} to connected clients", event.getClass().getSimpleName());
        // In real implementation, would emit WebSocket message
        // websocketSession.sendMessage(event);
    }
}
