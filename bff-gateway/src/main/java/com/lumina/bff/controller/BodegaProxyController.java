package com.lumina.bff.controller;

import com.lumina.bff.config.CircuitBreakerHelper;
import com.lumina.bff.config.MicroservicesConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/bodega")
@RequiredArgsConstructor
public class BodegaProxyController {

    private final WebClient.Builder webClientBuilder;
    private final MicroservicesConfig config;
    private final CircuitBreakerHelper circuitBreaker;

    @GetMapping("/inventario")
    public Mono<ResponseEntity<String>> listarInventario() {
        return circuitBreaker.protect("bodega", webClientBuilder.build()
                .get()
                .uri(config.getBodegaUrl() + "/api/bodega/inventario")
                .retrieve()
                .toEntity(String.class));
    }

    @GetMapping("/inventario/{productoId}")
    public Mono<ResponseEntity<String>> obtenerStock(@PathVariable Long productoId) {
        return circuitBreaker.protect("bodega", webClientBuilder.build()
                .get()
                .uri(config.getBodegaUrl() + "/api/bodega/inventario/" + productoId)
                .retrieve()
                .toEntity(String.class));
    }

    @PostMapping("/inventario")
    public Mono<ResponseEntity<String>> crearInventario(@RequestBody String body) {
        return circuitBreaker.protect("bodega", webClientBuilder.build()
                .post()
                .uri(config.getBodegaUrl() + "/api/bodega/inventario")
                .bodyValue(body)
                .header("Content-Type", "application/json")
                .retrieve()
                .toEntity(String.class));
    }

    @PatchMapping("/inventario/{productoId}/agregar")
    public Mono<ResponseEntity<String>> agregarStock(@PathVariable Long productoId, @RequestBody String body) {
        return circuitBreaker.protect("bodega", webClientBuilder.build()
                .patch()
                .uri(config.getBodegaUrl() + "/api/bodega/inventario/" + productoId + "/agregar")
                .bodyValue(body)
                .header("Content-Type", "application/json")
                .retrieve()
                .toEntity(String.class));
    }

    @PatchMapping("/inventario/{productoId}/descontar")
    public Mono<ResponseEntity<String>> descontarStock(@PathVariable Long productoId, @RequestBody String body) {
        return circuitBreaker.protect("bodega", webClientBuilder.build()
                .patch()
                .uri(config.getBodegaUrl() + "/api/bodega/inventario/" + productoId + "/descontar")
                .bodyValue(body)
                .header("Content-Type", "application/json")
                .retrieve()
                .toEntity(String.class));
    }

    @GetMapping("/alertas")
    public Mono<ResponseEntity<String>> obtenerAlertas() {
        return circuitBreaker.protect("bodega", webClientBuilder.build()
                .get()
                .uri(config.getBodegaUrl() + "/api/bodega/alertas")
                .retrieve()
                .toEntity(String.class));
    }
}
