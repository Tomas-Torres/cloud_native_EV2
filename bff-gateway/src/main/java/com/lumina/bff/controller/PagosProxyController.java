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
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagosProxyController {

    private final WebClient.Builder webClientBuilder;
    private final MicroservicesConfig config;
    private final CircuitBreakerHelper circuitBreaker;

    @PostMapping("/crear-preferencia")
    public Mono<ResponseEntity<String>> crearPreferencia(@RequestBody String body) {
        return circuitBreaker.protect("pagos", webClientBuilder.build()
                .post()
                .uri(config.getPagosUrl() + "/api/pagos/crear-preferencia")
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
    public Mono<ResponseEntity<String>> obtenerPago(@PathVariable Long id) {
        return circuitBreaker.protect("pagos", webClientBuilder.build()
                .get()
                .uri(config.getPagosUrl() + "/api/pagos/" + id)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.just(ResponseEntity.status(ex.getStatusCode())
                                .header("Content-Type", "application/json")
                                .body(ex.getResponseBodyAsString()))));
    }

    @GetMapping("/orden/{ordenId}")
    public Mono<ResponseEntity<String>> obtenerPagosPorOrden(@PathVariable String ordenId) {
        return circuitBreaker.protect("pagos", webClientBuilder.build()
                .get()
                .uri(config.getPagosUrl() + "/api/pagos/orden/" + ordenId)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.just(ResponseEntity.status(ex.getStatusCode())
                                .header("Content-Type", "application/json")
                                .body(ex.getResponseBodyAsString()))));
    }
}
