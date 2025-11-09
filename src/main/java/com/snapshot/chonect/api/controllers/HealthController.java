package com.snapshot.chonect.api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para health checks y monitoreo del sistema
 * Proporciona endpoints para verificar el estado de la aplicación
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);
    private static final String TIMESTAMP_KEY = "timestamp";

    /**
     * Health check básico - verifica que la aplicación esté corriendo
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put(TIMESTAMP_KEY, LocalDateTime.now());
        response.put("service", "Chonect Backend");

        logger.debug("Health check requested - status: UP");
        return ResponseEntity.ok(response);
    }

    /**
     * Health check detallado con información adicional
     */
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put(TIMESTAMP_KEY, LocalDateTime.now());
        response.put("service", "Chonect Backend");
        response.put("version", "1.0.0");
        response.put("environment", System.getProperty("spring.profiles.active", "default"));

        // Información del sistema
        Map<String, Object> system = new HashMap<>();
        system.put("java.version", System.getProperty("java.version"));
        system.put("os.name", System.getProperty("os.name"));
        system.put("os.arch", System.getProperty("os.arch"));
        system.put("available.processors", Runtime.getRuntime().availableProcessors());

        // Memoria
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> memory = new HashMap<>();
        memory.put("total", runtime.totalMemory());
        memory.put("free", runtime.freeMemory());
        memory.put("used", runtime.totalMemory() - runtime.freeMemory());
        memory.put("max", runtime.maxMemory());

        response.put("system", system);
        response.put("memory", memory);

        logger.debug("Detailed health check requested");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para verificar conectividad de red básica
     */
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "pong");
        response.put(TIMESTAMP_KEY, LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }
}
