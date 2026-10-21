package com.lumina.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumina.usuarios.entity.Usuario;
import com.lumina.usuarios.entity.Rol;
import com.lumina.usuarios.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UsuarioController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        })
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan Pérez")
                .email("juan@example.com")
                .password("password123")
                .run("12345678-9")
                .direccion("Calle 123")
                .rol(Rol.CLIENTE)
                .activo(true)
                .build();
    }

    @Test
    void registrar_ConDatosValidos_Retorna201Created() throws Exception {
        when(usuarioService.registrar(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan@example.com"))
                .andExpect(jsonPath("$.message").value("Usuario registrado exitosamente"));
    }

    @Test
    void registrar_ConEmailDuplicado_Retorna400BadRequest() throws Exception {
        when(usuarioService.registrar(any(Usuario.class)))
                .thenThrow(new RuntimeException("El email ya está registrado"));

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El email ya está registrado"));
    }

    @Test
    void registrar_ConRunDuplicado_Retorna400BadRequest() throws Exception {
        when(usuarioService.registrar(any(Usuario.class)))
                .thenThrow(new RuntimeException("El RUN ya está registrado"));

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El RUN ya está registrado"));
    }

    @Test
    void login_ConCredencialesValidas_Retorna200Ok() throws Exception {
        Map<String, Object> loginResponse = new HashMap<>();
        loginResponse.put("token", "jwtToken");
        loginResponse.put("usuario", Map.of(
                "id", 1L,
                "nombre", "Juan Pérez",
                "email", "juan@example.com",
                "rol", "CLIENTE"
        ));

        when(usuarioService.login(anyString(), anyString())).thenReturn(loginResponse);

        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "juan@example.com");
        credentials.put("password", "password123");

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwtToken"))
                .andExpect(jsonPath("$.usuario.id").value(1))
                .andExpect(jsonPath("$.usuario.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.usuario.email").value("juan@example.com"))
                .andExpect(jsonPath("$.usuario.rol").value("CLIENTE"));
    }

    @Test
    void login_ConCredencialesInvalidas_Retorna400BadRequest() throws Exception {
        when(usuarioService.login(anyString(), anyString()))
                .thenThrow(new RuntimeException("Credenciales incorrectas"));

        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "juan@example.com");
        credentials.put("password", "wrongPassword");

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Credenciales incorrectas"));
    }

    @Test
    void obtenerUsuario_ConIdValido_Retorna200Ok() throws Exception {
        when(usuarioService.obtenerPorId(anyLong())).thenReturn(usuario);

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan@example.com"))
                .andExpect(jsonPath("$.password").value((String) null));
    }

    @Test
    void obtenerUsuario_ConIdNoExistente_Retorna400BadRequest() throws Exception {
        when(usuarioService.obtenerPorId(anyLong()))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(get("/api/usuarios/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Usuario no encontrado"));
    }
}
