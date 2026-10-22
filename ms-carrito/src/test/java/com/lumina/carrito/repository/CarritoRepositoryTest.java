package com.lumina.carrito.repository;

import com.lumina.carrito.entity.Carrito;
import com.lumina.carrito.entity.ItemCarrito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarritoRepositoryTest {

    @Mock
    private CarritoRepository carritoRepository;

    private Carrito carrito;

    @BeforeEach
    void setUp() {
        ItemCarrito item = ItemCarrito.builder()
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
    void findByUsuarioId_ConUsuarioIdExistente_RetornaCarrito() {
        when(carritoRepository.findByUsuarioId(anyLong())).thenReturn(Optional.of(carrito));

        Optional<Carrito> resultado = carritoRepository.findByUsuarioId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getUsuarioId()).isEqualTo(1L);
        assertThat(resultado.get().getItems()).hasSize(1);
    }

    @Test
    void findByUsuarioId_ConUsuarioIdNoExistente_RetornaEmpty() {
        when(carritoRepository.findByUsuarioId(anyLong())).thenReturn(Optional.empty());

        Optional<Carrito> resultado = carritoRepository.findByUsuarioId(999L);

        assertThat(resultado).isEmpty();
    }
}
