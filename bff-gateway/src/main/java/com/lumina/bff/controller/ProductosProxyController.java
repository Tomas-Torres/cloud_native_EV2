package com.lumina.bff.controller;

import com.lumina.bff.config.CircuitBreakerHelper;
import com.lumina.bff.config.MicroservicesConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductosProxyController {

    private final WebClient.Builder webClientBuilder;
    private final MicroservicesConfig config;
    private final CircuitBreakerHelper circuitBreaker;

    @GetMapping
    public Mono<ResponseEntity<String>> listarProductos() {
        return circuitBreaker.protect("productos", webClientBuilder.build()
                .get()
                .uri(config.getProductosUrl() + "/api/productos")
                .retrieve()
                .toEntity(String.class));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<String>> obtenerProducto(@PathVariable Long id) {
        return circuitBreaker.protect("productos", webClientBuilder.build()
                .get()
                .uri(config.getProductosUrl() + "/api/productos/" + id)
                .retrieve()
                .toEntity(String.class));
    }

    @GetMapping("/marcas")
    public Mono<ResponseEntity<String>> listarMarcas() {
        return circuitBreaker.protect("productos", webClientBuilder.build()
                .get()
                .uri(config.getProductosUrl() + "/api/productos/marcas")
                .retrieve()
                .toEntity(String.class));
    }

    @PostMapping
    public Mono<ResponseEntity<String>> crearProducto(@RequestBody String body) {
        return circuitBreaker.protect("productos", webClientBuilder.build()
                .post()
                .uri(config.getProductosUrl() + "/api/productos")
                .bodyValue(body)
                .header("Content-Type", "application/json")
                .retrieve()
                .toEntity(String.class));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<String>> actualizarProducto(@PathVariable Long id, @RequestBody String body) {
        return circuitBreaker.protect("productos", webClientBuilder.build()
                .put()
                .uri(config.getProductosUrl() + "/api/productos/" + id)
                .bodyValue(body)
                .header("Content-Type", "application/json")
                .retrieve()
                .toEntity(String.class));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> eliminarProducto(@PathVariable Long id) {
        return circuitBreaker.protect("productos", webClientBuilder.build()
                .delete()
                .uri(config.getProductosUrl() + "/api/productos/" + id)
                .retrieve()
                .toEntity(String.class));
    }
}
