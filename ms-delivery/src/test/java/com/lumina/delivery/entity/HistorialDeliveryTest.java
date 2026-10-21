package com.lumina.delivery.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class HistorialDeliveryTest {

    private HistorialDelivery historialDelivery;
    private Delivery delivery;

    @BeforeEach
    void setUp() {
        delivery = Delivery.builder()
                .id(1L)
                .ordenId("ORD-123")
                .usuarioId(100L)
                .direccionEntrega("Calle Principal 123")
                .estado(EstadoDelivery.PREPARANDO)
                .build();

        historialDelivery = HistorialDelivery.builder()
                .id(1L)
                .estado(EstadoDelivery.PREPARANDO)
                .descripcion("Pedido recibido y en preparación")
                .fecha(LocalDateTime.now())
                .delivery(delivery)
                .build();
    }

    @Test
    void onCreate_DebeEstablecerFecha_CuandoEsNull() {
        historialDelivery.setFecha(null);

        historialDelivery.onCreate();

        assertThat(historialDelivery.getFecha()).isNotNull();
    }

    @Test
    void onCreate_NoDebeModificarFecha_CuandoYaTieneValor() {
        LocalDateTime fechaExistente = LocalDateTime.now().minusDays(1);
        historialDelivery.setFecha(fechaExistente);

        historialDelivery.onCreate();

        assertThat(historialDelivery.getFecha()).isEqualTo(fechaExistente);
    }

    @Test
    void builder_DebeCrearHistorialDeliveryConTodosLosCampos() {
        HistorialDelivery historialConstruido = HistorialDelivery.builder()
                .id(1L)
                .estado(EstadoDelivery.PREPARANDO)
                .descripcion("Pedido recibido y en preparación")
                .fecha(LocalDateTime.now())
                .delivery(delivery)
                .build();

        assertThat(historialConstruido.getId()).isEqualTo(1L);
        assertThat(historialConstruido.getEstado()).isEqualTo(EstadoDelivery.PREPARANDO);
        assertThat(historialConstruido.getDescripcion()).isEqualTo("Pedido recibido y en preparación");
        assertThat(historialConstruido.getDelivery()).isEqualTo(delivery);
    }

    @Test
    void builder_DebeFuncionarConCamposOpcionalesNull() {
        HistorialDelivery historialConCamposNull = HistorialDelivery.builder()
                .id(1L)
                .estado(EstadoDelivery.PREPARANDO)
                .descripcion("Pedido recibido")
                .build();

        assertThat(historialConCamposNull.getId()).isEqualTo(1L);
        assertThat(historialConCamposNull.getEstado()).isEqualTo(EstadoDelivery.PREPARANDO);
        assertThat(historialConCamposNull.getDescripcion()).isEqualTo("Pedido recibido");
        assertThat(historialConCamposNull.getFecha()).isNull();
        assertThat(historialConCamposNull.getDelivery()).isNull();
    }

    @Test
    void settersAndGetters_DebenFuncionarCorrectamente() {
        historialDelivery.setId(2L);
        historialDelivery.setEstado(EstadoDelivery.REPARTO);
        historialDelivery.setDescripcion("En camino al destino");
        historialDelivery.setFecha(LocalDateTime.now());

        assertThat(historialDelivery.getId()).isEqualTo(2L);
        assertThat(historialDelivery.getEstado()).isEqualTo(EstadoDelivery.REPARTO);
        assertThat(historialDelivery.getDescripcion()).isEqualTo("En camino al destino");
    }

    @Test
    void constructorSinArgumentos_DebeCrearInstanciaVacia() {
        HistorialDelivery historialVacio = new HistorialDelivery();

        assertThat(historialVacio).isNotNull();
        assertThat(historialVacio.getId()).isNull();
        assertThat(historialVacio.getEstado()).isNull();
        assertThat(historialVacio.getDescripcion()).isNull();
        assertThat(historialVacio.getFecha()).isNull();
        assertThat(historialVacio.getDelivery()).isNull();
    }

    @Test
    void constructorConTodosLosArgumentos_DebeCrearInstanciaCompleta() {
        HistorialDelivery historialCompleto = new HistorialDelivery(
                1L,
                EstadoDelivery.PREPARANDO,
                "Pedido recibido y en preparación",
                LocalDateTime.now(),
                delivery
        );

        assertThat(historialCompleto.getId()).isEqualTo(1L);
        assertThat(historialCompleto.getEstado()).isEqualTo(EstadoDelivery.PREPARANDO);
        assertThat(historialCompleto.getDescripcion()).isEqualTo("Pedido recibido y en preparación");
        assertThat(historialCompleto.getDelivery()).isEqualTo(delivery);
    }
}
