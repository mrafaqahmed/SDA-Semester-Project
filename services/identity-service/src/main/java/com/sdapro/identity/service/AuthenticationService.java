package com.sdapro.identity.service;

import com.sdapro.identity.domain.User;
import com.sdapro.identity.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;

/**
 * Authentication Service - JWT token generation and validation
 */
@Slf4j
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public AuthenticationService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public Optional<String> authenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && verifyPassword(password, user.get().getPasswordHash())) {
            user.get().setLastLogin(Instant.now());
            userRepository.save(user.get());
            log.info("User authenticated: {}", username);
            return Optional.of(tokenService.generateToken(user.get()));
        }
        log.warn("Authentication failed for user: {}", username);
        return Optional.empty();
    }

    public Optional<User> validateToken(String token) {
        return tokenService.validateToken(token);
    }

    private boolean verifyPassword(String rawPassword, String hashedPassword) {
        // In real impl: use BCrypt
        return rawPassword.hashCode() == hashedPassword.hashCode();
    }
}
