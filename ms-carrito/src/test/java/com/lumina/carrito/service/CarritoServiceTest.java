package com.lumina.carrito.service;

import com.lumina.carrito.entity.Carrito;
import com.lumina.carrito.entity.ItemCarrito;
import com.lumina.carrito.repository.CarritoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @InjectMocks
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
    void obtenerCarrito_ConUsuarioIdExistente_RetornaCarrito() {
        when(carritoRepository.findByUsuarioId(anyLong())).thenReturn(Optional.of(carrito));

        Carrito resultado = carritoService.obtenerCarrito(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsuarioId()).isEqualTo(1L);
        assertThat(resultado.getItems()).hasSize(1);
        verify(carritoRepository).findByUsuarioId(1L);
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void obtenerCarrito_ConUsuarioIdNoExistente_CreaNuevoCarrito() {
        Carrito nuevoCarrito = Carrito.builder().usuarioId(999L).items(new ArrayList<>()).build();
        when(carritoRepository.findByUsuarioId(anyLong())).thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class))).thenReturn(nuevoCarrito);

        Carrito resultado = carritoService.obtenerCarrito(999L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsuarioId()).isEqualTo(999L);
        assertThat(resultado.getItems()).isEmpty();
        verify(carritoRepository).findByUsuarioId(999L);
        verify(carritoRepository).save(any(Carrito.class));
    }

    @Test
    void agregarProducto_ConProductoNuevo_AgregaItem() {
        Map<String, Object> data = new HashMap<>();
        data.put("productoId", 200L);
        data.put("nombreProducto", "Nuevo Producto");
        data.put("precioUnitario", "149.99");
        data.put("imagenUrl", "http://example.com/new-image.jpg");

        when(carritoRepository.findByUsuarioId(anyLong())).thenReturn(Optional.of(carrito));
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        Carrito resultado = carritoService.agregarProducto(1L, data);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getItems()).hasSize(2);
        assertThat(resultado.getItems()).anyMatch(item -> 
            item.getProductoId().equals(200L) && 
            item.getNombreProducto().equals("Nuevo Producto") &&
            item.getCantidad() == 1
        );
        verify(carritoRepository).findByUsuarioId(1L);
        verify(carritoRepository).save(carrito);
    }

    @Test
    void agregarProducto_ConProductoExistente_IncrementaCantidad() {
        Map<String, Object> data = new HashMap<>();
        data.put("productoId", 100L);
        data.put("nombreProducto", "Producto Test");
        data.put("precioUnitario", "99.99");
        data.put("imagenUrl", "http://example.com/image.jpg");

        when(carritoRepository.findByUsuarioId(anyLong())).thenReturn(Optional.of(carrito));
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        Carrito resultado = carritoService.agregarProducto(1L, data);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getItems()).hasSize(1);
        assertThat(resultado.getItems().get(0).getCantidad()).isEqualTo(3);
        verify(carritoRepository).findByUsuarioId(1L);
        verify(carritoRepository).save(carrito);
    }

    @Test
    void agregarProducto_ConDataInvalida_LanzaExcepcion() {
        Map<String, Object> data = new HashMap<>();
        data.put("productoId", "invalid");

        when(carritoRepository.findByUsuarioId(anyLong())).thenReturn(Optional.of(carrito));

        assertThatThrownBy(() -> carritoService.agregarProducto(1L, data))
                .isInstanceOf(NumberFormatException.class);

        verify(carritoRepository).findByUsuarioId(1L);
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void eliminarProducto_ConProductoExistente_EliminaItem() {
        when(carritoRepository.findByUsuarioId(anyLong())).thenReturn(Optional.of(carrito));
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        Carrito resultado = carritoService.eliminarProducto(1L, 100L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getItems()).isEmpty();
        verify(carritoRepository).findByUsuarioId(1L);
        verify(carritoRepository).save(carrito);
    }

    @Test
    void eliminarProducto_ConProductoNoExistente_NoHaceNada() {
        when(carritoRepository.findByUsuarioId(anyLong())).thenReturn(Optional.of(carrito));
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        Carrito resultado = carritoService.eliminarProducto(1L, 999L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getItems()).hasSize(1);
        verify(carritoRepository).findByUsuarioId(1L);
        verify(carritoRepository).save(carrito);
    }

    @Test
    void vaciarCarrito_ConUsuarioIdValido_VaciaItems() {
        when(carritoRepository.findByUsuarioId(anyLong())).thenReturn(Optional.of(carrito));
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        carritoService.vaciarCarrito(1L);

        assertThat(carrito.getItems()).isEmpty();
        verify(carritoRepository).findByUsuarioId(1L);
        verify(carritoRepository).save(carrito);
    }
}
