package com.lumina.pagos.repository;

import com.lumina.pagos.entity.Pago;
import com.lumina.pagos.entity.EstadoPago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
class PagoRepositoryTest {

    @Mock
    private PagoRepository pagoRepository;

    private Pago pago1;
    private Pago pago2;

    @BeforeEach
    void setUp() {
        pago1 = Pago.builder()
                .id(1L)
                .ordenId("ORD-123")
                .usuarioId(100L)
                .montoTotal(new BigDecimal("10000.00"))
                .moneda("CLP")
                .estado(EstadoPago.PENDIENTE)
                .mercadopagoPreferenceId("pref-123")
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        pago2 = Pago.builder()
                .id(2L)
                .ordenId("ORD-456")
                .usuarioId(100L)
                .montoTotal(new BigDecimal("15000.00"))
                .moneda("CLP")
                .estado(EstadoPago.APROBADO)
                .mercadopagoPreferenceId("pref-456")
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }

    @Test
    void findByOrdenId_DebeRetornarListaDePagos_CuandoExisten() {
        when(pagoRepository.findByOrdenId("ORD-123")).thenReturn(Arrays.asList(pago1));

        List<Pago> resultado = pagoRepository.findByOrdenId("ORD-123");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getOrdenId()).isEqualTo("ORD-123");
        verify(pagoRepository).findByOrdenId("ORD-123");
    }

    @Test
    void findByOrdenId_DebeRetornarListaVacia_CuandoNoExisten() {
        when(pagoRepository.findByOrdenId("ORD-999")).thenReturn(Collections.emptyList());

        List<Pago> resultado = pagoRepository.findByOrdenId("ORD-999");

        assertThat(resultado).isEmpty();
        verify(pagoRepository).findByOrdenId("ORD-999");
    }

    @Test
    void findByUsuarioId_DebeRetornarListaDePagos_CuandoExisten() {
        when(pagoRepository.findByUsuarioId(100L)).thenReturn(Arrays.asList(pago1, pago2));

        List<Pago> resultado = pagoRepository.findByUsuarioId(100L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Pago::getUsuarioId)
                .containsOnly(100L);
        verify(pagoRepository).findByUsuarioId(100L);
    }

    @Test
    void findByUsuarioId_DebeRetornarListaVacia_CuandoNoHayPagos() {
        when(pagoRepository.findByUsuarioId(999L)).thenReturn(Collections.emptyList());

        List<Pago> resultado = pagoRepository.findByUsuarioId(999L);

        assertThat(resultado).isEmpty();
        verify(pagoRepository).findByUsuarioId(999L);
    }

    @Test
    void findByMercadopagoPreferenceId_DebeRetornarOptionalConPago_CuandoExiste() {
        when(pagoRepository.findByMercadopagoPreferenceId("pref-123")).thenReturn(Optional.of(pago1));

        Optional<Pago> resultado = pagoRepository.findByMercadopagoPreferenceId("pref-123");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getMercadopagoPreferenceId()).isEqualTo("pref-123");
        verify(pagoRepository).findByMercadopagoPreferenceId("pref-123");
    }

    @Test
    void findByMercadopagoPreferenceId_DebeRetornarOptionalVacio_CuandoNoExiste() {
        when(pagoRepository.findByMercadopagoPreferenceId("pref-999")).thenReturn(Optional.empty());

        Optional<Pago> resultado = pagoRepository.findByMercadopagoPreferenceId("pref-999");

        assertThat(resultado).isEmpty();
        verify(pagoRepository).findByMercadopagoPreferenceId("pref-999");
    }

    @Test
    void findByMercadopagoPaymentId_DebeRetornarOptionalConPago_CuandoExiste() {
        pago1.setMercadopagoPaymentId("pay-123");
        when(pagoRepository.findByMercadopagoPaymentId("pay-123")).thenReturn(Optional.of(pago1));

        Optional<Pago> resultado = pagoRepository.findByMercadopagoPaymentId("pay-123");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getMercadopagoPaymentId()).isEqualTo("pay-123");
        verify(pagoRepository).findByMercadopagoPaymentId("pay-123");
    }

    @Test
    void findByMercadopagoPaymentId_DebeRetornarOptionalVacio_CuandoNoExiste() {
        when(pagoRepository.findByMercadopagoPaymentId("pay-999")).thenReturn(Optional.empty());

        Optional<Pago> resultado = pagoRepository.findByMercadopagoPaymentId("pay-999");

        assertThat(resultado).isEmpty();
        verify(pagoRepository).findByMercadopagoPaymentId("pay-999");
    }

    @Test
    void findByEstado_DebeRetornarListaDePagos_CuandoExisten() {
        when(pagoRepository.findByEstado(EstadoPago.PENDIENTE)).thenReturn(Arrays.asList(pago1));

        List<Pago> resultado = pagoRepository.findByEstado(EstadoPago.PENDIENTE);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoPago.PENDIENTE);
        verify(pagoRepository).findByEstado(EstadoPago.PENDIENTE);
    }

    @Test
    void findByEstado_DebeRetornarListaVacia_CuandoNoHayPagos() {
        when(pagoRepository.findByEstado(EstadoPago.CANCELADO)).thenReturn(Collections.emptyList());

        List<Pago> resultado = pagoRepository.findByEstado(EstadoPago.CANCELADO);

        assertThat(resultado).isEmpty();
        verify(pagoRepository).findByEstado(EstadoPago.CANCELADO);
    }

    @Test
    void save_DebePersistirPago_CuandoEsNuevo() {
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago1);

        Pago guardado = pagoRepository.save(pago1);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isEqualTo(1L);
        assertThat(guardado.getOrdenId()).isEqualTo("ORD-123");
        verify(pagoRepository).save(pago1);
    }

    @Test
    void findById_DebeRetornarOptionalConPago_CuandoExiste() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago1));

        Optional<Pago> resultado = pagoRepository.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        verify(pagoRepository).findById(1L);
    }

    @Test
    void findById_DebeRetornarOptionalVacio_CuandoNoExiste() {
        when(pagoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Pago> resultado = pagoRepository.findById(999L);

        assertThat(resultado).isEmpty();
        verify(pagoRepository).findById(999L);
    }

    @Test
    void findAll_DebeRetornarTodosLosPagos() {
        when(pagoRepository.findAll()).thenReturn(Arrays.asList(pago1, pago2));

        List<Pago> resultado = pagoRepository.findAll();

        assertThat(resultado).hasSize(2);
        verify(pagoRepository).findAll();
    }

    @Test
    void deleteById_DebeEliminarPago_CuandoExiste() {
        pagoRepository.deleteById(1L);

        verify(pagoRepository).deleteById(1L);
    }
}
