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
class CarritoProxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring cargue correctamente
    }

    @Test
    void obtenerCarrito_EndpointExiste() throws Exception {
        mockMvc.perform(get("/api/carrito/1"))
            .andExpect(status().isOk());
    }

    @Test
    void agregarProducto_EndpointExiste() throws Exception {
        mockMvc.perform(post("/api/carrito/1/agregar")
            .contentType("application/json")
            .content("{\"productoId\":1,\"cantidad\":2}"))
            .andExpect(status().isOk());
    }

    @Test
    void eliminarProducto_EndpointExiste() throws Exception {
        mockMvc.perform(delete("/api/carrito/1/eliminar/1"))
            .andExpect(status().isOk());
    }
}
