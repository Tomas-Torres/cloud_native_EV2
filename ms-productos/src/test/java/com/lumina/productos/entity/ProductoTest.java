package com.lumina.productos.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductoTest {

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
            .stock(10)
            .imagenUrl("zapatillas.jpg")
            .categoria("Calzado")
            .marca(marca)
            .activo(true)
            .build();
}

@Test
void onCreate_DebeEstablecerActivoTrue_CuandoEsNull() {
    producto.setActivo(null);

    producto.onCreate();

    assertThat(producto.getActivo()).isTrue();
}

@Test
void onCreate_NoDebeModificarActivo_CuandoYaTieneValor() {
    producto.setActivo(false);

    producto.onCreate();

    assertThat(producto.getActivo()).isFalse();
}

@Test
void onCreate_DebeEstablecerStockCero_CuandoEsNull() {
    producto.setStock(null);

    producto.onCreate();

    assertThat(producto.getStock()).isEqualTo(0);
}

@Test
void onCreate_NoDebeModificarStock_CuandoYaTieneValor() {
    producto.setStock(10);

    producto.onCreate();

    assertThat(producto.getStock()).isEqualTo(10);
}

@Test
void builder_DebeCrearProductoConTodosLosCampos() {
    Producto productoConstruido = Producto.builder()
            .id(1L)
            .nombre("Zapatillas Running")
            .descripcion("Zapatillas para correr")
            .precio(new BigDecimal("99.99"))
            .stock(10)
            .imagenUrl("zapatillas.jpg")
            .categoria("Calzado")
            .marca(marca)
            .activo(true)
            .build();

    assertThat(productoConstruido.getId()).isEqualTo(1L);
    assertThat(productoConstruido.getNombre()).isEqualTo("Zapatillas Running");
    assertThat(productoConstruido.getDescripcion()).isEqualTo("Zapatillas para correr");
    assertThat(productoConstruido.getPrecio()).isEqualTo(new BigDecimal("99.99"));
    assertThat(productoConstruido.getStock()).isEqualTo(10);
    assertThat(productoConstruido.getImagenUrl()).isEqualTo("zapatillas.jpg");
    assertThat(productoConstruido.getCategoria()).isEqualTo("Calzado");
    assertThat(productoConstruido.getMarca()).isEqualTo(marca);
    assertThat(productoConstruido.getActivo()).isTrue();
}

@Test
void builder_DebeFuncionarConCamposOpcionalesNull() {
    Producto productoConCamposNull = Producto.builder()
            .id(1L)
            .nombre("Producto Simple")
            .precio(new BigDecimal("49.99"))
            .build();

    assertThat(productoConCamposNull.getId()).isEqualTo(1L);
    assertThat(productoConCamposNull.getNombre()).isEqualTo("Producto Simple");
    assertThat(productoConCamposNull.getPrecio()).isEqualTo(new BigDecimal("49.99"));
    assertThat(productoConCamposNull.getStock()).isNull();
    assertThat(productoConCamposNull.getDescripcion()).isNull();
    assertThat(productoConCamposNull.getImagenUrl()).isNull();
    assertThat(productoConCamposNull.getCategoria()).isNull();
    assertThat(productoConCamposNull.getMarca()).isNull();
}

@Test
void settersAndGetters_DebenFuncionarCorrectamente() {
    producto.setId(2L);
    producto.setNombre("Producto Actualizado");
    producto.setDescripcion("Nueva descripción");
    producto.setPrecio(new BigDecimal("149.99"));
    producto.setStock(20);
    producto.setImagenUrl("nueva-imagen.jpg");
    producto.setCategoria("Ropa");
    producto.setActivo(false);

    assertThat(producto.getId()).isEqualTo(2L);
    assertThat(producto.getNombre()).isEqualTo("Producto Actualizado");
    assertThat(producto.getDescripcion()).isEqualTo("Nueva descripción");
    assertThat(producto.getPrecio()).isEqualTo(new BigDecimal("149.99"));
    assertThat(producto.getStock()).isEqualTo(20);
    assertThat(producto.getImagenUrl()).isEqualTo("nueva-imagen.jpg");
    assertThat(producto.getCategoria()).isEqualTo("Ropa");
    assertThat(producto.getActivo()).isFalse();
}

@Test
void constructorSinArgumentos_DebeCrearInstanciaVacia() {
    Producto productoVacio = new Producto();

    assertThat(productoVacio).isNotNull();
    assertThat(productoVacio.getId()).isNull();
    assertThat(productoVacio.getNombre()).isNull();
    assertThat(productoVacio.getDescripcion()).isNull();
    assertThat(productoVacio.getPrecio()).isNull();
    assertThat(productoVacio.getStock()).isNull();
    assertThat(productoVacio.getImagenUrl()).isNull();
    assertThat(productoVacio.getCategoria()).isNull();
    assertThat(productoVacio.getMarca()).isNull();
    assertThat(productoVacio.getActivo()).isNull();
}

@Test
void constructorConTodosLosArgumentos_DebeCrearInstanciaCompleta() {
    Producto productoCompleto = new Producto(
            1L,
            "Zapatillas Running",
            "Zapatillas para correr",
            new BigDecimal("99.99"),
            10,
            "zapatillas.jpg",
            "Calzado",
            marca,
            true
    );

    assertThat(productoCompleto.getId()).isEqualTo(1L);
    assertThat(productoCompleto.getNombre()).isEqualTo("Zapatillas Running");
    assertThat(productoCompleto.getDescripcion()).isEqualTo("Zapatillas para correr");
    assertThat(productoCompleto.getPrecio()).isEqualTo(new BigDecimal("99.99"));
    assertThat(productoCompleto.getStock()).isEqualTo(10);
    assertThat(productoCompleto.getImagenUrl()).isEqualTo("zapatillas.jpg");
    assertThat(productoCompleto.getCategoria()).isEqualTo("Calzado");
    assertThat(productoCompleto.getMarca()).isEqualTo(marca);
    assertThat(productoCompleto.getActivo()).isTrue();
}

@Test
void precio_DebeManejarBigDecimalCorrectamente() {
    BigDecimal precio1 = new BigDecimal("99.99");
    BigDecimal precio2 = new BigDecimal("149.99");

    producto.setPrecio(precio1);
    assertThat(producto.getPrecio()).isEqualTo(precio1);

    producto.setPrecio(precio2);
    assertThat(producto.getPrecio()).isEqualTo(precio2);
    assertThat(producto.getPrecio()).isNotEqualTo(precio1);
}

@Test
void stock_DebeManejarIntegerCorrectamente() {
    producto.setStock(15);
    assertThat(producto.getStock()).isEqualTo(15);

    producto.setStock(0);
    assertThat(producto.getStock()).isEqualTo(0);
}}
