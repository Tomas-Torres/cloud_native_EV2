package com.lumina.bff.controller;

import com.lumina.bff.config.CircuitBreakerHelper;
import com.lumina.bff.config.MicroservicesConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuariosProxyController {

    private final WebClient.Builder webClientBuilder;
    private final MicroservicesConfig config;
    private final CircuitBreakerHelper circuitBreaker;

    @PostMapping("/registro")
    public Mono<ResponseEntity<String>> registrar(@RequestBody String body) {
        return circuitBreaker.protect("usuarios", webClientBuilder.build()
                .post()
                .uri(config.getUsuariosUrl() + "/api/usuarios/registro")
                .bodyValue(body)
                .header("Content-Type", "application/json")
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.just(ResponseEntity.status(ex.getStatusCode())
                                .header("Content-Type", "application/json")
                                .body(ex.getResponseBodyAsString()))));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody String body) {
        return circuitBreaker.protect("usuarios", webClientBuilder.build()
                .post()
                .uri(config.getUsuariosUrl() + "/api/usuarios/login")
                .bodyValue(body)
                .header("Content-Type", "application/json")
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.just(ResponseEntity.status(ex.getStatusCode())
                                .header("Content-Type", "application/json")
                                .body(ex.getResponseBodyAsString()))));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<String>> obtenerUsuario(@PathVariable Long id) {
        return circuitBreaker.protect("usuarios", webClientBuilder.build()
                .get()
                .uri(config.getUsuariosUrl() + "/api/usuarios/" + id)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.just(ResponseEntity.status(ex.getStatusCode())
                                .header("Content-Type", "application/json")
                                .body(ex.getResponseBodyAsString()))));
    }

    @GetMapping
    public Mono<ResponseEntity<String>> listarUsuarios() {
        return circuitBreaker.protect("usuarios", webClientBuilder.build()
                .get()
                .uri(config.getUsuariosUrl() + "/api/usuarios")
                .retrieve()
                .toEntity(String.class));
        }       
}
