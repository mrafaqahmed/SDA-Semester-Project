package com.sdapro.identity.service;

import com.sdapro.identity.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Token Service - JWT token generation and validation
 */
@Slf4j
@Service
public class TokenService {
    private final Map<String, TokenData> tokenCache = new HashMap<>();
    private static final long TOKEN_VALIDITY_HOURS = 24;

    public String generateToken(User user) {
        String token = UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
        TokenData tokenData = new TokenData(user.getId().toString(), user.getUsername(), 
            user.getRoles(), Instant.now().plus(TOKEN_VALIDITY_HOURS, ChronoUnit.HOURS));
        tokenCache.put(token, tokenData);
        log.info("Token generated for user: {}", user.getUsername());
        return token;
    }

    public Optional<User> validateToken(String token) {
        TokenData tokenData = tokenCache.get(token);
        if (tokenData != null && tokenData.expiresAt.isAfter(Instant.now())) {
            return Optional.of(new User());  // Mock user return
        }
        return Optional.empty();
    }

    public boolean hasPermission(String token, String permission) {
        TokenData tokenData = tokenCache.get(token);
        if (tokenData == null || tokenData.expiresAt.isBefore(Instant.now())) {
            return false;
        }
        // Map roles to permissions
        return tokenData.roles.contains("ADMIN") || 
               (permission.equals("READ") && tokenData.roles.contains("ANALYST"));
    }

    static class TokenData {
        final String userId;
        final String username;
        final Set<String> roles;
        final Instant expiresAt;

        TokenData(String userId, String username, Set<String> roles, Instant expiresAt) {
            this.userId = userId;
            this.username = username;
            this.roles = roles;
            this.expiresAt = expiresAt;
        }
    }
}
