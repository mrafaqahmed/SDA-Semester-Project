# ADR-005: Real-Time Push Strategy

## Status
**ACCEPTED**

## Context
SOC Dashboard must display real-time updates when incidents change state or new alerts arrive (requirement: < 2 second latency). Options:
- **Polling**: Dashboard polls every N seconds; simple, stateless, but wasted requests
- **Server-Sent Events (SSE)**: Unidirectional server → browser stream; simpler than WebSockets, built-in reconnect
- **WebSockets**: Bidirectional persistent connection; lower latency, but stateful servers, harder to scale
- **Long polling**: Browser polls, waits for response; compromise, but battery-draining on mobile

## Decision
**Server-Sent Events (SSE)** with fallback to short polling

## Rationale

### SSE (Primary)
- **Unidirectional**: Server → Browser (dashboard only listens, doesn't send)
- **HTTP/1.1**: Works on HTTP, no special protocol negotiation
- **Auto-reconnect**: Built-in browser support, handles temporary disconnections
- **Lower latency**: < 200ms typical (vs. polling every 2s)
- **Stateless**: Server can scale horizontally; load balancer routes event to any instance
- **Graceful degradation**: Falls back to polling if unsupported

**Implementation**:
```java
@GetMapping("/incidents/stream")
public SseEmitter getIncidentStream() {
  SseEmitter emitter = new SseEmitter();
  eventBus.subscribe("IncidentStateChanged", (event) -> {
    emitter.send(SseEmitter.event()
      .id(event.id)
      .name("incident-updated")
      .data(event)
      .reconnectTime(5000)
    );
  });
  return emitter;
}
```

**Browser**:
```javascript
const source = new EventSource("/incidents/stream");
source.addEventListener("incident-updated", (e) => {
  updateIncidentUI(JSON.parse(e.data));
});
```

### When to Use Short Polling (Fallback)
If SSE unsupported (very old browsers, certain firewalls):
```javascript
setInterval(() => {
  fetch("/incidents/list")
    .then(r => r.json())
    .then(incidents => updateUI(incidents));
}, 2000);  // Poll every 2 seconds
```

## Architecture

```
EventBusPublisher (Singleton)
  ↓ (publishes IncidentStateChanged event)
RabbitMQ
  ↓ (DashboardUpdater observer subscribes)
DashboardUpdater
  ├→ Broadcast to all SSE emitters in memory
  └→ Or trigger polling clients to refresh
  
SseEmitter Registry:
[analyst_123 → SseEmitter1, analyst_456 → SseEmitter2]
```

## Event Types Pushed

| Event | Source | Latency Target |
|-------|--------|-----------------|
| `IncidentCreated` | Incident Management | < 500ms |
| `IncidentStateChanged` | Incident Management | < 500ms |
| `ResponseActionExecuted` | Response Orchestration | < 500ms |
| `AlertEnriched` | Enrichment Service | < 1s |

## Consequences

### Positive
- ✅ Real-time without polling overhead
- ✅ Stateless: scales horizontally with load balancer
- ✅ Automatic reconnection browser support
- ✅ Lower bandwidth than polling
- ✅ Graceful fallback to polling

### Negative
- ❌ One-way only (browser can't initiate updates)
- ❌ Connection limits per domain (browsers limit ~6 concurrent SSE per domain)
- ❌ Older browsers (IE < 10) not supported (use polling fallback)

## Scaling Considerations

### Single Server
```
DashboardController
  └→ SseEmitterRegistry (in-memory)
     └→ [analyst_1 → SseEmitter, ...]
```

### Multiple Servers (Load Balanced)
```
Load Balancer
  ├→ Server A
  │  └→ SseEmitterRegistry (analyst_1, analyst_2)
  └→ Server B
     └→ SseEmitterRegistry (analyst_3, analyst_4)

EventBusPublisher (via RabbitMQ)
  ├→ Server A's DashboardUpdater
  └→ Server B's DashboardUpdater
```

Each server's `DashboardUpdater` pushes to connected clients.

## Monitoring

```
Metrics:
- dashboard:sse_emitters:active_count
- dashboard:event:latency (event published → analyst sees)
- dashboard:polling:fallback_count
```

## Related
- ADR-002: Event-driven communication
- Pattern: Observer
