package com.lumina.bodega.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumina.bodega.entity.AlertaStock;
import com.lumina.bodega.entity.Inventario;
import com.lumina.bodega.service.BodegaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BodegaController.class)
class BodegaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BodegaService bodegaService;

    private Inventario inventario;
    private AlertaStock alerta;

    @BeforeEach
    void setUp() {
        inventario = Inventario.builder()
                .id(1L)
                .productoId(100L)
                .nombreProducto("Laptop HP")
                .stock(50)
                .stockMinimo(10)
                .ubicacionBodega("Bodega A")
                .fechaActualizacion(LocalDateTime.now())
                .build();

        alerta = AlertaStock.builder()
                .id(1L)
                .productoId(100L)
                .nombreProducto("Laptop HP")
                .stockActual(5)
                .stockMinimo(10)
                .resuelta(false)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    @Test
    void listarInventario_DebeRetornar200YLista_CuandoExisten() throws Exception {
        when(bodegaService.listarInventario()).thenReturn(List.of(inventario));

        mockMvc.perform(get("/api/bodega/inventario"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].productoId").value(100L))
                .andExpect(jsonPath("$[0].nombreProducto").value("Laptop HP"));

        verify(bodegaService).listarInventario();
    }

    @Test
    void listarInventario_DebeRetornar200YListaVacia_CuandoNoExisten() throws Exception {
        when(bodegaService.listarInventario()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/bodega/inventario"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(bodegaService).listarInventario();
    }

    @Test
    void obtenerStock_DebeRetornar200YInventario_CuandoExiste() throws Exception {
        when(bodegaService.obtenerStock(100L)).thenReturn(inventario);

        mockMvc.perform(get("/api/bodega/inventario/100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productoId").value(100L))
                .andExpect(jsonPath("$.nombreProducto").value("Laptop HP"))
                .andExpect(jsonPath("$.stock").value(50));

        verify(bodegaService).obtenerStock(100L);
    }

    @Test
    void obtenerStock_DebeRetornar500_CuandoNoExiste() throws Exception {
        when(bodegaService.obtenerStock(999L))
                .thenThrow(new RuntimeException("No hay registro de inventario para producto: 999"));

        mockMvc.perform(get("/api/bodega/inventario/999"))
                .andExpect(status().isInternalServerError());

        verify(bodegaService).obtenerStock(999L);
    }

    @Test
    void crearInventario_DebeRetornar201YInventario_CuandoEsValido() throws Exception {
        when(bodegaService.crearInventario(any(Inventario.class))).thenReturn(inventario);

        mockMvc.perform(post("/api/bodega/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productoId").value(100L))
                .andExpect(jsonPath("$.nombreProducto").value("Laptop HP"));

        verify(bodegaService).crearInventario(any(Inventario.class));
    }

    @Test
    void agregarStock_DebeRetornar200YInventario_CuandoEsValido() throws Exception {
        when(bodegaService.actualizarStock(100L, 20)).thenReturn(inventario);

        Map<String, Integer> body = new HashMap<>();
        body.put("cantidad", 20);

        mockMvc.perform(patch("/api/bodega/inventario/100/agregar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productoId").value(100L));

        verify(bodegaService).actualizarStock(100L, 20);
    }

    @Test
    void descontarStock_DebeRetornar200YInventario_CuandoEsValido() throws Exception {
        when(bodegaService.descontarStock(100L, 10)).thenReturn(inventario);

        Map<String, Integer> body = new HashMap<>();
        body.put("cantidad", 10);

        mockMvc.perform(patch("/api/bodega/inventario/100/descontar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productoId").value(100L));

        verify(bodegaService).descontarStock(100L, 10);
    }

    @Test
    void descontarStock_DebeRetornar500_CuandoStockInsuficiente() throws Exception {
        when(bodegaService.descontarStock(100L, 100))
                .thenThrow(new RuntimeException("Stock insuficiente para producto: 100"));

        Map<String, Integer> body = new HashMap<>();
        body.put("cantidad", 100);

        mockMvc.perform(patch("/api/bodega/inventario/100/descontar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isInternalServerError());

        verify(bodegaService).descontarStock(100L, 100);
    }

    @Test
    void obtenerAlertas_DebeRetornar200YLista_CuandoExisten() throws Exception {
        when(bodegaService.obtenerAlertas()).thenReturn(List.of(alerta));

        mockMvc.perform(get("/api/bodega/alertas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].productoId").value(100L))
                .andExpect(jsonPath("$[0].resuelta").value(false));

        verify(bodegaService).obtenerAlertas();
    }

    @Test
    void obtenerAlertas_DebeRetornar200YListaVacia_CuandoNoExisten() throws Exception {
        when(bodegaService.obtenerAlertas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/bodega/alertas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(bodegaService).obtenerAlertas();
    }
}
