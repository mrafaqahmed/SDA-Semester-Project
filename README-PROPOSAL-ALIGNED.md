# SDA-Pro: Security Incident Response & Threat Mitigation Platform

**A Software Design & Architecture semester project demonstrating 12 design patterns, 4 architecture styles, and enterprise SOA principles.**

---

## Overview

SDA-Pro is an intelligent Security Operations Center (SOC) platform that:
- **Ingests** security alerts from multiple sources (Splunk, CrowdStrike, Firewall)
- **Enriches** alerts through a configurable pipeline (geo-IP, threat intel, asset context)
- **Correlates** individual alerts into security incidents
- **Orchestrates** automated response actions (isolate endpoints, block IPs, disable accounts)
- **Tracks** incident lifecycle through 7 states
- **Provides** real-time SOC analyst dashboard with WebSocket/SSE updates
- **Logs** all actions immutably for compliance (GDPR, SOC2)

---

## Alignment with Proposal

This project **fully implements** the SDA-Pro proposal requirements:

### ✅ 12 Design Patterns (All Present)

| # | Pattern | Service(s) | Evidence |
|----|---------|-----------|----------|
| 1 | **Singleton** | Alert Ingestion, Event Bus | `IngestionConfigManager`, `EventBusPublisher` |
| 2 | **Factory Method** | Alert Ingestion, Response Orch. | `AlertNormalizerFactory`, `ResponseActionFactory` |
| 3 | **Abstract Factory** | Enrichment, Notification | `EnrichmentProviderFactory`, `NotificationFactory` |
| 4 | **Composite** | Alert Ingestion | `SingleAlert`, `AlertCampaign`, `IncidentCluster` tree |
| 5 | **Facade** | Response Orchestration | `IncidentResponseFacade` simplifies state + action + logging |
| 6 | **Adapter** | Threat Intel | `SplunkAdapter`, `VirusTotalAdapter`, `MISPAdapter` |
| 7 | **Decorator** | Response Orchestration | `AuditLogDecorator`, `ApprovalGateDecorator`, `RollbackDecorator` |
| 8 | **Proxy** | Threat Intel, Response Orch. | `ThreatIntelProxy` (cache + rate limit), `ResponseActionProxy` (auth) |
| 9 | **State** | Incident Management | `NewState` → `TriageState` → `ContainmentState` → `ClosedState` |
| 10 | **Chain of Responsibility** | Enrichment | Dedup → GeoIP → ThreatIntel → Asset → Classification handlers |
| 11 | **Observer** | Event Bus, Dashboard | `IncidentEventPublisher` notifies `DashboardUpdater`, `AuditLogger` |
| 12 | **Strategy** | Response Orchestration, Enrichment | `AggressiveStrategy`, `BalancedStrategy`, `ConservativeStrategy` |

Each pattern includes **source code comment annotation**: `// PATTERN: <Name>`

### ✅ 4 Architecture Styles (All Implemented)

1. **SOA (Service-Oriented Architecture)**
   - 8 autonomous services with explicit API contracts
   - See `docs/adr/ADR-001-SOA-vs-Microservices.md`

2. **MVC (Model-View-Controller)**
   - SOC Dashboard: `soc-dashboard/src/{controllers,models,views}`
   - Controllers route HTTP, Models manage API clients + DTOs, Views render React/Vue

3. **Layered Architecture**
   - Each service: Presentation → Business Logic → Data Access
   - Clear separation in `services/*/src/main/java/com/sdapro/`

4. **Event-Driven Architecture**
   - RabbitMQ message bus publishes domain events
   - Multiple subscribers (Dashboard, Audit, Notifications) react independently
   - Decouples services; enables asynchronous processing

---

## Project Structure

