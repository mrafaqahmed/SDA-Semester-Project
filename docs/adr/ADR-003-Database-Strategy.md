# ADR-003: Database Strategy

## Status
**ACCEPTED**

## Context
SDA-Pro must store alerts, incidents, responses, audit trails, and threat intelligence. Options:
- **Single relational DB**: Simplicity, ACID guarantees, but normalized schema complexity
- **Polyglot persistence**: PostgreSQL + NoSQL + cache optimized per domain
- **Document store only**: Flexible schema, but weak transaction support

## Decision
**Polyglot persistence**:
- **PostgreSQL**: Incidents (ACID, state transitions), audit logs (immutable)
- **MongoDB/Document store**: Alert raw payloads (flexible schema)
- **Redis**: Threat intel cache, session store, rate limiting counters

## Rationale

### PostgreSQL (Relational)
**Data**: Incidents, responses, audit trails, user sessions
**Why**: 
- Incident state machine requires transactional consistency
- Audit logs demand immutability and compliance
- Complex queries (e.g., "incidents created by analyst X in last 24h")
- SQL is efficient for analytics

**Schema**:
```sql
incidents (id, state, severity, created_at, analyst_id)
response_actions (id, incident_id, type, status, executed_at)
audit_log (id, event_type, entity_id, actor, timestamp) -- immutable
```

### MongoDB/Document Store (or PostgreSQL JSONB)
**Data**: Alert raw payloads, enrichment context
**Why**:
- Alerts from Splunk, CrowdStrike, Firewall have different schemas
- Normalization to canonical schema + raw payload storage
- Query by alert properties without rigid schema
- Composite document (AlertCampaign + children) maps naturally to document model

**Example**:
```json
{
  "alertId": "...",
  "rawPayload": { /* original Splunk/CrowdStrike JSON */ },
  "canonicalAlert": { /* normalized */ },
  "enrichmentContext": { "geoip": {...}, "threatIntel": {...} }
}
```

### Redis (Cache + Session)
**Data**: Threat intel reputation cache, session tokens, rate limit counters
**Why**:
- Threat intel lookups (VirusTotal) are slow; cache saves 90% of queries
- Analyst session tokens expire quickly; Redis TTL handles cleanup
- Rate limiting (e.g., max 4 queries/min to VirusTotal) via counters
- Sub-second latency for enrichment handlers

**Usage**:
```
Cache: threat:ip:reputation:8.8.8.8 → "MALICIOUS" (TTL 24h)
Sessions: session:analyst_123 → {analyst_id, roles} (TTL 8h)
RateLimit: rate:virustotal:queries → 3 (TTL 60s)
```

## Service-Database Mapping

| Service | Relational (PostgreSQL) | Document (MongoDB) | Cache (Redis) |
|---------|------------------------|-------------------|---------------|
| Alert Ingestion | — | Raw + Canonical alerts | — |
| Enrichment | — | Enriched alerts | Threat intel cache |
| Incident Mgmt | Incidents, responses | — | Sessions |
| Response Orch. | Response history | — | Rate limit counters |
| Threat Intel | — | — | Reputation cache (primary) |
| Audit | Immutable audit log | — | — |
| Identity | User/analyst records | — | Session tokens |

## Consequences

### Positive
- ✅ Alerts remain flexible (Composite documents)
- ✅ Incidents have strong consistency (ACID state transitions)
- ✅ Audit trail is tamper-evident
- ✅ Threat intel cache improves response time 10x

### Negative
- ❌ Multiple databases increase operational complexity
- ❌ Cross-database transactions not possible (eventual consistency)
- ❌ Developers must choose right storage per domain

## Deployment

All databases deployed via `docker-compose`:
```yaml
services:
  postgres:
    image: postgres:15
  mongo:
    image: mongo:6
  redis:
    image: redis:7
```

## Related
- ADR-004: Threat intel caching strategy
