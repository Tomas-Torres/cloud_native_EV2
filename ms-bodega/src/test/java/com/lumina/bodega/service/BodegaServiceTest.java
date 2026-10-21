package com.lumina.bodega.service;

import com.lumina.bodega.entity.AlertaStock;
import com.lumina.bodega.entity.Inventario;
import com.lumina.bodega.repository.AlertaStockRepository;
import com.lumina.bodega.repository.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BodegaServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private AlertaStockRepository alertaStockRepository;

    @InjectMocks
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
    void listarInventario_DebeRetornarListaVacia_CuandoNoHayRegistros() {
        when(inventarioRepository.findAll()).thenReturn(Collections.emptyList());

        List<Inventario> resultado = bodegaService.listarInventario();

        assertThat(resultado).isEmpty();
        verify(inventarioRepository).findAll();
    }

    @Test
    void listarInventario_DebeRetornarListaDeInventarios_CuandoExisten() {
        when(inventarioRepository.findAll()).thenReturn(List.of(inventario));

        List<Inventario> resultado = bodegaService.listarInventario();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProductoId()).isEqualTo(100L);
        verify(inventarioRepository).findAll();
    }

    @Test
    void crearInventario_DebeGuardarInventario_CuandoEsValido() {
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        Inventario resultado = bodegaService.crearInventario(inventario);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getProductoId()).isEqualTo(100L);
        verify(inventarioRepository).save(inventario);
    }

    @Test
    void obtenerStock_DebeRetornarInventario_CuandoExiste() {
        when(inventarioRepository.findByProductoId(100L)).thenReturn(Optional.of(inventario));

        Inventario resultado = bodegaService.obtenerStock(100L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getProductoId()).isEqualTo(100L);
        verify(inventarioRepository).findByProductoId(100L);
    }

    @Test
    void obtenerStock_DebeLanzarRuntimeException_CuandoNoExiste() {
        when(inventarioRepository.findByProductoId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bodegaService.obtenerStock(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No hay registro de inventario para producto: 999");
    }

    @Test
    void actualizarStock_DebeAgregarStock_CuandoEsValido() {
        when(inventarioRepository.findByProductoId(100L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        Inventario resultado = bodegaService.actualizarStock(100L, 20);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getStock()).isEqualTo(70);
        verify(inventarioRepository).findByProductoId(100L);
        verify(inventarioRepository).save(inventario);
    }

    @Test
    void actualizarStock_DebeGenerarAlerta_CuandoStockEsCritico() {
        inventario.setStock(15);
        when(inventarioRepository.findByProductoId(100L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);
        when(alertaStockRepository.findByProductoIdAndResueltaFalse(100L)).thenReturn(Collections.emptyList());
        when(alertaStockRepository.save(any(AlertaStock.class))).thenReturn(alerta);

        Inventario resultado = bodegaService.actualizarStock(100L, -5);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getStock()).isEqualTo(10);
        verify(alertaStockRepository).save(any(AlertaStock.class));
    }

    @Test
    void actualizarStock_DebeResolverAlertas_CuandoStockSeNormaliza() {
        inventario.setStock(5);
        when(inventarioRepository.findByProductoId(100L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);
        when(alertaStockRepository.findByProductoIdAndResueltaFalse(100L)).thenReturn(List.of(alerta));

        Inventario resultado = bodegaService.actualizarStock(100L, 20);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getStock()).isEqualTo(25);
        verify(alertaStockRepository).save(alerta);
    }

    @Test
    void descontarStock_DebeDescontarStock_CuandoEsSuficiente() {
        when(inventarioRepository.findByProductoId(100L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        Inventario resultado = bodegaService.descontarStock(100L, 10);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getStock()).isEqualTo(40);
        verify(inventarioRepository).findByProductoId(100L);
        verify(inventarioRepository).save(inventario);
    }

    @Test
    void descontarStock_DebeLanzarRuntimeException_CuandoStockInsuficiente() {
        inventario.setStock(5);
        when(inventarioRepository.findByProductoId(100L)).thenReturn(Optional.of(inventario));

        assertThatThrownBy(() -> bodegaService.descontarStock(100L, 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Stock insuficiente para producto: 100");
    }

    @Test
    void descontarStock_DebeGenerarAlerta_CuandoStockEsCritico() {
        inventario.setStock(15);
        when(inventarioRepository.findByProductoId(100L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);
        when(alertaStockRepository.findByProductoIdAndResueltaFalse(100L)).thenReturn(Collections.emptyList());
        when(alertaStockRepository.save(any(AlertaStock.class))).thenReturn(alerta);

        Inventario resultado = bodegaService.descontarStock(100L, 10);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getStock()).isEqualTo(5);
        verify(alertaStockRepository).save(any(AlertaStock.class));
    }

    @Test
    void descontarStock_DebeResolverAlertas_CuandoStockSeNormaliza() {
        inventario.setStock(5);
        when(inventarioRepository.findByProductoId(100L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);
        when(alertaStockRepository.findByProductoIdAndResueltaFalse(100L)).thenReturn(List.of(alerta));

        Inventario resultado = bodegaService.descontarStock(100L, -5);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getStock()).isEqualTo(10);
        verify(alertaStockRepository).save(alerta);
    }

    @Test
    void obtenerAlertas_DebeRetornarListaVacia_CuandoNoHayAlertas() {
        when(alertaStockRepository.findByResueltaFalseOrderByFechaCreacionDesc())
                .thenReturn(Collections.emptyList());

        List<AlertaStock> resultado = bodegaService.obtenerAlertas();

        assertThat(resultado).isEmpty();
        verify(alertaStockRepository).findByResueltaFalseOrderByFechaCreacionDesc();
    }

    @Test
    void obtenerAlertas_DebeRetornarListaDeAlertas_CuandoExisten() {
        when(alertaStockRepository.findByResueltaFalseOrderByFechaCreacionDesc())
                .thenReturn(List.of(alerta));

        List<AlertaStock> resultado = bodegaService.obtenerAlertas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProductoId()).isEqualTo(100L);
        assertThat(resultado.get(0).getResuelta()).isFalse();
        verify(alertaStockRepository).findByResueltaFalseOrderByFechaCreacionDesc();
    }
}
