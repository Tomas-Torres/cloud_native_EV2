package com.lumina.delivery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumina.delivery.entity.Delivery;
import com.lumina.delivery.entity.EstadoDelivery;
import com.lumina.delivery.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeliveryController.class)
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeliveryService deliveryService;

    private Delivery delivery;

    @BeforeEach
    void setUp() {
        delivery = Delivery.builder()
                .id(1L)
                .ordenId("ORD-123")
                .usuarioId(100L)
                .direccionEntrega("Calle Principal 123")
                .estado(EstadoDelivery.PREPARANDO)
                .repartidorNombre("Juan Pérez")
                .fechaEstimadaEntrega(LocalDateTime.now().plusDays(2))
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    @Test
    void obtener_DebeRetornar200YDelivery_CuandoExiste() throws Exception {
        when(deliveryService.obtenerPorId(1L)).thenReturn(delivery);

        mockMvc.perform(get("/api/delivery/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ordenId").value("ORD-123"))
                .andExpect(jsonPath("$.estado").value("PREPARANDO"));
    }

    @Test
    void obtenerPorOrden_DebeRetornar200YDelivery_CuandoExiste() throws Exception {
        when(deliveryService.obtenerPorOrden("ORD-123")).thenReturn(delivery);

        mockMvc.perform(get("/api/delivery/orden/ORD-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ordenId").value("ORD-123"))
                .andExpect(jsonPath("$.usuarioId").value(100L));
    }

    @Test
    void crear_DebeRetornar201YDelivery_CuandoEsValido() throws Exception {
        when(deliveryService.crearDelivery(any(Delivery.class))).thenReturn(delivery);

        mockMvc.perform(post("/api/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(delivery)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ordenId").value("ORD-123"));
    }

    @Test
    void actualizarEstado_DebeRetornar200YDelivery_CuandoExiste() throws Exception {
        when(deliveryService.actualizarEstado(anyLong(), any(EstadoDelivery.class), any(String.class)))
                .thenReturn(delivery);

        Map<String, String> body = new HashMap<>();
        body.put("estado", "REPARTO");
        body.put("descripcion", "En camino al destino");

        mockMvc.perform(patch("/api/delivery/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));
    }
}
