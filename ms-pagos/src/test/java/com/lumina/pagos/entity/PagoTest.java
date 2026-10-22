package com.lumina.pagos.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PagoTest {

    @Test
    void builder_DebeCrearPagoConTodosLosCampos() {
        Pago pago = Pago.builder()
                .id(1L)
                .ordenId("ORD-123")
                .usuarioId(100L)
                .montoTotal(new BigDecimal("10000.00"))
                .moneda("CLP")
                .estado(EstadoPago.PENDIENTE)
                .mercadopagoPreferenceId("pref-123")
                .mercadopagoPaymentId("pay-456")
                .mercadopagoStatus("approved")
                .mercadopagoStatusDetail("accredited")
                .metodoPago("credit_card")
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        assertThat(pago).isNotNull();
        assertThat(pago.getId()).isEqualTo(1L);
        assertThat(pago.getOrdenId()).isEqualTo("ORD-123");
        assertThat(pago.getUsuarioId()).isEqualTo(100L);
        assertThat(pago.getMontoTotal()).isEqualTo(new BigDecimal("10000.00"));
        assertThat(pago.getMoneda()).isEqualTo("CLP");
        assertThat(pago.getEstado()).isEqualTo(EstadoPago.PENDIENTE);
        assertThat(pago.getMercadopagoPreferenceId()).isEqualTo("pref-123");
        assertThat(pago.getMercadopagoPaymentId()).isEqualTo("pay-456");
        assertThat(pago.getMercadopagoStatus()).isEqualTo("approved");
        assertThat(pago.getMercadopagoStatusDetail()).isEqualTo("accredited");
        assertThat(pago.getMetodoPago()).isEqualTo("credit_card");
    }

    @Test
    void noArgsConstructor_DebeCrearPagoVacio() {
        Pago pago = new Pago();

        assertThat(pago).isNotNull();
        assertThat(pago.getId()).isNull();
        assertThat(pago.getOrdenId()).isNull();
        assertThat(pago.getEstado()).isNull();
    }

    @Test
    void allArgsConstructor_DebeCrearPagoConTodosLosCampos() {
        LocalDateTime now = LocalDateTime.now();
        Pago pago = new Pago(
                1L,
                "ORD-123",
                100L,
                new BigDecimal("10000.00"),
                "CLP",
                EstadoPago.PENDIENTE,
                "pref-123",
                "pay-456",
                "approved",
                "accredited",
                "credit_card",
                now,
                now
        );

        assertThat(pago).isNotNull();
        assertThat(pago.getId()).isEqualTo(1L);
        assertThat(pago.getOrdenId()).isEqualTo("ORD-123");
        assertThat(pago.getUsuarioId()).isEqualTo(100L);
        assertThat(pago.getMontoTotal()).isEqualTo(new BigDecimal("10000.00"));
        assertThat(pago.getMoneda()).isEqualTo("CLP");
        assertThat(pago.getEstado()).isEqualTo(EstadoPago.PENDIENTE);
    }

    @Test
    void settersAndGetters_DebenFuncionarCorrectamente() {
        Pago pago = new Pago();
        LocalDateTime now = LocalDateTime.now();

        pago.setId(1L);
        pago.setOrdenId("ORD-123");
        pago.setUsuarioId(100L);
        pago.setMontoTotal(new BigDecimal("10000.00"));
        pago.setMoneda("CLP");
        pago.setEstado(EstadoPago.APROBADO);
        pago.setMercadopagoPreferenceId("pref-123");
        pago.setMercadopagoPaymentId("pay-456");
        pago.setMercadopagoStatus("approved");
        pago.setMercadopagoStatusDetail("accredited");
        pago.setMetodoPago("credit_card");
        pago.setFechaCreacion(now);
        pago.setFechaActualizacion(now);

        assertThat(pago.getId()).isEqualTo(1L);
        assertThat(pago.getOrdenId()).isEqualTo("ORD-123");
        assertThat(pago.getUsuarioId()).isEqualTo(100L);
        assertThat(pago.getMontoTotal()).isEqualTo(new BigDecimal("10000.00"));
        assertThat(pago.getMoneda()).isEqualTo("CLP");
        assertThat(pago.getEstado()).isEqualTo(EstadoPago.APROBADO);
        assertThat(pago.getMercadopagoPreferenceId()).isEqualTo("pref-123");
        assertThat(pago.getMercadopagoPaymentId()).isEqualTo("pay-456");
        assertThat(pago.getMercadopagoStatus()).isEqualTo("approved");
        assertThat(pago.getMercadopagoStatusDetail()).isEqualTo("accredited");
        assertThat(pago.getMetodoPago()).isEqualTo("credit_card");
        assertThat(pago.getFechaCreacion()).isEqualTo(now);
        assertThat(pago.getFechaActualizacion()).isEqualTo(now);
    }
}
