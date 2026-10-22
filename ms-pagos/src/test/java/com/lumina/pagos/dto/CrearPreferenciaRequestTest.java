package com.lumina.pagos.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class CrearPreferenciaRequestTest {

    @Test
    void builder_DebeCrearRequestConTodosLosCampos() {
        CrearPreferenciaRequest.ItemRequest item = CrearPreferenciaRequest.ItemRequest.builder()
                .titulo("Producto Test")
                .cantidad(2)
                .precioUnitario(new BigDecimal("5000.00"))
                .descripcion("Descripción del producto")
                .imagenUrl("http://imagen.com/producto.jpg")
                .build();

        CrearPreferenciaRequest request = CrearPreferenciaRequest.builder()
                .ordenId("ORD-123")
                .usuarioId(100L)
                .items(Collections.singletonList(item))
                .emailComprador("test@example.com")
                .build();

        assertThat(request).isNotNull();
        assertThat(request.getOrdenId()).isEqualTo("ORD-123");
        assertThat(request.getUsuarioId()).isEqualTo(100L);
        assertThat(request.getItems()).hasSize(1);
        assertThat(request.getEmailComprador()).isEqualTo("test@example.com");
    }

    @Test
    void noArgsConstructor_DebeCrearRequestVacio() {
        CrearPreferenciaRequest request = new CrearPreferenciaRequest();

        assertThat(request).isNotNull();
        assertThat(request.getOrdenId()).isNull();
        assertThat(request.getUsuarioId()).isNull();
        assertThat(request.getItems()).isNull();
    }

    @Test
    void allArgsConstructor_DebeCrearRequestConTodosLosCampos() {
        CrearPreferenciaRequest.ItemRequest item = new CrearPreferenciaRequest.ItemRequest(
                "Producto Test",
                2,
                new BigDecimal("5000.00"),
                "Descripción del producto",
                "http://imagen.com/producto.jpg"
        );

        CrearPreferenciaRequest request = new CrearPreferenciaRequest(
                "ORD-123",
                100L,
                Collections.singletonList(item),
                "test@example.com"
        );

        assertThat(request).isNotNull();
        assertThat(request.getOrdenId()).isEqualTo("ORD-123");
        assertThat(request.getUsuarioId()).isEqualTo(100L);
        assertThat(request.getItems()).hasSize(1);
        assertThat(request.getEmailComprador()).isEqualTo("test@example.com");
    }

    @Test
    void settersAndGetters_DebenFuncionarCorrectamente() {
        CrearPreferenciaRequest request = new CrearPreferenciaRequest();

        request.setOrdenId("ORD-123");
        request.setUsuarioId(100L);
        request.setEmailComprador("test@example.com");

        assertThat(request.getOrdenId()).isEqualTo("ORD-123");
        assertThat(request.getUsuarioId()).isEqualTo(100L);
        assertThat(request.getEmailComprador()).isEqualTo("test@example.com");
    }

    @Test
    void itemRequestBuilder_DebeCrearItemConTodosLosCampos() {
        CrearPreferenciaRequest.ItemRequest item = CrearPreferenciaRequest.ItemRequest.builder()
                .titulo("Producto Test")
                .cantidad(2)
                .precioUnitario(new BigDecimal("5000.00"))
                .descripcion("Descripción del producto")
                .imagenUrl("http://imagen.com/producto.jpg")
                .build();

        assertThat(item).isNotNull();
        assertThat(item.getTitulo()).isEqualTo("Producto Test");
        assertThat(item.getCantidad()).isEqualTo(2);
        assertThat(item.getPrecioUnitario()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(item.getDescripcion()).isEqualTo("Descripción del producto");
        assertThat(item.getImagenUrl()).isEqualTo("http://imagen.com/producto.jpg");
    }

    @Test
    void itemRequestNoArgsConstructor_DebeCrearItemVacio() {
        CrearPreferenciaRequest.ItemRequest item = new CrearPreferenciaRequest.ItemRequest();

        assertThat(item).isNotNull();
        assertThat(item.getTitulo()).isNull();
        assertThat(item.getCantidad()).isNull();
        assertThat(item.getPrecioUnitario()).isNull();
    }

    @Test
    void itemRequestAllArgsConstructor_DebeCrearItemConTodosLosCampos() {
        CrearPreferenciaRequest.ItemRequest item = new CrearPreferenciaRequest.ItemRequest(
                "Producto Test",
                2,
                new BigDecimal("5000.00"),
                "Descripción del producto",
                "http://imagen.com/producto.jpg"
        );

        assertThat(item).isNotNull();
        assertThat(item.getTitulo()).isEqualTo("Producto Test");
        assertThat(item.getCantidad()).isEqualTo(2);
        assertThat(item.getPrecioUnitario()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(item.getDescripcion()).isEqualTo("Descripción del producto");
        assertThat(item.getImagenUrl()).isEqualTo("http://imagen.com/producto.jpg");
    }

    @Test
    void itemRequestSettersAndGetters_DebenFuncionarCorrectamente() {
        CrearPreferenciaRequest.ItemRequest item = new CrearPreferenciaRequest.ItemRequest();

        item.setTitulo("Producto Test");
        item.setCantidad(2);
        item.setPrecioUnitario(new BigDecimal("5000.00"));
        item.setDescripcion("Descripción del producto");
        item.setImagenUrl("http://imagen.com/producto.jpg");

        assertThat(item.getTitulo()).isEqualTo("Producto Test");
        assertThat(item.getCantidad()).isEqualTo(2);
        assertThat(item.getPrecioUnitario()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(item.getDescripcion()).isEqualTo("Descripción del producto");
        assertThat(item.getImagenUrl()).isEqualTo("http://imagen.com/producto.jpg");
    }

    @Test
    void itemRequestConCamposOpcionalesNulos_DebeCrearItemCorrectamente() {
        CrearPreferenciaRequest.ItemRequest item = CrearPreferenciaRequest.ItemRequest.builder()
                .titulo("Producto Test")
                .cantidad(2)
                .precioUnitario(new BigDecimal("5000.00"))
                .descripcion(null)
                .imagenUrl(null)
                .build();

        assertThat(item).isNotNull();
        assertThat(item.getTitulo()).isEqualTo("Producto Test");
        assertThat(item.getCantidad()).isEqualTo(2);
        assertThat(item.getPrecioUnitario()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(item.getDescripcion()).isNull();
        assertThat(item.getImagenUrl()).isNull();
    }
}
