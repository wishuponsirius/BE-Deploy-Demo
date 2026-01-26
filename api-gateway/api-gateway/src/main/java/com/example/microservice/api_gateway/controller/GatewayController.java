package com.example.microservice.api_gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gateway")
@Tag(name = "Gateway", description = "API Gateway Health & Status endpoints")
public class GatewayController {

    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "Check if the API Gateway is running")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "api-gateway");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    @Operation(summary = "Gateway Info", description = "Get API Gateway information")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "API Gateway");
        response.put("version", "1.0.0");
        response.put("description", "Microservice API Gateway with JWT Authentication");
        return ResponseEntity.ok(response);
    }
}
