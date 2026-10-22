package com.lumina.bff.config;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Aplica un Circuit Breaker (Resilience4j) a las llamadas reactivas que el BFF
 * realiza hacia los microservicios.
 *
 * - Si el microservicio responde con un error HTTP (4xx/5xx), se considera que
 *   el servicio esta vivo: la excepcion se propaga sin abrir el circuito.
 * - Si el microservicio no responde (conexion rechazada, timeout) o el circuito
 *   ya esta ABIERTO, se devuelve una respuesta de fallback 503.
 */
@Component
public class CircuitBreakerHelper {

    private final CircuitBreakerRegistry registry;

    public CircuitBreakerHelper(CircuitBreakerRegistry registry) {
        this.registry = registry;
    }

    public Mono<ResponseEntity<String>> protect(String name, Mono<ResponseEntity<String>> call) {
        CircuitBreaker circuitBreaker = registry.circuitBreaker(name);
        return call
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(CallNotPermittedException.class, ex -> fallback(name))
                .onErrorResume(ex -> !(ex instanceof WebClientResponseException), ex -> fallback(name));
    }

    private Mono<ResponseEntity<String>> fallback(String name) {
        String body = "{\"error\":\"El servicio '" + name
                + "' no esta disponible en este momento. Intente nuevamente mas tarde.\"}";
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body));
    }
}
