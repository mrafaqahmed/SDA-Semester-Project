# ADR-001: SOA vs. Microservices vs. Modular Monolith

## Status
**ACCEPTED**

## Context
SDA-Pro requires clear service boundaries for alert ingestion, enrichment, incident management, response orchestration, threat intelligence, notifications, audit logging, and identity services. The team must choose between:
- **Service-Oriented Architecture (SOA)**: Loosely coupled services with explicit contracts
- **Microservices**: Independent deployable services with full lifecycle autonomy
- **Modular Monolith**: Single deployment unit with logical module boundaries

## Decision
**Service-Oriented Architecture (SOA)** with Docker-based containerization simulating microservice deployment characteristics.

## Rationale
1. **Service Boundaries**: Clear separation of concerns—each service owns its domain (alerts, incidents, responses, threat intel)
2. **Logical Independence**: Services communicate via well-defined APIs and events, not shared databases
3. **Scalability**: Individual services can be horizontally scaled based on load
4. **Educational Value**: Students learn enterprise architecture patterns without full microservice complexity
5. **Testing**: Integration testing across service boundaries validates patterns (Observer, Facade, Proxy)

## Service Boundaries

| Service | Domain | Primary Pattern | Responsibility |
|---------|--------|-----------------|-----------------|
| Alert Ingestion | Alert reception | Factory Method, Composite, Singleton | Normalize alerts from Splunk, CrowdStrike, Firewall |
| Enrichment & Correlation | Alert processing | Chain of Responsibility, Strategy, Abstract Factory | Geo-IP, threat intel, asset context enrichment |
| Incident Management | Incident lifecycle | State | Track incidents through New → Triage → ... → Closed |
| Response Orchestration | Incident response | Facade, Decorator, Strategy | Execute and track response actions |
| Threat Intelligence | External feeds | Adapter, Proxy | Integrate VirusTotal, MISP, custom feeds with caching |
| Notification | Alerting | Abstract Factory | Email, Slack, PagerDuty dispatching |
| Audit | Compliance logging | Observer | Immutable event log for SOC2/GDPR |
| Identity | Authentication | — | Analyst session management and RBAC |

## Consequences

### Positive
- ✅ Each team member owns a service, reducing integration conflicts
- ✅ Patterns are clearly scoped to their service (e.g., State in Incident Management)
- ✅ Event-driven decoupling (Observer/Pub-Sub) reduces tight coupling
- ✅ Docker deployment structure allows future migration to true microservices

### Negative
- ❌ No distributed transaction support (eventual consistency only)
- ❌ Network calls between services introduce latency
- ❌ Debugging cross-service flows requires correlation IDs

## Trade-offs
**Simplicity vs. Autonomy**: SOA accepts some coordination overhead (shared schemas) to remain simpler than full microservices while still teaching service decomposition.

## Related
- ADR-002: Synchronous vs. Asynchronous communication
- ADR-003: Database strategy
