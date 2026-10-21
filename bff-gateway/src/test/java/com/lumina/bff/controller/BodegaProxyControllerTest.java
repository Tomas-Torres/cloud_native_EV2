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
class BodegaProxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring cargue correctamente
    }

    @Test
    void listarInventario_EndpointExiste() throws Exception {
        mockMvc.perform(get("/api/bodega/inventario"))
            .andExpect(request().asyncStarted());
    }

    @Test
    void obtenerStock_EndpointExiste() throws Exception {
        mockMvc.perform(get("/api/bodega/inventario/1"))
            .andExpect(request().asyncStarted());
    }

    @Test
    void crearInventario_EndpointExiste() throws Exception {
        mockMvc.perform(post("/api/bodega/inventario")
                .contentType("application/json")
                .content("{\"productoId\":1,\"cantidad\":10}"))
            .andExpect(request().asyncStarted());
    }

    @Test
    void agregarStock_EndpointExiste() throws Exception {
        mockMvc.perform(patch("/api/bodega/inventario/1/agregar")
                .contentType("application/json")
                .content("{\"cantidad\":5}"))
            .andExpect(request().asyncStarted());
    }

    @Test
    void descontarStock_EndpointExiste() throws Exception {
        mockMvc.perform(patch("/api/bodega/inventario/1/descontar")
                .contentType("application/json")
                .content("{\"cantidad\":3}"))
            .andExpect(request().asyncStarted());
    }

    @Test
    void obtenerAlertas_EndpointExiste() throws Exception {
        mockMvc.perform(get("/api/bodega/alertas"))
            .andExpect(request().asyncStarted());
    }
}
