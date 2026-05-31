package com.sdapro.eventbus;

import com.sdapro.shared.events.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PATTERN: Singleton + Observer
 * RATIONALE: Single event bus instance ensures all events flow through one channel.
 * Subscribers register for specific event types.
 */
@Slf4j
public class EventBusPublisher {
    private static EventBusPublisher instance;
    private final Map<String, List<EventSubscriber>> subscribers = new HashMap<>();

    private EventBusPublisher() {
    }

    public static synchronized EventBusPublisher getInstance() {
        if (instance == null) {
            instance = new EventBusPublisher();
        }
        return instance;
    }

    public void subscribe(String eventType, EventSubscriber subscriber) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(subscriber);
        log.info("Subscriber registered for event type: {}", eventType);
    }

    public void publish(DomainEvent event) {
        String eventType = event.getClass().getSimpleName();
        log.info("Publishing event: {}", eventType);

        List<EventSubscriber> eventSubscribers = subscribers.get(eventType);
        if (eventSubscribers != null) {
            for (EventSubscriber subscriber : eventSubscribers) {
                try {
                    subscriber.onEvent(event);
                } catch (Exception e) {
                    log.error("Error notifying subscriber of event {}: {}", eventType, e.getMessage());
                }
            }
        } else {
            log.debug("No subscribers for event type: {}", eventType);
        }
    }

    public interface EventSubscriber {
        void onEvent(DomainEvent event);
    }
}
