package com.lumina.bff.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeliveryProxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring cargue correctamente
    }

    @Test
    void obtenerDelivery_EndpointExiste() throws Exception {
        mockMvc.perform(get("/api/delivery/1"))
            .andExpect(status().isOk());
    }

    @Test
    void obtenerDeliveryPorOrden_EndpointExiste() throws Exception {
        mockMvc.perform(get("/api/delivery/orden/ORD-123"))
            .andExpect(status().isOk());
    }

    @Test
    void actualizarEstado_EndpointExiste() throws Exception {
        mockMvc.perform(patch("/api/delivery/1/estado")
            .contentType("application/json")
            .content("{\"estado\":\"entregado\"}"))
            .andExpect(status().isOk());
    }
}
