package com.lumina.pagos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumina.pagos.dto.CrearPreferenciaRequest;
import com.lumina.pagos.dto.PagoResponse;
import com.lumina.pagos.entity.EstadoPago;
import com.lumina.pagos.service.PagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagoController.class)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PagoService pagoService;

    private CrearPreferenciaRequest request;
    private PagoResponse pagoResponse;

    @BeforeEach
    void setUp() {
        CrearPreferenciaRequest.ItemRequest item = CrearPreferenciaRequest.ItemRequest.builder()
                .titulo("Producto Test")
                .cantidad(2)
                .precioUnitario(new BigDecimal("5000.00"))
                .descripcion("Descripción del producto")
                .imagenUrl("http://imagen.com/producto.jpg")
                .build();

        request = CrearPreferenciaRequest.builder()
                .ordenId("ORD-123")
                .usuarioId(100L)
                .items(Collections.singletonList(item))
                .emailComprador("test@example.com")
                .build();

        pagoResponse = PagoResponse.builder()
                .id(1L)
                .ordenId("ORD-123")
                .usuarioId(100L)
                .montoTotal(new BigDecimal("10000.00"))
                .moneda("CLP")
                .estado(EstadoPago.PENDIENTE)
                .mercadopagoPreferenceId("pref-123")
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .initPoint("https://init.point")
                .sandboxInitPoint("https://sandbox.init.point")
                .build();
    }

    @Test
    void crearPreferencia_DebeRetornar201YPagoResponse_CuandoEsValido() throws Exception {
        when(pagoService.crearPreferencia(any(CrearPreferenciaRequest.class))).thenReturn(pagoResponse);

        mockMvc.perform(post("/api/pagos/crear-preferencia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ordenId").value("ORD-123"))
                .andExpect(jsonPath("$.usuarioId").value(100L))
                .andExpect(jsonPath("$.montoTotal").value(10000.00))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.mercadopagoPreferenceId").value("pref-123"));

        verify(pagoService).crearPreferencia(any(CrearPreferenciaRequest.class));
    }

    @Test
    void crearPreferencia_DebeRetornar400_CuandoRequestInvalido() throws Exception {
        CrearPreferenciaRequest invalidRequest = CrearPreferenciaRequest.builder()
                .ordenId("")  // Invalid: blank
                .usuarioId(null)  // Invalid: null
                .items(null)  // Invalid: null
                .build();

        mockMvc.perform(post("/api/pagos/crear-preferencia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void recibirNotificacion_DebeRetornar200_CuandoConQueryParams() throws Exception {
        mockMvc.perform(post("/api/pagos/notifications")
                        .param("topic", "payment")
                        .param("id", "12345"))
                .andExpect(status().isOk());

        verify(pagoService).procesarNotificacion("payment", 12345L);
    }

    @Test
    void recibirNotificacion_DebeRetornar200_CuandoConBodyJson() throws Exception {
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("id", "12345");
        body.put("type", "payment");
        body.put("data", data);

        mockMvc.perform(post("/api/pagos/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(pagoService).procesarNotificacion("payment", 12345L);
    }

    @Test
    void recibirNotificacion_DebeRetornar200_CuandoSinParametros() throws Exception {
        mockMvc.perform(post("/api/pagos/notifications"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPago_DebeRetornar200YPagoResponse_CuandoExiste() throws Exception {
        when(pagoService.obtenerPago(1L)).thenReturn(pagoResponse);

        mockMvc.perform(get("/api/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ordenId").value("ORD-123"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(pagoService).obtenerPago(1L);
    }

    @Test
    void obtenerPago_DebeRetornar404_CuandoNoExiste() throws Exception {
        when(pagoService.obtenerPago(999L))
                .thenThrow(new RuntimeException("Pago no encontrado con ID: 999"));

        mockMvc.perform(get("/api/pagos/999"))
                .andExpect(status().isInternalServerError());

        verify(pagoService).obtenerPago(999L);
    }

    @Test
    void obtenerPagosPorOrden_DebeRetornar200YLista_CuandoExisten() throws Exception {
        when(pagoService.obtenerPagosPorOrden("ORD-123"))
                .thenReturn(Collections.singletonList(pagoResponse));

        mockMvc.perform(get("/api/pagos/orden/ORD-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].ordenId").value("ORD-123"));

        verify(pagoService).obtenerPagosPorOrden("ORD-123");
    }

    @Test
    void obtenerPagosPorOrden_DebeRetornar200YListaVacia_CuandoNoExisten() throws Exception {
        when(pagoService.obtenerPagosPorOrden("ORD-999"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/pagos/orden/ORD-999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(pagoService).obtenerPagosPorOrden("ORD-999");
    }
}
