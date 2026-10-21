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
class UsuariosProxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring cargue correctamente
    }

    @Test
    void registrar_EndpointExiste() throws Exception {
        mockMvc.perform(post("/api/usuarios/registro")
                .contentType("application/json")
                .content("{\"email\":\"test@lumina.cl\",\"password\":\"123456\"}"))
            .andExpect(request().asyncStarted());
    }

    @Test
    void login_EndpointExiste() throws Exception {
        mockMvc.perform(post("/api/usuarios/login")
                .contentType("application/json")
                .content("{\"email\":\"test@lumina.cl\",\"password\":\"123456\"}"))
            .andExpect(request().asyncStarted());
    }

    @Test
    void obtenerUsuario_EndpointExiste() throws Exception {
        mockMvc.perform(get("/api/usuarios/1"))
            .andExpect(request().asyncStarted());
    }
}
