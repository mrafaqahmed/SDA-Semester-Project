# ADR-002: Synchronous vs. Asynchronous Inter-Service Communication

## Status
**ACCEPTED** (Hybrid Approach)

## Context
Services must communicate for alert processing and response coordination. Options:
- **Synchronous (REST/gRPC)**: Immediate response, simpler debugging, but tight coupling
- **Asynchronous (Message Queue)**: Loose coupling, resilience, but eventual consistency complexity
- **Hybrid**: Use both strategically

## Decision
**Hybrid approach**: 
- **Synchronous** for query patterns (e.g., Dashboard → Incident Service)
- **Asynchronous** (RabbitMQ/Kafka) for state-changing events (Alert Ingested, Incident Created, Action Executed)

## Rationale

### Synchronous (REST/gRPC)
Used for:
- `Dashboard → Incident Service`: Fetch incident list
- `Dashboard → Audit Service`: Fetch compliance reports
- `Response Orchestration → Threat Intel`: Reputation checks (but with Proxy caching)
- **Rationale**: Real-time queries don't justify queue complexity; low message volume

### Asynchronous (Event-Driven via Observer)
Used for:
- `Alert Ingestion → Enrichment`: AlertIngested event
- `Enrichment → Incident Management`: IncidentCreated event
- `Response Orchestration → Notification`: ResponseActionExecuted event
- `Incident Management → Audit`: IncidentStateChanged event
- **Rationale**: Decouples publishers from subscribers; multiple services react to same event; Observer pattern naturally implements this

## Event Flow (Observer Pattern)

```
AlertIngestionService publishes AlertIngested
    ↓
RabbitMQ (MessageBus)
    ├→ EnrichmentService (subscriber)
    ├→ DashboardUpdater (Observer)
    └→ AuditLogger (Observer)
```

## Communication Contracts

| Pattern | Example | Transport | Coupling |
|---------|---------|-----------|----------|
| Query | Dashboard → Get Incidents | REST | Loose |
| Command | Response Orchestration → Execute Action | REST | Loose |
| Event | AlertIngested published | RabbitMQ | Decoupled |

## Consequences

### Positive
- ✅ Subscribers added/removed without affecting publishers
- ✅ Backpressure handled by queue (subscribers can lag)
- ✅ Natural fit for Observer and Pub-Sub patterns
- ✅ Queries remain fast (synchronous)

### Negative
- ❌ Eventual consistency: dashboard may not immediately show enriched alerts
- ❌ Duplicate processing possible if subscriber fails mid-event
- ❌ Requires idempotency in event handlers

## Implementation

### Synchronous (Spring RestTemplate / WebClient)
```java
// Dashboard queries incident service
restTemplate.getForObject("http://incident-service/incidents", List.class);
```

### Asynchronous (RabbitMQ via Observer)
```java
// EventBusPublisher notifies all subscribers
eventBus.publishAlertIngested(alert);
```

## Related
- ADR-001: Service boundaries
- ADR-004: Threat intel caching (reduces sync calls via Proxy)
