package com.lumina.bff.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class CircuitBreakerHelperTest {

    private CircuitBreakerRegistry registry;
    private CircuitBreakerHelper helper;

    @BeforeEach
    void setUp() {
        registry = CircuitBreakerRegistry.ofDefaults();
        helper = new CircuitBreakerHelper(registry);
    }

    @Test
    void protect_DebeDevolverRespuesta_CuandoLlamadaExitosa() {
        Mono<ResponseEntity<String>> ok = Mono.just(ResponseEntity.ok("contenido"));

        ResponseEntity<String> resp = helper.protect("productos", ok).block();

        assertThat(resp).isNotNull();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isEqualTo("contenido");
    }

    @Test
    void protect_DebeDevolver503_CuandoServicioNoResponde() {
        Mono<ResponseEntity<String>> caido =
                Mono.error(new IOException("Connection refused"));

        ResponseEntity<String> resp = helper.protect("carrito", caido).block();

        assertThat(resp).isNotNull();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    void protect_DebeDevolver503_CuandoCircuitoEstaAbierto() {
        CircuitBreaker cb = registry.circuitBreaker("pagos");
        cb.transitionToOpenState();

        Mono<ResponseEntity<String>> call = Mono.just(ResponseEntity.ok("no deberia ejecutarse"));

        ResponseEntity<String> resp = helper.protect("pagos", call).block();

        assertThat(resp).isNotNull();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
