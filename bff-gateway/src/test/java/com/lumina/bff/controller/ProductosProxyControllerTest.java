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
class ProductosProxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring cargue correctamente
    }

    @Test
    void listarProductos_EndpointExiste() throws Exception {
        mockMvc.perform(get("/api/productos"))
            .andExpect(status().isOk());
    }

    @Test
    void obtenerProducto_EndpointExiste() throws Exception {
        mockMvc.perform(get("/api/productos/1"))
            .andExpect(status().isOk());
    }

    @Test
    void listarMarcas_EndpointExiste() throws Exception {
        mockMvc.perform(get("/api/productos/marcas"))
            .andExpect(status().isOk());
    }
}
