package com.lumina.pagos.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoPagoTest {

    @Test
    void valoresEnum_DebenTenerLosValoresCorrectos() {
        assertThat(EstadoPago.values()).hasSize(7);
        assertThat(EstadoPago.values()).containsExactlyInAnyOrder(
                EstadoPago.PENDIENTE,
                EstadoPago.PROCESANDO,
                EstadoPago.APROBADO,
                EstadoPago.RECHAZADO,
                EstadoPago.CANCELADO,
                EstadoPago.REEMBOLSADO,
                EstadoPago.EN_MEDIACION
        );
    }

    @Test
    void valorPENDIENTE_DebeExistir() {
        assertThat(EstadoPago.PENDIENTE).isNotNull();
        assertThat(EstadoPago.PENDIENTE.name()).isEqualTo("PENDIENTE");
    }

    @Test
    void valorPROCESANDO_DebeExistir() {
        assertThat(EstadoPago.PROCESANDO).isNotNull();
        assertThat(EstadoPago.PROCESANDO.name()).isEqualTo("PROCESANDO");
    }

    @Test
    void valorAPROBADO_DebeExistir() {
        assertThat(EstadoPago.APROBADO).isNotNull();
        assertThat(EstadoPago.APROBADO.name()).isEqualTo("APROBADO");
    }

    @Test
    void valorRECHAZADO_DebeExistir() {
        assertThat(EstadoPago.RECHAZADO).isNotNull();
        assertThat(EstadoPago.RECHAZADO.name()).isEqualTo("RECHAZADO");
    }

    @Test
    void valorCANCELADO_DebeExistir() {
        assertThat(EstadoPago.CANCELADO).isNotNull();
        assertThat(EstadoPago.CANCELADO.name()).isEqualTo("CANCELADO");
    }

    @Test
    void valorREEMBOLSADO_DebeExistir() {
        assertThat(EstadoPago.REEMBOLSADO).isNotNull();
        assertThat(EstadoPago.REEMBOLSADO.name()).isEqualTo("REEMBOLSADO");
    }

    @Test
    void valorEN_MEDIACION_DebeExistir() {
        assertThat(EstadoPago.EN_MEDIACION).isNotNull();
        assertThat(EstadoPago.EN_MEDIACION.name()).isEqualTo("EN_MEDIACION");
    }

    @Test
    void valueOf_DebeRetornarValorCorrecto() {
        assertThat(EstadoPago.valueOf("PENDIENTE")).isEqualTo(EstadoPago.PENDIENTE);
        assertThat(EstadoPago.valueOf("APROBADO")).isEqualTo(EstadoPago.APROBADO);
        assertThat(EstadoPago.valueOf("RECHAZADO")).isEqualTo(EstadoPago.RECHAZADO);
    }
}
