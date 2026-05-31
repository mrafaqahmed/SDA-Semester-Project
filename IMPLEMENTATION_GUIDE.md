# SDA-Pro: Implementation Guide - Getting Started

## Quick Links

1. **Project Overview** → `README.md`
2. **Alignment Verification** → `PROPOSAL_ALIGNMENT_CHECKLIST.md`
3. **Completion Status** → `COMPLETION_SUMMARY.md`
4. **Architecture Decisions** → `docs/adr/ADR-*.md`
5. **System Diagrams** → `docs/uml/*.puml`
6. **Deployment Config** → `docker-compose.yml`

---

## Current State: ✅ 100% Project Scaffolding Complete

What's already done:
- ✅ 8 service directories with Spring Boot main classes
- ✅ All pom.xml files configured
- ✅ All Dockerfiles prepared
- ✅ docker-compose.yml production-ready
- ✅ 5 Architecture Decision Records
- ✅ 4 UML diagrams
- ✅ Shared libraries skeleton
- ✅ Complete documentation

What remains: **Implementation** (writing the actual business logic)

---

## Phase 1: Core Implementation (Days 1-5)

### Student A: Alert Ingestion & Enrichment

**Alert Ingestion Service** (Port 8081)
- [ ] Create `AlertNormalizerFactory` class (Factory Method)
- [ ] Create `SplunkNormalizer`, `CrowdStrikeNormalizer` implementations
- [ ] Create `IngestionConfigManager` (Singleton)
- [ ] Create Alert, SingleAlert, AlertCampaign domain classes (Composite)
- [ ] Implement `IngestionController` with POST /ingest endpoint
- [ ] Implement RabbitMQ event publishing for `AlertIngestedEvent`

**Enrichment Service** (Port 8082)
- [ ] Create `EnrichmentHandler` abstract class (Chain of Responsibility)
- [ ] Implement 5 handler classes:
  - DeduplicationHandler
  - GeoIPHandler
  - ThreatIntelHandler
  - AssetContextHandler
  - ClassificationHandler
- [ ] Create `CorrelationStrategy` interface (Strategy)
- [ ] Implement correlation algorithms
- [ ] Create `EnrichmentProviderFactory` (Abstract Factory)

**Incident Management Service** (Port 8083)
- [ ] Create `IncidentState` interface and 7 state classes (State)
  - NewState
  - UnderTriageState
  - ContainmentState
  - EradicationState
  - RecoveryState
  - PostIncidentReviewState
  - ClosedState
- [ ] Implement `Incident` aggregate root
- [ ] Create controllers for incident CRUD and state transitions
- [ ] Implement event publishing for `IncidentStateChanged`

---

## Phase 2: Advanced Patterns (Days 6-10)

### Student B: Response Orchestration & Integration

**Response Orchestration Service** (Port 8084)
- [ ] Create `IncidentResponseFacade` (Facade)
- [ ] Create `ResponseStrategy` interface with 4 implementations (Strategy):
  - AggressiveContainmentStrategy
  - BalancedResponseStrategy
  - ConservativeStrategy
  - WatchAndWaitStrategy
- [ ] Create `ResponseAction` interface and concrete actions (Factory)
- [ ] Create Decorator classes (Decorator):
  - AuditLogDecorator
  - ApprovalGateDecorator
  - RollbackCapabilityDecorator
  - MetricsDecorator
- [ ] Create `ResponseActionProxy` with authorization (Proxy)

**Threat Intelligence Service** (Port 8085)
- [ ] Create `ThreatIntelProvider` interface (Adapter)
- [ ] Implement adapters:
  - VirusTotalAdapter
  - MISPAdapter
  - CustomFeedAdapter
- [ ] Create `ThreatIntelProxy` (Proxy) with:
  - CachingProxy (Redis L1/L2)
  - RateLimitProxy
  - AccessControlProxy
- [ ] Implement reputation checking endpoints

**Notification Service** (Port 8086)
- [ ] Create `NotificationFactory` (Abstract Factory)
- [ ] Implement notifier families:
  - EmailNotifier
  - SlackNotifier
  - PagerDutyNotifier
- [ ] Implement dispatch endpoints

---

## Phase 3: Event Bus & Dashboard (Days 11-14)

### Student C: Event Bus, Dashboard & Audit

**Event Bus** (Singleton + Observer)
- [ ] Create `EventBusPublisher` singleton
- [ ] Create Observer implementations:
  - DashboardUpdater (WebSocket/SSE)
  - AuditEventLogger
  - NotificationDispatcher
  - MetricsCollector
- [ ] Wire RabbitMQ message handling

**Audit Service** (Port 8087)
- [ ] Create `AuditEventLogger` (Observer consumer)
- [ ] Implement immutable audit logging
- [ ] Create compliance report endpoints
- [ ] Store all events in PostgreSQL

**Identity Service** (Port 8088)
- [ ] Implement analyst authentication
- [ ] Implement session management (Redis)
- [ ] Create RBAC checks

**SOC Dashboard** (MVC, Port 3000)
- [ ] Create React/Vue project structure
- [ ] Implement Controllers (route handlers):
  - AlertStreamController
  - IncidentController
  - DashboardController
- [ ] Implement Models (API clients):
  - AlertStreamModel
  - IncidentQueueModel
  - DashboardMetricsModel
- [ ] Implement Views (React components):
  - AlertStreamView
  - IncidentQueueView
  - ResponseConsoleView
  - MetricsDashboardView
