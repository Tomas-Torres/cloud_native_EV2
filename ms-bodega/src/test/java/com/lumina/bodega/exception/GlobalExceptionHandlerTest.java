package com.lumina.bodega.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleRuntimeException_DebeRetornar500ConMensaje() {
        RuntimeException exception = new RuntimeException("Error de prueba");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body).containsKey("timestamp");
        assertThat(body).containsKey("message");
        assertThat(body).containsKey("status");
        assertThat(body.get("message")).isEqualTo("Error de prueba");
        assertThat(body.get("status")).isEqualTo(500);
    }

    @Test
    void handleValidationException_DebeRetornar400ConErroresDeCampo() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("request", "productoId", "El ID de producto es obligatorio");
        FieldError fieldError2 = new FieldError("request", "stock", "El stock es obligatorio");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError1, fieldError2));

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleValidationException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body).containsKey("timestamp");
        assertThat(body).containsKey("status");
        assertThat(body).containsKey("errors");
        assertThat(body.get("status")).isEqualTo(400);

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) body.get("errors");
        assertThat(errors).isNotNull();
        assertThat(errors).hasSize(2);
        assertThat(errors.get("productoId")).isEqualTo("El ID de producto es obligatorio");
        assertThat(errors.get("stock")).isEqualTo("El stock es obligatorio");
    }

    @Test
    void handleValidationException_DebeRetornar400ConListaVacia_CuandoNoHayErrores() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.emptyList());

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleValidationException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body).containsKey("timestamp");
        assertThat(body).containsKey("status");
        assertThat(body).containsKey("errors");
        assertThat(body.get("status")).isEqualTo(400);

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) body.get("errors");
        assertThat(errors).isNotNull();
        assertThat(errors).isEmpty();
    }

    @Test
    void handleRuntimeException_DebeIncluirTimestamp() {
        RuntimeException exception = new RuntimeException("Error de prueba");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(exception);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);
    }

    @Test
    void handleValidationException_DebeIncluirTimestamp() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.emptyList());

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleValidationException(exception);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);
    }
}
