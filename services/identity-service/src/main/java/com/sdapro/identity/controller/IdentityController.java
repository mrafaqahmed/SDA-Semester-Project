package com.sdapro.identity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/identity")
public class IdentityController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("identity-service is healthy");
    }
}
