package com.lumina.bodega.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AlertaStockTest {

    @Test
    void builder_DebeCrearAlertaStockConTodosLosCampos() {
        AlertaStock alerta = AlertaStock.builder()
                .id(1L)
                .productoId(100L)
                .nombreProducto("Laptop HP")
                .stockActual(5)
                .stockMinimo(10)
                .resuelta(false)
                .fechaCreacion(LocalDateTime.now())
                .build();

        assertThat(alerta).isNotNull();
        assertThat(alerta.getId()).isEqualTo(1L);
        assertThat(alerta.getProductoId()).isEqualTo(100L);
        assertThat(alerta.getNombreProducto()).isEqualTo("Laptop HP");
        assertThat(alerta.getStockActual()).isEqualTo(5);
        assertThat(alerta.getStockMinimo()).isEqualTo(10);
        assertThat(alerta.getResuelta()).isFalse();
    }

    @Test
    void noArgsConstructor_DebeCrearAlertaStockVacio() {
        AlertaStock alerta = new AlertaStock();

        assertThat(alerta).isNotNull();
        assertThat(alerta.getId()).isNull();
        assertThat(alerta.getProductoId()).isNull();
        assertThat(alerta.getNombreProducto()).isNull();
        assertThat(alerta.getStockActual()).isNull();
        assertThat(alerta.getStockMinimo()).isNull();
        assertThat(alerta.getResuelta()).isNull();
    }

    @Test
    void allArgsConstructor_DebeCrearAlertaStockConTodosLosCampos() {
        LocalDateTime now = LocalDateTime.now();
        AlertaStock alerta = new AlertaStock(
                1L,
                100L,
                "Laptop HP",
                5,
                10,
                false,
                now
        );

        assertThat(alerta).isNotNull();
        assertThat(alerta.getId()).isEqualTo(1L);
        assertThat(alerta.getProductoId()).isEqualTo(100L);
        assertThat(alerta.getNombreProducto()).isEqualTo("Laptop HP");
        assertThat(alerta.getStockActual()).isEqualTo(5);
        assertThat(alerta.getStockMinimo()).isEqualTo(10);
        assertThat(alerta.getResuelta()).isFalse();
        assertThat(alerta.getFechaCreacion()).isEqualTo(now);
    }

    @Test
    void settersAndGetters_DebenFuncionarCorrectamente() {
        AlertaStock alerta = new AlertaStock();
        LocalDateTime now = LocalDateTime.now();

        alerta.setId(1L);
        alerta.setProductoId(100L);
        alerta.setNombreProducto("Laptop HP");
        alerta.setStockActual(5);
        alerta.setStockMinimo(10);
        alerta.setResuelta(false);
        alerta.setFechaCreacion(now);

        assertThat(alerta.getId()).isEqualTo(1L);
        assertThat(alerta.getProductoId()).isEqualTo(100L);
        assertThat(alerta.getNombreProducto()).isEqualTo("Laptop HP");
        assertThat(alerta.getStockActual()).isEqualTo(5);
        assertThat(alerta.getStockMinimo()).isEqualTo(10);
        assertThat(alerta.getResuelta()).isFalse();
        assertThat(alerta.getFechaCreacion()).isEqualTo(now);
    }

    @Test
    void builder_ConResueltaTrue_DebeCrearAlertaResuelta() {
        AlertaStock alerta = AlertaStock.builder()
                .id(1L)
                .productoId(100L)
                .nombreProducto("Laptop HP")
                .stockActual(5)
                .stockMinimo(10)
                .resuelta(true)
                .build();

        assertThat(alerta).isNotNull();
        assertThat(alerta.getResuelta()).isTrue();
    }

    @Test
    void setters_ConResueltaTrue_DebeActualizarCorrectamente() {
        AlertaStock alerta = new AlertaStock();
        alerta.setResuelta(true);

        assertThat(alerta.getResuelta()).isTrue();
    }
}