```
SDA-Pro/
├── docs/                                 # Documentation
│   ├── adr/                             # Architecture Decision Records (5 ADRs)
│   │   ├── ADR-001-SOA-vs-Microservices.md
│   │   ├── ADR-002-Communication-Strategy.md
│   │   ├── ADR-003-Database-Strategy.md
│   │   ├── ADR-004-Threat-Intel-Caching.md
│   │   └── ADR-005-Real-Time-Push.md
│   ├── uml/                             # UML Diagrams (PlantUML)
│   │   ├── class-diagram.puml
│   │   ├── component-diagram.puml
│   │   ├── sequence-ingestion.puml
│   │   └── sequence-response.puml
│   └── api/                             # API Contracts (OpenAPI/Swagger)
│       ├── alert-ingestion-api.yml
│       ├── incident-management-api.yml
│       └── response-orchestration-api.yml
│
├── shared/                              # Cross-service libraries
│   ├── pom.xml
│   ├── src/main/java/com/sdapro/
│   │   ├── events/                      # Domain events (Observer pattern)
│   │   │   ├── AlertIngested.java
│   │   │   ├── IncidentCreated.java
│   │   │   ├── ResponseActionExecuted.java
│   │   │   └── DomainEvent.java
│   │   ├── contracts/                   # Service API DTOs
│   │   │   ├── AlertDTO.java
│   │   │   ├── IncidentDTO.java
│   │   │   └── ResponseActionDTO.java
│   │   └── commons/                     # Utilities
│   │       ├── logging/
│   │       ├── validation/
│   │       └── error/
│
├── services/                            # 8 Microservices
│   ├── alert-ingestion-service/         # Student A: Singleton, Factory, Composite
│   ├── enrichment-correlation-service/  # Student A: CoR, Composite, Strategy, AbstractFactory
│   ├── incident-management-service/     # Student A: State
│   ├── response-orchestration-service/  # Student B: Facade, Strategy, Decorator, Proxy
│   ├── threat-intel-service/            # Student B: Adapter, Proxy
│   ├── notification-service/            # Student B/C: Abstract Factory
│   ├── audit-service/                   # Student C: Observer (consumer)
│   └── identity-service/                # Student C: Authentication
│
├── soc-dashboard/                       # Student C: MVC React/Vue Application
│   ├── src/
│   │   ├── controllers/                 # Route handlers
│   │   ├── models/                      # API clients, DTOs
│   │   ├── views/                       # React/Vue components
│   │   ├── services/                    # Dashboard business logic
│   │   └── store/                       # State management
│   ├── tests/
│   └── Dockerfile
│
├── middleware/                          # Student B: Chain of Responsibility
│   ├── src/
│   │   ├── deduplication/              # Handler 1
│   │   ├── geoip/                       # Handler 2
│   │   ├── threat-intel/                # Handler 3
│   │   ├── asset-context/               # Handler 4
│   │   └── classification/              # Handler 5
│
├── event-bus/                           # Student C: Observer + Singleton
│   ├── src/
│   │   ├── publisher/                   # EventBusPublisher (Singleton)
│   │   ├── subscribers/                 # Observer implementations
│   │   └── schemas/                     # Event definitions
│
├── scripts/                             # Deployment & Testing
│   ├── seed/                            # Demo alert data, threat intel samples
│   ├── migration/                       # Database schema versioning
│   └── integration-test/                # E2E test scenarios
│
├── docker-compose.yml                   # Local dev environment
├── Makefile                             # Build automation
├── pom.xml                              # Maven parent POM
└── README.md                            # This file

```

---

## Technology Stack

| Layer | Technology | Rationale |
|-------|-----------|-----------|
| **Backend Services** | Java 11 + Spring Boot 2.7 | Industry standard; excellent Spring ecosystem |
| **Frontend Dashboard** | React or Vue.js | Modern, responsive, real-time capable |
| **Message Bus** | RabbitMQ / Apache Kafka | Event-driven decoupling (Observer pattern) |
| **Relational DB** | PostgreSQL | Strong transaction support for incidents, ACID guarantees |
| **Document DB** | MongoDB | Flexible schema for heterogeneous alerts |
| **Cache** | Redis | Threat intel caching, session management, rate limiting |
| **Containerization** | Docker + Docker Compose | Microservice simulation, local dev environment |
| **Version Control** | Git + GitFlow | Standard team collaboration workflow |

---

## Core Services

### 1. Alert Ingestion Service (Student A Primary)
**Patterns**: Singleton, Factory Method, Composite

- **Function**: Ingest alerts from Splunk, CrowdStrike, Firewall; normalize to canonical schema
- **Endpoints**:
  - `POST /ingest/splunk` - Webhook receiver
  - `POST /ingest/crowdstrike` - API polling adapter
  - `GET /alerts` - Alert history

**Key Classes**:
- `IngestionConfigManager` (Singleton) - Shared configuration
- `AlertNormalizerFactory` (Factory) - Produces normalizers by source type
- `Alert`, `AlertCampaign`, `IncidentCluster` (Composite) - Tree structure for grouping

---

### 2. Enrichment & Correlation Service (Student A Primary)
**Patterns**: Chain of Responsibility, Composite, Strategy, Abstract Factory

- **Function**: Enrich alerts with geo-IP, threat intel, asset context; correlate into incidents
- **Pipeline**: Dedup → GeoIP → ThreatIntel → AssetContext → Classification
- **Endpoints**:
  - `POST /enrich` - Process alert through pipeline
  - `POST /correlate` - Detect incident patterns

