package com.lumina.bodega.repository;

import com.lumina.bodega.entity.AlertaStock;
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
class AlertaStockRepositoryTest {

    @Mock
    private AlertaStockRepository alertaStockRepository;

    private AlertaStock alerta1;
    private AlertaStock alerta2;

    @BeforeEach
    void setUp() {
        alerta1 = AlertaStock.builder()
                .id(1L)
                .productoId(100L)
                .nombreProducto("Laptop HP")
                .stockActual(5)
                .stockMinimo(10)
                .resuelta(false)
                .fechaCreacion(LocalDateTime.now())
                .build();

        alerta2 = AlertaStock.builder()
                .id(2L)
                .productoId(200L)
                .nombreProducto("Monitor Dell")
                .stockActual(15)
                .stockMinimo(20)
                .resuelta(false)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    @Test
    void findByResueltaFalseOrderByFechaCreacionDesc_DebeRetornarListaDeAlertas_CuandoExisten() {
        when(alertaStockRepository.findByResueltaFalseOrderByFechaCreacionDesc())
                .thenReturn(Arrays.asList(alerta1, alerta2));

        List<AlertaStock> resultado = alertaStockRepository.findByResueltaFalseOrderByFechaCreacionDesc();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getProductoId()).isEqualTo(100L);
        assertThat(resultado.get(1).getProductoId()).isEqualTo(200L);
        verify(alertaStockRepository).findByResueltaFalseOrderByFechaCreacionDesc();
    }

    @Test
    void findByResueltaFalseOrderByFechaCreacionDesc_DebeRetornarListaVacia_CuandoNoExisten() {
        when(alertaStockRepository.findByResueltaFalseOrderByFechaCreacionDesc())
                .thenReturn(Collections.emptyList());

        List<AlertaStock> resultado = alertaStockRepository.findByResueltaFalseOrderByFechaCreacionDesc();

        assertThat(resultado).isEmpty();
        verify(alertaStockRepository).findByResueltaFalseOrderByFechaCreacionDesc();
    }

    @Test
    void findByProductoIdAndResueltaFalse_DebeRetornarListaDeAlertas_CuandoExisten() {
        when(alertaStockRepository.findByProductoIdAndResueltaFalse(100L))
                .thenReturn(Arrays.asList(alerta1));

        List<AlertaStock> resultado = alertaStockRepository.findByProductoIdAndResueltaFalse(100L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProductoId()).isEqualTo(100L);
        assertThat(resultado.get(0).getResuelta()).isFalse();
        verify(alertaStockRepository).findByProductoIdAndResueltaFalse(100L);
    }

    @Test
    void findByProductoIdAndResueltaFalse_DebeRetornarListaVacia_CuandoNoExisten() {
        when(alertaStockRepository.findByProductoIdAndResueltaFalse(999L))
                .thenReturn(Collections.emptyList());

        List<AlertaStock> resultado = alertaStockRepository.findByProductoIdAndResueltaFalse(999L);

        assertThat(resultado).isEmpty();
        verify(alertaStockRepository).findByProductoIdAndResueltaFalse(999L);
    }

    @Test
    void save_DebePersistirAlerta_CuandoEsNueva() {
        when(alertaStockRepository.save(any(AlertaStock.class))).thenReturn(alerta1);

        AlertaStock guardada = alertaStockRepository.save(alerta1);

        assertThat(guardada).isNotNull();
        assertThat(guardada.getId()).isEqualTo(1L);
        assertThat(guardada.getProductoId()).isEqualTo(100L);
        verify(alertaStockRepository).save(alerta1);
    }

    @Test
    void findById_DebeRetornarOptionalConAlerta_CuandoExiste() {
        when(alertaStockRepository.findById(1L)).thenReturn(Optional.of(alerta1));

        Optional<AlertaStock> resultado = alertaStockRepository.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        verify(alertaStockRepository).findById(1L);
    }

    @Test
    void findById_DebeRetornarOptionalVacio_CuandoNoExiste() {
        when(alertaStockRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<AlertaStock> resultado = alertaStockRepository.findById(999L);

        assertThat(resultado).isEmpty();
        verify(alertaStockRepository).findById(999L);
    }

    @Test
    void findAll_DebeRetornarTodasLasAlertas() {
        when(alertaStockRepository.findAll()).thenReturn(Arrays.asList(alerta1, alerta2));

        List<AlertaStock> resultado = alertaStockRepository.findAll();

        assertThat(resultado).hasSize(2);
        verify(alertaStockRepository).findAll();
    }

    @Test
    void deleteById_DebeEliminarAlerta_CuandoExiste() {
        alertaStockRepository.deleteById(1L);

        verify(alertaStockRepository).deleteById(1L);
    }
}