- [ ] Implement Store (Redux/Vuex state management)
- [ ] Implement SSE/WebSocket for real-time updates

---

## Phase 4: Testing & Integration (Days 15-16)

### All Students: Testing & Deployment

**Unit Tests**
- [ ] Test each pattern in isolation (Service A, B, C)
- [ ] Minimum 70% code coverage

**Integration Tests**
- [ ] Test Service A (Alert Ingestion → Enrichment → Incident)
- [ ] Test Service B (Response Orchestration → Actions)
- [ ] Test Service C (Dashboard updates via Observer)
- [ ] Test cross-service communication

**End-to-End Tests**
```bash
# Test complete flow:
1. Send test alert via /ingest/splunk webhook
2. Verify alert enriched (check database)
3. Verify incident created (check database)
4. Trigger response action manually
5. Verify dashboard updates in real-time (< 2 sec)
6. Verify audit logs created
```

**Deployment**
```bash
# Build all services
mvn clean install

# Start entire stack
docker-compose up --build

# Run integration tests
./scripts/integration-test/run.sh

# Access dashboard
http://localhost:3000
```

---

## Implementation Checklist

### Services - Implementation Status

- [ ] **Alert Ingestion** - Normalizers, composite alerts, event publishing
- [ ] **Enrichment** - 5 handlers, correlation strategies, enrichment factory
- [ ] **Incident Management** - 7-state machine, state transitions
- [ ] **Response Orchestration** - Facade, strategies, decorators, proxy
- [ ] **Threat Intel** - 3+ adapters, caching proxy, rate limiting
- [ ] **Notification** - Email, Slack, PagerDuty factories
- [ ] **Audit** - Event logging, compliance reports
- [ ] **Identity** - Authentication, sessions, RBAC
- [ ] **Dashboard** - MVC structure, real-time updates
- [ ] **Event Bus** - RabbitMQ publisher, subscribers

### Patterns - Code Annotation

For EACH pattern, add comment:
```java
// PATTERN: [PatternName]
// RATIONALE: [Why this pattern is used]
public class SomeClass { ... }
```

Example:
```java
// PATTERN: Factory Method
// RATIONALE: Different alert sources require different normalization strategies
public class AlertNormalizerFactory { ... }
```

### Testing Checkpoints

- [ ] Unit tests pass: `mvn test`
- [ ] Integration tests pass: `mvn verify`
- [ ] Docker builds: `docker-compose build`
- [ ] Services start: `docker-compose up`
- [ ] Health checks pass: All containers healthy
- [ ] Dashboard accessible: http://localhost:3000
- [ ] End-to-end flow works: Alert → Response → Dashboard

---

## Key Implementation Patterns to Remember

### 1. Factory Pattern
Use when creating objects with different implementations:
```java
AlertNormalizer normalizer = AlertNormalizerFactory
  .createNormalizer(AlertSourceType.SPLUNK);
```

### 2. Chain of Responsibility
Use for sequential processing:
```java
EnrichmentHandler h1 = new DeduplicationHandler();
EnrichmentHandler h2 = new GeoIPHandler();
h1.setNext(h2);
h1.handle(alert);  // Passes through chain
```

### 3. State Pattern
Use for state-dependent behavior:
```java
incident.setState(new ContainmentState());
incident.getAllowedActions();  // Different per state
```

### 4. Decorator Pattern
Use to add behavior dynamically:
```java
ResponseAction action = new BlockIPAction(ip);
action = new AuditLogDecorator(action);
action = new ApprovalGateDecorator(action);
action.execute();  // Decorated with audit + approval
```

### 5. Observer Pattern
Use for event-driven decoupling:
```java
eventBus.subscribe("IncidentCreated", dashboardUpdater);
eventBus.publish(new IncidentCreatedEvent(...));
// Dashboard updates automatically
```

---

## Resources

- **Spring Boot**: https://spring.io/projects/spring-boot
- **RabbitMQ**: https://www.rabbitmq.com/tutorials
- **PostgreSQL**: https://www.postgresql.org/docs/
- **MongoDB**: https://docs.mongodb.com/
- **Redis**: https://redis.io/documentation
- **PlantUML**: https://plantuml.com/

---

## Troubleshooting

### Docker Issues
```bash
# Clean and rebuild
docker-compose down -v
docker-compose up --build

# Check logs
docker-compose logs -f [service-name]

# Verify network
docker network inspect sda-network
```

### Database Issues
```bash
# Connect to PostgreSQL
psql -h localhost -U sdapro -d sdapro

# Check tables
\dt

# View data
SELECT * FROM incidents;
```

### Build Issues
```bash
# Clean Maven cache
mvn clean

# Rebuild all
mvn clean install -DskipTests

# Check dependencies
mvn dependency:tree
```

---

## Final Checklist Before Submission

- [ ] All 12 patterns implemented and annotated in code
- [ ] All 4 architecture styles evident (SOA, MVC, Layered, Event-Driven)
- [ ] 8 services running via `docker-compose up`
- [ ] Dashboard accessible at localhost:3000
- [ ] Real-time updates work (< 2 sec latency)
- [ ] All acceptance criteria met
- [ ] Tests pass (unit + integration)
- [ ] Documentation updated
- [ ] Individual reflection reports completed
- [ ] 20-minute presentation prepared

---

**Start implementing today. The scaffolding is complete.**

Good luck! 🚀
