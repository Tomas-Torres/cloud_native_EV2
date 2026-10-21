package com.lumina.bff.controller;

import com.lumina.bff.config.CircuitBreakerHelper;
import com.lumina.bff.config.MicroservicesConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoProxyController {

    private final WebClient.Builder webClientBuilder;
    private final MicroservicesConfig config;
    private final CircuitBreakerHelper circuitBreaker;

    @GetMapping("/{usuarioId}")
    public Mono<ResponseEntity<String>> obtenerCarrito(@PathVariable Long usuarioId) {
        return circuitBreaker.protect("carrito", webClientBuilder.build()
                .get()
                .uri(config.getCarritoUrl() + "/api/carrito/" + usuarioId)
                .retrieve()
                .toEntity(String.class));
    }

    @PostMapping("/{usuarioId}/agregar")
    public Mono<ResponseEntity<String>> agregarProducto(@PathVariable Long usuarioId, @RequestBody String body) {
        return circuitBreaker.protect("carrito", webClientBuilder.build()
                .post()
                .uri(config.getCarritoUrl() + "/api/carrito/" + usuarioId + "/agregar")
                .bodyValue(body)
                .header("Content-Type", "application/json")
                .retrieve()
                .toEntity(String.class));
    }

    @DeleteMapping("/{usuarioId}/eliminar/{productoId}")
    public Mono<ResponseEntity<String>> eliminarProducto(@PathVariable Long usuarioId, @PathVariable Long productoId) {
        return circuitBreaker.protect("carrito", webClientBuilder.build()
                .delete()
                .uri(config.getCarritoUrl() + "/api/carrito/" + usuarioId + "/eliminar/" + productoId)
                .retrieve()
                .toEntity(String.class));
    }
}