**Key Classes**:
- `EnrichmentHandler` (CoR) - Abstract base for handlers
- `DeduplicationHandler`, `GeoIPHandler`, `ThreatIntelHandler`, etc. (CoR) - Concrete handlers
- `CorrelationStrategy` (Strategy) - Multiple correlation algorithms
- `EnrichmentProviderFactory` (AbstractFactory) - Families of enrichment sources

---

### 3. Incident Management Service (Student A Primary)
**Patterns**: State

- **Function**: Track incidents through lifecycle; enforce state-dependent behavior
- **States**: New → UnderTriage → Containment → Eradication → Recovery → PostIncidentReview → Closed
- **Endpoints**:
  - `GET /incidents` - List incidents
  - `POST /incidents/{id}/triage` - Begin triage (triggers state transition)
  - `POST /incidents/{id}/actions` - Execute response actions

**Key Classes**:
- `Incident` - State machine aggregate root
- `IncidentState` (interface) - State behavior contract
- `NewState`, `TriageState`, `ContainmentState`, etc. (State) - Concrete states

---

### 4. Response Orchestration Service (Student B Primary)
**Patterns**: Facade, Strategy, Decorator, Proxy

- **Function**: Execute response actions (isolate endpoint, block IP, disable account); manage approval gates, audit logs, rollback
- **Endpoints**:
  - `POST /incidents/{id}/respond` - Assess and execute response
  - `GET /incidents/{id}/actions` - Response history

**Key Classes**:
- `IncidentResponseFacade` (Facade) - Coordinates state transition, strategy selection, action execution, logging
- `ResponseStrategy` (Strategy) - AggressiveContainmentStrategy, BalancedStrategy, ConservativeStrategy, WatchAndWaitStrategy
- `ResponseActionDecorator` (Decorator) - Wrap actions with audit, approval, rollback, metrics
- `ResponseActionProxy` (Proxy) - Enforce authorization before action execution

---

### 5. Threat Intel Service (Student B Primary)
**Patterns**: Adapter, Proxy

- **Function**: Integrate external threat intel (VirusTotal, MISP); cache with rate limiting
- **Endpoints**:
  - `POST /reputation/check` - Query reputation score
  - `GET /indicators` - List known malicious indicators

**Key Classes**:
- `ThreatIntelProvider` (interface) - Adapter contract
- `VirusTotalAdapter`, `MISPAdapter`, `CustomFeedAdapter` (Adapter) - Convert external APIs
- `ThreatIntelProxy` (Proxy) - Caching, rate limiting, access control
- Implementations: `CachingProxy`, `RateLimitProxy`, `AccessControlProxy`

---

### 6. Notification Service (Student B/C)
**Patterns**: Abstract Factory

- **Function**: Dispatch notifications to Email, Slack, PagerDuty
- **Endpoints**:
  - `POST /dispatch` - Send notification to appropriate channel

**Key Classes**:
- `NotificationFactory` (AbstractFactory) - Families of notifiers
- `EmailNotifier`, `SlackNotifier`, `PagerDutyNotifier` (Notifier implementations)

---

### 7. Audit Service (Student C)
**Patterns**: Observer (consumer)

- **Function**: Listen to all domain events; log immutably for compliance
- **No REST API** - Only event subscriber

**Key Classes**:
- `AuditEventLogger` (Observer) - Subscribes to IncidentStateChanged, ResponseActionExecuted, etc.

---

### 8. Identity Service (Student C)
**Patterns**: —

- **Function**: Analyst authentication, session management, RBAC
- **Endpoints**:
  - `POST /login` - Authenticate analyst
  - `GET /analysts/{id}` - User info + roles

---

### SOC Dashboard (Student C)
**Pattern**: MVC

- **Function**: Real-time incident queue, alert stream, response history
- **Technologies**: React or Vue.js
- **Updates**: Server-Sent Events (SSE) / WebSocket for < 2 second latency
- **Structure**:
  - `Controllers/` - HTTP request handlers (REST)
  - `Models/` - API clients (fetch from backend services)
  - `Views/` - React components (render UI)
  - `Store/` - State management (Redux / Vuex)

---

## API Contracts

### Synchronous (REST/gRPC)

```
Dashboard → Incident Service:     GET /incidents
Dashboard → Audit Service:        GET /audit/reports/{id}
Response Orch → Threat Intel:     POST /reputation/check
Response Orch → Notification:     POST /dispatch
Identity Service → All:           GET /analysts/{id}
```

### Asynchronous (Event-Driven via RabbitMQ)

