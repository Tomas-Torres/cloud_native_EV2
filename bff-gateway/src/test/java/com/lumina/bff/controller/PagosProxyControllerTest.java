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
class PagosProxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring cargue correctamente
    }

    @Test
    void crearPreferencia_EndpointExiste() throws Exception {
        mockMvc.perform(post("/api/pagos/crear-preferencia")
            .contentType("application/json")
            .content("{\"ordenId\":\"ORD-123\",\"monto\":99.99}"))
            .andExpect(status().isOk());
    }

    @Test
    void obtenerPago_EndpointExiste() throws Exception {
        mockMvc.perform(get("/api/pagos/1"))
            .andExpect(status().isOk());
    }

    @Test
    void obtenerPagosPorOrden_EndpointExiste() throws Exception {
        mockMvc.perform(get("/api/pagos/orden/ORD-123"))
            .andExpect(status().isOk());
    }
}
