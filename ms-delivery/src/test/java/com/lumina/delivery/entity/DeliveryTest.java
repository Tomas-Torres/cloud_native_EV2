package com.lumina.delivery.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DeliveryTest {

    private Delivery delivery;

    @BeforeEach
    void setUp() {
        delivery = Delivery.builder()
                .id(1L)
                .ordenId("ORD-123")
                .usuarioId(100L)
                .direccionEntrega("Calle Principal 123")
                .estado(EstadoDelivery.PREPARANDO)
                .repartidorNombre("Juan Pérez")
                .fechaEstimadaEntrega(LocalDateTime.now().plusDays(2))
                .fechaCreacion(LocalDateTime.now())
                .historial(new ArrayList<>())
                .build();
    }

    @Test
    void onCreate_DebeEstablecerFechaCreacion_CuandoEsNull() {
        delivery.setFechaCreacion(null);

        delivery.onCreate();

        assertThat(delivery.getFechaCreacion()).isNotNull();
    }

    @Test
    void onCreate_DebeEstablecerEstadoPreparando_CuandoEsNull() {
        delivery.setEstado(null);

        delivery.onCreate();

        assertThat(delivery.getEstado()).isEqualTo(EstadoDelivery.PREPARANDO);
    }

    @Test
    void onCreate_NoDebeModificarEstado_CuandoYaTieneValor() {
        delivery.setEstado(EstadoDelivery.REPARTO);

        delivery.onCreate();

        assertThat(delivery.getEstado()).isEqualTo(EstadoDelivery.REPARTO);
    }

    @Test
    void builder_DebeCrearDeliveryConTodosLosCampos() {
        Delivery deliveryConstruido = Delivery.builder()
                .id(1L)
                .ordenId("ORD-123")
                .usuarioId(100L)
                .direccionEntrega("Calle Principal 123")
                .estado(EstadoDelivery.PREPARANDO)
                .repartidorNombre("Juan Pérez")
                .fechaEstimadaEntrega(LocalDateTime.now().plusDays(2))
                .fechaCreacion(LocalDateTime.now())
                .historial(new ArrayList<>())
                .build();

        assertThat(deliveryConstruido.getId()).isEqualTo(1L);
        assertThat(deliveryConstruido.getOrdenId()).isEqualTo("ORD-123");
        assertThat(deliveryConstruido.getUsuarioId()).isEqualTo(100L);
        assertThat(deliveryConstruido.getDireccionEntrega()).isEqualTo("Calle Principal 123");
        assertThat(deliveryConstruido.getEstado()).isEqualTo(EstadoDelivery.PREPARANDO);
        assertThat(deliveryConstruido.getRepartidorNombre()).isEqualTo("Juan Pérez");
    }

    @Test
    void builder_DebeFuncionarConCamposOpcionalesNull() {
        Delivery deliveryConCamposNull = Delivery.builder()
                .id(1L)
                .ordenId("ORD-123")
                .usuarioId(100L)
                .direccionEntrega("Calle Principal 123")
                .build();

        assertThat(deliveryConCamposNull.getId()).isEqualTo(1L);
        assertThat(deliveryConCamposNull.getOrdenId()).isEqualTo("ORD-123");
        assertThat(deliveryConCamposNull.getUsuarioId()).isEqualTo(100L);
        assertThat(deliveryConCamposNull.getDireccionEntrega()).isEqualTo("Calle Principal 123");
        assertThat(deliveryConCamposNull.getEstado()).isNull();
        assertThat(deliveryConCamposNull.getRepartidorNombre()).isNull();
        assertThat(deliveryConCamposNull.getFechaEstimadaEntrega()).isNull();
    }

    @Test
    void settersAndGetters_DebenFuncionarCorrectamente() {
        delivery.setId(2L);
        delivery.setOrdenId("ORD-456");
        delivery.setUsuarioId(200L);
        delivery.setDireccionEntrega("Avenida Secundaria 456");
        delivery.setEstado(EstadoDelivery.REPARTO);
        delivery.setRepartidorNombre("María García");
        delivery.setFechaEstimadaEntrega(LocalDateTime.now().plusDays(1));

        assertThat(delivery.getId()).isEqualTo(2L);
        assertThat(delivery.getOrdenId()).isEqualTo("ORD-456");
        assertThat(delivery.getUsuarioId()).isEqualTo(200L);
        assertThat(delivery.getDireccionEntrega()).isEqualTo("Avenida Secundaria 456");
        assertThat(delivery.getEstado()).isEqualTo(EstadoDelivery.REPARTO);
        assertThat(delivery.getRepartidorNombre()).isEqualTo("María García");
    }

    @Test
    void constructorSinArgumentos_DebeCrearInstanciaVacia() {
        Delivery deliveryVacio = new Delivery();

        assertThat(deliveryVacio).isNotNull();
        assertThat(deliveryVacio.getId()).isNull();
        assertThat(deliveryVacio.getOrdenId()).isNull();
        assertThat(deliveryVacio.getUsuarioId()).isNull();
        assertThat(deliveryVacio.getDireccionEntrega()).isNull();
        assertThat(deliveryVacio.getEstado()).isNull();
    }

    @Test
    void constructorConTodosLosArgumentos_DebeCrearInstanciaCompleta() {
        List<HistorialDelivery> historial = new ArrayList<>();
        Delivery deliveryCompleto = new Delivery(
                1L,
                "ORD-123",
                100L,
                "Calle Principal 123",
                EstadoDelivery.PREPARANDO,
                "Juan Pérez",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now(),
                LocalDateTime.now(),
                historial
        );

        assertThat(deliveryCompleto.getId()).isEqualTo(1L);
        assertThat(deliveryCompleto.getOrdenId()).isEqualTo("ORD-123");
        assertThat(deliveryCompleto.getUsuarioId()).isEqualTo(100L);
        assertThat(deliveryCompleto.getDireccionEntrega()).isEqualTo("Calle Principal 123");
        assertThat(deliveryCompleto.getEstado()).isEqualTo(EstadoDelivery.PREPARANDO);
    }
}