```
Event Type              Publisher               Subscribers
─────────────────────────────────────────────────────────
AlertIngested           Alert Ingestion         → Enrichment, Dashboard, Audit
AlertEnriched           Enrichment              → Correlation, Dashboard, Audit
IncidentCreated         Incident Management     → Dashboard, Notification, Response Orch
IncidentStateChanged    Incident Management     → Dashboard, Audit, Metrics
ResponseActionExecuted  Response Orchestration  → Dashboard, Audit, Notification
ThreatIntelUpdated      Threat Intel            → Enrichment (cache invalidation)
```

---

## Development Workflow

### 1. Set Up Local Environment

```bash
# Clone and install dependencies
git clone <repo>
cd SDA-Pro

# Build all services
mvn clean install

# Start local infrastructure (PostgreSQL, MongoDB, Redis, RabbitMQ)
docker-compose up -d
```

### 2. Git Branching (GitFlow)

```
main
  ├─── (production-stable releases: v0.1.0, v0.2.0)
  └─── develop
        ├─── feature/A-composite-state (Student A)
        ├─── feature/B-threat-intel-proxy (Student B)
        ├─── feature/C-dashboard-observer (Student C)
        └─── feature/shared-event-schemas (Collaborative)
```

### 3. Testing Strategy

| Type | Scope | Who |
|------|-------|-----|
| Unit | Individual classes (patterns in isolation) | Each student for their modules |
| Integration | Service + DB + Message Bus | Rotating pair each week |
| Contract | API request/response shapes | Provider service owner |
| End-to-End | Full `docker-compose` stack | All 3 together |

---

## Acceptance Criteria

- [ ] Alerts from 2+ sources ingested and normalized
- [ ] Alerts enriched through Chain of Responsibility (min. 3 handlers)
- [ ] Composite grouping: Alerts grouped into Campaigns
- [ ] Incidents transition through all 6 states
- [ ] At least 2 response strategies selectable
- [ ] At least 2 threat intel adapters (Proxy + Adapter)
- [ ] Dashboard real-time updates (< 2 seconds)
- [ ] All 12 patterns annotated in code
- [ ] All 4 architecture styles documented with diagrams
- [ ] System starts with `docker-compose up` and passes E2E test

---

## Grading Rubric

| Criteria | Weight | Excellent (90–100) | Satisfactory (70–89) | Needs Improvement (<70) |
|----------|--------|-------------------|----------------------|-------------------------|
| Architectural Correctness | 25% | Clean SOA, MVC, layering | Minor violations | Monolithic tendencies |
| Pattern Sophistication | 25% | Natural emergence, strong justification | All present, some forced | Shoehorned patterns |
| Integration Quality | 20% | Seamless end-to-end, robust error handling | Works with minor issues | Frequent failures |
| Code Quality & Extensibility | 15% | Add new source/action without core changes | Mostly extensible | Brittle, core changes needed |
| Presentation & Documentation | 15% | Clear UML, insightful ADRs, confident demo | Adequate diagrams, basic ADRs | Missing diagrams, poor justifications |

---

## Getting Started

### Quick Start

```bash
# Build and start all services
docker-compose up --build

# Access dashboard
# http://localhost:3000

# Send test alert
curl -X POST http://localhost:8080/alert-ingestion/ingest/splunk \
  -H "Content-Type: application/json" \
  -d '{
    "source": "splunk",
    "severity": "HIGH",
    "message": "Lateral movement detected"
  }'
```

### Documentation

- **Architecture Decisions**: `docs/adr/`
- **UML Diagrams**: `docs/uml/` (render at plantuml.com)
- **API Contracts**: `docs/api/`

---

## Team Responsibilities

| Student | Primary Domain | Services Owned | Patterns |
|---------|---|---|---|
| **A** (Threat Pipeline Engineer) | Alert Ingestion, Enrichment, Incident Mgmt | alert-ingestion, enrichment-correlation, incident-management | Composite, State, Factory, Abstract Factory, Singleton, Strategy |
| **B** (Integration & Response Engineer) | Threat Intel, Response Orch, Security Middleware | response-orchestration, threat-intel, notification (shared), middleware | Adapter, Decorator, Proxy, Facade, Chain of Responsibility |
| **C** (SOC Platform Engineer) | Dashboard, Event Bus, Identity | soc-dashboard, audit, identity, event-bus | MVC, Observer, SOA, Event-Driven, Layered |

---

## References

- Spring Boot: https://spring.io/projects/spring-boot
- RabbitMQ: https://www.rabbitmq.com/
- PlantUML: https://plantuml.com/
- GitFlow: https://nvie.com/posts/a-successful-git-branching-model/

---

**Last Updated**: May 2026  
**Version**: 0.1.0  
**Team**: Student A, Student B, Student C
