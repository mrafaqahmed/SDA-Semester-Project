package com.sdapro.identity.service;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * PATTERN: Singleton
 * RATIONALE: Single shared instance managing user authentication and authorization.
 * Thread-safe configuration for credential validation across services.
 */
@Service
public class IdentityService {

    // Simulated user database
    private static final Map<String, UserCredential> users = new ConcurrentHashMap<>();
    
    static {
        users.put("admin", new UserCredential("admin", "admin123", "ROLE_ADMIN"));
        users.put("analyst", new UserCredential("analyst", "analyst123", "ROLE_ANALYST"));
        users.put("responder", new UserCredential("responder", "responder123", "ROLE_RESPONDER"));
    }

    public boolean authenticateUser(String username, String password) {
        UserCredential user = users.get(username);
        if (user == null) {
            return false;
        }
        return user.password.equals(password);
    }

    public String getUserRole(String username) {
        UserCredential user = users.get(username);
        return user != null ? user.role : null;
    }

    public boolean hasPermission(String username, String permission) {
        String role = getUserRole(username);
        if (role == null) {
            return false;
        }

        return switch (role) {
            case "ROLE_ADMIN" -> true; // Admin has all permissions
            case "ROLE_ANALYST" -> permission.contains("read") || permission.contains("query");
            case "ROLE_RESPONDER" -> permission.contains("execute") || permission.contains("approve");
            default -> false;
        };
    }

    public static class UserCredential {
        public final String username;
        public final String password;
        public final String role;

        public UserCredential(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }
    }
}
