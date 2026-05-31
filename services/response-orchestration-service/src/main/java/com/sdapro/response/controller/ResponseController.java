package com.sdapro.response.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/response")
public class ResponseController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("response-orchestration-service is healthy");
    }
}
