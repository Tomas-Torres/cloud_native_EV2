package com.lumina.bodega.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class InventarioTest {

    @Test
    void builder_DebeCrearInventarioConTodosLosCampos() {
        Inventario inventario = Inventario.builder()
                .id(1L)
                .productoId(100L)
                .nombreProducto("Laptop HP")
                .stock(50)
                .stockMinimo(10)
                .ubicacionBodega("Bodega A")
                .fechaActualizacion(LocalDateTime.now())
                .build();

        assertThat(inventario).isNotNull();
        assertThat(inventario.getId()).isEqualTo(1L);
        assertThat(inventario.getProductoId()).isEqualTo(100L);
        assertThat(inventario.getNombreProducto()).isEqualTo("Laptop HP");
        assertThat(inventario.getStock()).isEqualTo(50);
        assertThat(inventario.getStockMinimo()).isEqualTo(10);
        assertThat(inventario.getUbicacionBodega()).isEqualTo("Bodega A");
    }

    @Test
    void noArgsConstructor_DebeCrearInventarioVacio() {
        Inventario inventario = new Inventario();

        assertThat(inventario).isNotNull();
        assertThat(inventario.getId()).isNull();
        assertThat(inventario.getProductoId()).isNull();
        assertThat(inventario.getNombreProducto()).isNull();
        assertThat(inventario.getStock()).isNull();
        assertThat(inventario.getStockMinimo()).isNull();
    }

    @Test
    void allArgsConstructor_DebeCrearInventarioConTodosLosCampos() {
        LocalDateTime now = LocalDateTime.now();
        Inventario inventario = new Inventario(
                1L,
                100L,
                "Laptop HP",
                50,
                10,
                "Bodega A",
                now
        );

        assertThat(inventario).isNotNull();
        assertThat(inventario.getId()).isEqualTo(1L);
        assertThat(inventario.getProductoId()).isEqualTo(100L);
        assertThat(inventario.getNombreProducto()).isEqualTo("Laptop HP");
        assertThat(inventario.getStock()).isEqualTo(50);
        assertThat(inventario.getStockMinimo()).isEqualTo(10);
        assertThat(inventario.getUbicacionBodega()).isEqualTo("Bodega A");
        assertThat(inventario.getFechaActualizacion()).isEqualTo(now);
    }

    @Test
    void settersAndGetters_DebenFuncionarCorrectamente() {
        Inventario inventario = new Inventario();
        LocalDateTime now = LocalDateTime.now();

        inventario.setId(1L);
        inventario.setProductoId(100L);
        inventario.setNombreProducto("Laptop HP");
        inventario.setStock(50);
        inventario.setStockMinimo(10);
        inventario.setUbicacionBodega("Bodega A");
        inventario.setFechaActualizacion(now);

        assertThat(inventario.getId()).isEqualTo(1L);
        assertThat(inventario.getProductoId()).isEqualTo(100L);
        assertThat(inventario.getNombreProducto()).isEqualTo("Laptop HP");
        assertThat(inventario.getStock()).isEqualTo(50);
        assertThat(inventario.getStockMinimo()).isEqualTo(10);
        assertThat(inventario.getUbicacionBodega()).isEqualTo("Bodega A");
        assertThat(inventario.getFechaActualizacion()).isEqualTo(now);
    }

    @Test
    void isStockCritico_DebeRetornarTrue_CuandoStockEsIgualAlMinimo() {
        Inventario inventario = Inventario.builder()
                .stock(10)
                .stockMinimo(10)
                .build();

        assertThat(inventario.isStockCritico()).isTrue();
    }

    @Test
    void isStockCritico_DebeRetornarTrue_CuandoStockEsMenorAlMinimo() {
        Inventario inventario = Inventario.builder()
                .stock(5)
                .stockMinimo(10)
                .build();

        assertThat(inventario.isStockCritico()).isTrue();
    }

    @Test
    void isStockCritico_DebeRetornarFalse_CuandoStockEsMayorAlMinimo() {
        Inventario inventario = Inventario.builder()
                .stock(20)
                .stockMinimo(10)
                .build();

        assertThat(inventario.isStockCritico()).isFalse();
    }
}
