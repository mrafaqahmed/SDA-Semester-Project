package com.sdapro.threatintel.proxy;

import com.sdapro.threatintel.adapter.ThreatIntelProvider;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PATTERN: Proxy
 * RATIONALE: Controls access to ThreatIntelProvider with caching, rate limiting,
 * and access control. Prevents excessive API calls and improves performance.
 */
@Slf4j
public class ThreatIntelProxy implements ThreatIntelProvider {
    private final ThreatIntelProvider realProvider;
    private final Map<String, CachedResult> cache = new ConcurrentHashMap<>();
    private final RateLimiter rateLimiter = new RateLimiter(100, 60);  // 100 requests per 60 seconds
    private static final long CACHE_TTL_MS = 3600000;  // 1 hour

    public ThreatIntelProxy(ThreatIntelProvider realProvider) {
        this.realProvider = realProvider;
    }

    @Override
    public String getProviderName() {
        return realProvider.getProviderName();
    }

    @Override
    public Map<String, Object> checkReputation(String indicator) {
        // Check rate limit
        if (!rateLimiter.allowRequest()) {
            log.warn("Rate limit exceeded for indicator: {}", indicator);
            return Map.of("error", "Rate limit exceeded");
        }

        // Check cache
        CachedResult cached = cache.get(indicator);
        if (cached != null && !cached.isExpired()) {
            log.debug("Cache hit for indicator: {}", indicator);
            return cached.result;
        }

        // Call real provider
        log.debug("Cache miss, calling provider for indicator: {}", indicator);
        Map<String, Object> result = realProvider.checkReputation(indicator);

        // Cache result
        cache.put(indicator, new CachedResult(result, System.currentTimeMillis()));

        return result;
    }

    @Override
    public double getConfidenceScore(String indicator) {
        return realProvider.getConfidenceScore(indicator);
    }

    @Override
    public boolean isKnownThreat(String indicator) {
        return realProvider.isKnownThreat(indicator);
    }

    static class CachedResult {
        final Map<String, Object> result;
        final long timestamp;

        CachedResult(Map<String, Object> result, long timestamp) {
            this.result = result;
            this.timestamp = timestamp;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL_MS;
        }
    }

    static class RateLimiter {
        private final int maxRequests;
        private final long windowMs;
        private final Map<Long, Integer> requestCounts = new ConcurrentHashMap<>();

        RateLimiter(int maxRequests, long windowSeconds) {
            this.maxRequests = maxRequests;
            this.windowMs = windowSeconds * 1000;
        }

        boolean allowRequest() {
            long now = System.currentTimeMillis();
            long window = now / windowMs;
            int count = requestCounts.getOrDefault(window, 0);
            
            if (count < maxRequests) {
                requestCounts.put(window, count + 1);
                return true;
            }
            return false;
        }
    }
}
