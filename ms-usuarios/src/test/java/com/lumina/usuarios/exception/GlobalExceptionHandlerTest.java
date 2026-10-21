package com.lumina.usuarios.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleRuntimeException_ConRuntimeException_Retorna400BadRequest() {
        RuntimeException exception = new RuntimeException("Error de prueba");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Error de prueba");
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("timestamp")).isNotNull();
    }

    @Test
    void handleRuntimeException_VerificaEstructuraResponse() {
        RuntimeException exception = new RuntimeException("Mensaje de error");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(exception);

        assertThat(response.getBody()).containsKeys("message", "timestamp", "status");
    }
}
