package com.lumina.productos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumina.productos.entity.Marca;
import com.lumina.productos.entity.Producto;
import com.lumina.productos.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    private Producto producto;
    private Marca marca;

    @BeforeEach
    void setUp() {
        marca = Marca.builder()
                .id(1L)
                .nombre("Nike")
                .logoUrl("nike-logo.png")
                .build();

        producto = Producto.builder()
                .id(1L)
                .nombre("Zapatillas Running")
                .descripcion("Zapatillas para correr")
                .precio(new BigDecimal("99.99"))
                .imagenUrl("zapatillas.jpg")
                .categoria("Calzado")
                .marca(marca)
                .activo(true)
                .build();
    }

    @Test
    void listar_DebeRetornar200YLista_CuandoHayProductosActivos() throws Exception {
        List<Producto> productos = Arrays.asList(producto);
        when(productoService.listarActivos()).thenReturn(productos);

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Zapatillas Running"))
                .andExpect(jsonPath("$[0].activo").value(true));
    }

    @Test
    void listar_DebeRetornar200YListaVacia_CuandoNoHayProductosActivos() throws Exception {
        when(productoService.listarActivos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void obtener_DebeRetornar200YProducto_CuandoExiste() throws Exception {
        when(productoService.obtenerPorId(1L)).thenReturn(producto);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Zapatillas Running"))
                .andExpect(jsonPath("$.precio").value(99.99))
                .andExpect(jsonPath("$.categoria").value("Calzado"));
    }


    @Test
    void buscar_DebeRetornar200YLista_CuandoHayCoincidencias() throws Exception {
        List<Producto> productos = Arrays.asList(producto);
        when(productoService.buscar("zapatillas")).thenReturn(productos);

        mockMvc.perform(get("/api/productos/buscar").param("q", "zapatillas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Zapatillas Running"));
    }

    @Test
    void buscar_DebeRetornar200YListaVacia_CuandoNoHayCoincidencias() throws Exception {
        when(productoService.buscar("inexistente")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/productos/buscar").param("q", "inexistente"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void buscar_DebeFuncionarConBusquedaCaseInsensitive() throws Exception {
        List<Producto> productos = Arrays.asList(producto);
        when(productoService.buscar("ZAPATILLAS")).thenReturn(productos);

        mockMvc.perform(get("/api/productos/buscar").param("q", "ZAPATILLAS"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombre").value("Zapatillas Running"));
    }

    @Test
    void listarMarcas_DebeRetornar200YLista_CuandoHayMarcas() throws Exception {
        List<Marca> marcas = Arrays.asList(marca);
        when(productoService.listarMarcas()).thenReturn(marcas);

        mockMvc.perform(get("/api/productos/marcas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Nike"));
    }

    @Test
    void listarMarcas_DebeRetornar200YListaVacia_CuandoNoHayMarcas() throws Exception {
        when(productoService.listarMarcas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/productos/marcas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void crear_DebeRetornar201YProducto_CuandoEsValido() throws Exception {
        Producto nuevoProducto = Producto.builder()
                .nombre("Nuevo Producto")
                .descripcion("Descripción")
                .precio(new BigDecimal("49.99"))
                .marca(marca)
                .build();

        when(productoService.crear(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoProducto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Zapatillas Running"));
    }

    @Test
    void actualizar_DebeRetornar200YProductoActualizado_CuandoExiste() throws Exception {
        Producto datosActualizados = Producto.builder()
                .nombre("Zapatillas Actualizadas")
                .descripcion("Nueva descripción")
                .precio(new BigDecimal("149.99"))
                .imagenUrl("nueva-imagen.jpg")
                .categoria("Calzado Premium")
                .marca(marca)
                .build();

        when(productoService.actualizar(anyLong(), any(Producto.class))).thenReturn(producto);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datosActualizados)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void eliminar_DebeRetornar204NoContent_CuandoExiste() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }
}
