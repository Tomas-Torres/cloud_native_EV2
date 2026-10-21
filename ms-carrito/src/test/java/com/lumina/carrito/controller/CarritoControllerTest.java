package com.lumina.carrito.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumina.carrito.entity.Carrito;
import com.lumina.carrito.entity.ItemCarrito;
import com.lumina.carrito.service.CarritoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CarritoController.class)
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarritoService carritoService;

    private Carrito carrito;
    private ItemCarrito item;

    @BeforeEach
    void setUp() {
        item = ItemCarrito.builder()
                .id(1L)
                .productoId(100L)
                .nombreProducto("Producto Test")
                .precioUnitario(new BigDecimal("99.99"))
                .cantidad(2)
                .imagenUrl("http://example.com/image.jpg")
                .build();

        carrito = Carrito.builder()
                .id(1L)
                .usuarioId(1L)
                .items(new ArrayList<>(List.of(item)))
                .build();
    }

    @Test
    void obtenerCarrito_ConUsuarioIdValido_Retorna200Ok() throws Exception {
        when(carritoService.obtenerCarrito(anyLong())).thenReturn(carrito);

        mockMvc.perform(get("/api/carrito/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].productoId").value(100))
                .andExpect(jsonPath("$.items[0].nombreProducto").value("Producto Test"));
    }

    @Test
    void obtenerCarrito_ConUsuarioIdNoExistente_CreaNuevoCarrito() throws Exception {
        Carrito nuevoCarrito = Carrito.builder()
                .id(2L)
                .usuarioId(999L)
                .items(new ArrayList<>())
                .build();
        
        when(carritoService.obtenerCarrito(anyLong())).thenReturn(nuevoCarrito);

        mockMvc.perform(get("/api/carrito/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.usuarioId").value(999))
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void agregarProducto_ConDatosValidos_Retorna200Ok() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("productoId", 200L);
        data.put("nombreProducto", "Nuevo Producto");
        data.put("precioUnitario", "149.99");
        data.put("imagenUrl", "http://example.com/new-image.jpg");

        when(carritoService.agregarProducto(anyLong(), anyMap())).thenReturn(carrito);

        mockMvc.perform(post("/api/carrito/1/agregar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void agregarProducto_ConProductoExistente_IncrementaCantidad() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("productoId", 100L);
        data.put("nombreProducto", "Producto Test");
        data.put("precioUnitario", "99.99");
        data.put("imagenUrl", "http://example.com/image.jpg");

        ItemCarrito itemActualizado = ItemCarrito.builder()
                .id(1L)
                .productoId(100L)
                .nombreProducto("Producto Test")
                .precioUnitario(new BigDecimal("99.99"))
                .cantidad(3)
                .imagenUrl("http://example.com/image.jpg")
                .build();

        Carrito carritoActualizado = Carrito.builder()
                .id(1L)
                .usuarioId(1L)
                .items(new ArrayList<>(List.of(itemActualizado)))
                .build();

        when(carritoService.agregarProducto(anyLong(), anyMap())).thenReturn(carritoActualizado);

        mockMvc.perform(post("/api/carrito/1/agregar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].cantidad").value(3));
    }

    @Test
    void eliminarProducto_ConProductoExistente_Retorna200Ok() throws Exception {
        Carrito carritoVacio = Carrito.builder()
                .id(1L)
                .usuarioId(1L)
                .items(new ArrayList<>())
                .build();

        when(carritoService.eliminarProducto(anyLong(), anyLong())).thenReturn(carritoVacio);

        mockMvc.perform(delete("/api/carrito/1/eliminar/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void vaciarCarrito_ConUsuarioIdValido_Retorna204NoContent() throws Exception {
        org.mockito.Mockito.doNothing().when(carritoService).vaciarCarrito(anyLong());

        mockMvc.perform(delete("/api/carrito/1/vaciar"))
                .andExpect(status().isNoContent());
    }
}
