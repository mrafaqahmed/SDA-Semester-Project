# ADR-004: Threat Intelligence Caching Strategy

## Status
**ACCEPTED**

## Context
SDA-Pro integrates external threat intel providers (VirusTotal, MISP) for reputation scoring during enrichment. Direct queries are slow (avg 500ms) and rate-limited (4 queries/min for VirusTotal's free tier). Options:
- **No cache**: Simple, always fresh, but unacceptable latency and rate limit hits
- **In-memory cache**: Fast, but limited by process memory; no cross-service sharing
- **Redis cache**: Fast, distributed, TTL-managed, resilient
- **Hybrid**: In-memory + Redis fallback

## Decision
**Redis-backed Proxy with in-memory L1 cache**

```
ThreatIntelHandler
  ↓
ThreatIntelProxy (caching + rate limiting)
  ├→ L1 Cache (in-memory, 1000 entries max)
  │  └→ Cache hit → return immediately (< 5ms)
  │
  ├→ L2 Cache (Redis, unlimited, TTL 24h)
  │  └→ Cache hit → return from Redis (< 50ms)
  │
  └→ Rate Limiter checks quota
     └→ Under quota → query VirusTotal (~ 500ms)
     └→ Over quota → return cached result or "SERVICE_UNAVAILABLE"
```

## Rationale

### L1 Cache (In-Memory)
- **Scope**: Per process; fastest (< 5ms)
- **Size**: 1000 entries (typical ~100 bytes each = ~100 KB)
- **TTL**: 1 hour (refresh stale entries)
- **Invalidation**: Explicit via `ThreatIntelUpdated` event from Threat Intel Service

### L2 Cache (Redis)
- **Scope**: Distributed; shared across all service instances
- **Size**: Unlimited (Redis manages memory)
- **TTL**: 24 hours (VirusTotal caches for similar period)
- **Invalidation**: Automatic TTL + event-driven refresh

### Rate Limiting
- **Quota**: 4 queries/min per API key (VirusTotal free tier)
- **Counter**: Redis `INCR rate:virustotal:queries` with TTL 60s
- **Fallback**: If quota exceeded, return L2 cache (slightly stale is better than blocked)

## Proxy Pattern Implementation

```java
// PATTERN: Proxy + Adapter
class ThreatIntelProxy implements ThreatIntelProvider {
  private final VirusTotalAdapter realProvider;
  private final L1Cache cache;
  private final RedisCache l2Cache;
  
  public ReputationResult checkReputation(String ip) {
    // L1: Check in-memory cache
    if (cache.contains(ip)) return cache.get(ip);
    
    // L2: Check Redis
    if (l2Cache.contains(ip)) {
      ReputationResult cached = l2Cache.get(ip);
      cache.put(ip, cached);  // Refresh L1
      return cached;
    }
    
    // Rate limiter
    if (!rateLimiter.tryAcquire(1)) {
      // Over quota, return fallback
      return l2Cache.getOrDefault(ip, ReputationResult.UNKNOWN);
    }
    
    // Call real provider
    ReputationResult result = realProvider.checkReputation(ip);
    
    // Cache in both levels
    cache.put(ip, result);
    l2Cache.put(ip, result, Duration.ofHours(24));
    
    return result;
  }
}
```

## Cache Invalidation

### Event-Driven Invalidation
When Threat Intel Service receives new indicators from MISP:
1. **Publish**: `ThreatIntelUpdated` event with affected IPs
2. **Subscribers**: All ThreatIntelProxy instances clear L1 cache for those IPs
3. **L2**: Redis TTL eventually expires

## Consequences

### Positive
- ✅ Enrichment pipeline latency reduced from 500ms → < 5ms (L1 hit)
- ✅ Distributed caching: all service instances share L2
- ✅ Rate limit quotas honored with intelligent fallback
- ✅ Natural implementation of Proxy pattern

### Negative
- ❌ Stale data possible (up to 24h old)
- ❌ L1/L2 divergence if Redis unavailable
- ❌ Cache invalidation complexity

## Monitoring

```
Metrics:
- cache:l1:hit_rate
- cache:l2:hit_rate
- threat_intel:api:latency
- threat_intel:rate_limit:exceeded_count
```

## Related
- ADR-003: Redis as cache layer
- Pattern: Proxy, Adapter
