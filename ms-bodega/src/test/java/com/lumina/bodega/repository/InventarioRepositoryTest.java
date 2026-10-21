package com.lumina.bodega.repository;

import com.lumina.bodega.entity.Inventario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventarioRepositoryTest {

    @Mock
    private InventarioRepository inventarioRepository;

    private Inventario inventario1;
    private Inventario inventario2;

    @BeforeEach
    void setUp() {
        inventario1 = Inventario.builder()
                .id(1L)
                .productoId(100L)
                .nombreProducto("Laptop HP")
                .stock(50)
                .stockMinimo(10)
                .ubicacionBodega("Bodega A")
                .fechaActualizacion(LocalDateTime.now())
                .build();

        inventario2 = Inventario.builder()
                .id(2L)
                .productoId(200L)
                .nombreProducto("Monitor Dell")
                .stock(100)
                .stockMinimo(20)
                .ubicacionBodega("Bodega B")
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }

    @Test
    void findByProductoId_DebeRetornarOptionalConInventario_CuandoExiste() {
        when(inventarioRepository.findByProductoId(100L)).thenReturn(Optional.of(inventario1));

        Optional<Inventario> resultado = inventarioRepository.findByProductoId(100L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getProductoId()).isEqualTo(100L);
        assertThat(resultado.get().getNombreProducto()).isEqualTo("Laptop HP");
        verify(inventarioRepository).findByProductoId(100L);
    }

    @Test
    void findByProductoId_DebeRetornarOptionalVacio_CuandoNoExiste() {
        when(inventarioRepository.findByProductoId(999L)).thenReturn(Optional.empty());

        Optional<Inventario> resultado = inventarioRepository.findByProductoId(999L);

        assertThat(resultado).isEmpty();
        verify(inventarioRepository).findByProductoId(999L);
    }

    @Test
    void findByStockLessThanEqual_DebeRetornarListaDeInventarios_CuandoExisten() {
        when(inventarioRepository.findByStockLessThanEqual(10)).thenReturn(Arrays.asList(inventario1));

        List<Inventario> resultado = inventarioRepository.findByStockLessThanEqual(10);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProductoId()).isEqualTo(100L);
        verify(inventarioRepository).findByStockLessThanEqual(10);
    }

    @Test
    void findByStockLessThanEqual_DebeRetornarListaVacia_CuandoNoExisten() {
        when(inventarioRepository.findByStockLessThanEqual(5)).thenReturn(Collections.emptyList());

        List<Inventario> resultado = inventarioRepository.findByStockLessThanEqual(5);

        assertThat(resultado).isEmpty();
        verify(inventarioRepository).findByStockLessThanEqual(5);
    }

    @Test
    void save_DebePersistirInventario_CuandoEsNuevo() {
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario1);

        Inventario guardado = inventarioRepository.save(inventario1);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isEqualTo(1L);
        assertThat(guardado.getProductoId()).isEqualTo(100L);
        verify(inventarioRepository).save(inventario1);
    }

    @Test
    void findById_DebeRetornarOptionalConInventario_CuandoExiste() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario1));

        Optional<Inventario> resultado = inventarioRepository.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        verify(inventarioRepository).findById(1L);
    }

    @Test
    void findById_DebeRetornarOptionalVacio_CuandoNoExiste() {
        when(inventarioRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Inventario> resultado = inventarioRepository.findById(999L);

        assertThat(resultado).isEmpty();
        verify(inventarioRepository).findById(999L);
    }

    @Test
    void findAll_DebeRetornarTodosLosInventarios() {
        when(inventarioRepository.findAll()).thenReturn(Arrays.asList(inventario1, inventario2));

        List<Inventario> resultado = inventarioRepository.findAll();

        assertThat(resultado).hasSize(2);
        verify(inventarioRepository).findAll();
    }

    @Test
    void deleteById_DebeEliminarInventario_CuandoExiste() {
        inventarioRepository.deleteById(1L);

        verify(inventarioRepository).deleteById(1L);
    }
}
