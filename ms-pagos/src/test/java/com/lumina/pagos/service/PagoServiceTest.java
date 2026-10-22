package com.lumina.pagos.service;

import com.lumina.pagos.dto.CrearPreferenciaRequest;
import com.lumina.pagos.dto.PagoResponse;
import com.lumina.pagos.entity.EstadoPago;
import com.lumina.pagos.entity.Pago;
import com.lumina.pagos.repository.PagoRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PreferenceClient preferenceClient;

    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private PagoService pagoService;

    private CrearPreferenciaRequest request;
    private Pago pago;
    private Preference preference;
    private Payment payment;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pagoService, "notificationUrl", "http://localhost:8080/api/pagos/notifications");

        CrearPreferenciaRequest.ItemRequest item = CrearPreferenciaRequest.ItemRequest.builder()
                .titulo("Producto Test")
                .cantidad(2)
                .precioUnitario(new BigDecimal("5000.00"))
                .descripcion("Descripción del producto")
                .imagenUrl("http://imagen.com/producto.jpg")
                .build();

        request = CrearPreferenciaRequest.builder()
                .ordenId("ORD-123")
                .usuarioId(100L)
                .items(Collections.singletonList(item))
                .emailComprador("test@example.com")
                .build();

        pago = Pago.builder()
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

        preference = mock(Preference.class);
        payment = mock(Payment.class);
    }

    @Test
    void crearPreferencia_DebeRetornarPagoResponse_CuandoEsExitoso() {
        when(preference.getId()).thenReturn("pref-123");
        when(preference.getInitPoint()).thenReturn("https://init.point");
        when(preference.getSandboxInitPoint()).thenReturn("https://sandbox.init.point");
        try {
            when(preferenceClient.create(any())).thenReturn(preference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        PagoResponse resultado = pagoService.crearPreferencia(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getOrdenId()).isEqualTo("ORD-123");
        assertThat(resultado.getUsuarioId()).isEqualTo(100L);
        assertThat(resultado.getMontoTotal()).isEqualTo(new BigDecimal("10000.00"));
        assertThat(resultado.getEstado()).isEqualTo(EstadoPago.PENDIENTE);
        assertThat(resultado.getMercadopagoPreferenceId()).isEqualTo("pref-123");
        assertThat(resultado.getInitPoint()).isEqualTo("https://init.point");
        assertThat(resultado.getSandboxInitPoint()).isEqualTo("https://sandbox.init.point");
    }

    @Test
    void crearPreferencia_DebeCalcularMontoTotalCorrectamente() {
        when(preference.getId()).thenReturn("pref-123");
        when(preference.getInitPoint()).thenReturn("https://init.point");
        when(preference.getSandboxInitPoint()).thenReturn("https://sandbox.init.point");
        try {
            when(preferenceClient.create(any())).thenReturn(preference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        PagoResponse resultado = pagoService.crearPreferencia(request);

        assertThat(resultado.getMontoTotal()).isEqualTo(new BigDecimal("10000.00"));
    }

    @Test
    void crearPreferencia_DebeEstablecerEstadoPendiente() {
        when(preference.getId()).thenReturn("pref-123");
        when(preference.getInitPoint()).thenReturn("https://init.point");
        when(preference.getSandboxInitPoint()).thenReturn("https://sandbox.init.point");
        try {
            when(preferenceClient.create(any())).thenReturn(preference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        PagoResponse resultado = pagoService.crearPreferencia(request);

        assertThat(resultado.getEstado()).isEqualTo(EstadoPago.PENDIENTE);
    }

    @Test
    void crearPreferencia_DebeLanzarRuntimeException_CuandoMercadoPagoApiError() {
        try {
            when(preferenceClient.create(any())).thenThrow(new RuntimeException("Error MercadoPago: API Error"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertThatThrownBy(() -> pagoService.crearPreferencia(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error al crear preferencia de pago");
    }

    @Test
    void crearPreferencia_DebeLanzarRuntimeException_CuandoErrorGenerico() {
        try {
            when(preferenceClient.create(any())).thenThrow(new RuntimeException("Error genérico"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertThatThrownBy(() -> pagoService.crearPreferencia(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error al crear preferencia de pago");
    }

    @Test
    void crearPreferencia_DebeAgregarNotificationUrl_CuandoNoEsLocalhost() {
        ReflectionTestUtils.setField(pagoService, "notificationUrl", "https://production.com/notifications");
        when(preference.getId()).thenReturn("pref-123");
        when(preference.getInitPoint()).thenReturn("https://init.point");
        when(preference.getSandboxInitPoint()).thenReturn("https://sandbox.init.point");
        try {
            when(preferenceClient.create(any())).thenReturn(preference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        PagoResponse resultado = pagoService.crearPreferencia(request);

        assertThat(resultado).isNotNull();
    }

    @Test
    void procesarNotificacion_DebeActualizarPago_CuandoTopicEsPayment() {
        when(payment.getId()).thenReturn(12345L);
        when(payment.getExternalReference()).thenReturn("ORD-123");
        when(payment.getStatus()).thenReturn("approved");
        when(payment.getStatusDetail()).thenReturn("accredited");
        when(payment.getPaymentMethodId()).thenReturn("credit_card");
        try {
            when(paymentClient.get(12345L)).thenReturn(payment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(pagoRepository.findByOrdenId("ORD-123")).thenReturn(Collections.singletonList(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        pagoService.procesarNotificacion("payment", 12345L);

        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    void procesarNotificacion_DebeIgnorar_CuandoTopicNoEsPayment() {
        pagoService.procesarNotificacion("merchant_order", 12345L);

        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    void procesarNotificacion_DebeLogWarning_CuandoPagoNoEncontrado() {
        when(payment.getExternalReference()).thenReturn("ORD-123");
        try {
            when(paymentClient.get(12345L)).thenReturn(payment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(pagoRepository.findByOrdenId("ORD-123")).thenReturn(Collections.emptyList());

        pagoService.procesarNotificacion("payment", 12345L);

        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    void procesarNotificacion_DebeActualizarEstadoAprobado_CuandoPaymentApproved() {
        when(payment.getId()).thenReturn(12345L);
        when(payment.getExternalReference()).thenReturn("ORD-123");
        when(payment.getStatus()).thenReturn("approved");
        when(payment.getStatusDetail()).thenReturn("accredited");
        when(payment.getPaymentMethodId()).thenReturn("credit_card");
        try {
            when(paymentClient.get(12345L)).thenReturn(payment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(pagoRepository.findByOrdenId("ORD-123")).thenReturn(Collections.singletonList(pago));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pagoService.procesarNotificacion("payment", 12345L);

        verify(pagoRepository).save(argThat(p -> p.getEstado() == EstadoPago.APROBADO));
    }

    @Test
    void procesarNotificacion_DebeMapearEstadoPending_Correctamente() {
        when(payment.getId()).thenReturn(12345L);
        when(payment.getExternalReference()).thenReturn("ORD-123");
        when(payment.getStatus()).thenReturn("pending");
        when(payment.getStatusDetail()).thenReturn("accredited");
        when(payment.getPaymentMethodId()).thenReturn("credit_card");
        try {
            when(paymentClient.get(12345L)).thenReturn(payment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(pagoRepository.findByOrdenId("ORD-123")).thenReturn(Collections.singletonList(pago));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pagoService.procesarNotificacion("payment", 12345L);

        verify(pagoRepository).save(argThat(p -> p.getEstado() == EstadoPago.PROCESANDO));
    }

    @Test
    void procesarNotificacion_DebeMapearEstadoRejected_Correctamente() {
        when(payment.getId()).thenReturn(12345L);
        when(payment.getExternalReference()).thenReturn("ORD-123");
        when(payment.getStatus()).thenReturn("rejected");
        when(payment.getStatusDetail()).thenReturn("accredited");
        when(payment.getPaymentMethodId()).thenReturn("credit_card");
        try {
            when(paymentClient.get(12345L)).thenReturn(payment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(pagoRepository.findByOrdenId("ORD-123")).thenReturn(Collections.singletonList(pago));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pagoService.procesarNotificacion("payment", 12345L);

        verify(pagoRepository).save(argThat(p -> p.getEstado() == EstadoPago.RECHAZADO));
    }

    @Test
    void procesarNotificacion_DebeMapearEstadoCancelled_Correctamente() {
        when(payment.getId()).thenReturn(12345L);
        when(payment.getExternalReference()).thenReturn("ORD-123");
        when(payment.getStatus()).thenReturn("cancelled");
        when(payment.getStatusDetail()).thenReturn("accredited");
        when(payment.getPaymentMethodId()).thenReturn("credit_card");
        try {
            when(paymentClient.get(12345L)).thenReturn(payment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(pagoRepository.findByOrdenId("ORD-123")).thenReturn(Collections.singletonList(pago));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pagoService.procesarNotificacion("payment", 12345L);

        verify(pagoRepository).save(argThat(p -> p.getEstado() == EstadoPago.CANCELADO));
    }

    @Test
    void procesarNotificacion_DebeMapearEstadoRefunded_Correctamente() {
        when(payment.getId()).thenReturn(12345L);
        when(payment.getExternalReference()).thenReturn("ORD-123");
        when(payment.getStatus()).thenReturn("refunded");
        when(payment.getStatusDetail()).thenReturn("accredited");
        when(payment.getPaymentMethodId()).thenReturn("credit_card");
        try {
            when(paymentClient.get(12345L)).thenReturn(payment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(pagoRepository.findByOrdenId("ORD-123")).thenReturn(Collections.singletonList(pago));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pagoService.procesarNotificacion("payment", 12345L);

        verify(pagoRepository).save(argThat(p -> p.getEstado() == EstadoPago.REEMBOLSADO));
    }

    @Test
    void procesarNotificacion_DebeMapearEstadoInMediation_Correctamente() {
        when(payment.getId()).thenReturn(12345L);
        when(payment.getExternalReference()).thenReturn("ORD-123");
        when(payment.getStatus()).thenReturn("in_mediation");
        when(payment.getStatusDetail()).thenReturn("accredited");
        when(payment.getPaymentMethodId()).thenReturn("credit_card");
        try {
            when(paymentClient.get(12345L)).thenReturn(payment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(pagoRepository.findByOrdenId("ORD-123")).thenReturn(Collections.singletonList(pago));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pagoService.procesarNotificacion("payment", 12345L);

        verify(pagoRepository).save(argThat(p -> p.getEstado() == EstadoPago.EN_MEDIACION));
    }

    @Test
    void procesarNotificacion_DebeLanzarRuntimeException_CuandoError() {
        try {
            when(paymentClient.get(12345L)).thenThrow(new RuntimeException("Error"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertThatThrownBy(() -> pagoService.procesarNotificacion("payment", 12345L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error procesando notificación de pago");
    }

    @Test
    void obtenerPago_DebeRetornarPagoResponse_CuandoExiste() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));

        PagoResponse resultado = pagoService.obtenerPago(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getOrdenId()).isEqualTo("ORD-123");
        verify(pagoRepository).findById(1L);
    }

    @Test
    void obtenerPago_DebeLanzarRuntimeException_CuandoNoExiste() {
        when(pagoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagoService.obtenerPago(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Pago no encontrado con ID: 999");
    }

    @Test
    void obtenerPagosPorOrden_DebeRetornarLista_CuandoExisten() {
        when(pagoRepository.findByOrdenId("ORD-123")).thenReturn(Collections.singletonList(pago));

        List<PagoResponse> resultado = pagoService.obtenerPagosPorOrden("ORD-123");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getOrdenId()).isEqualTo("ORD-123");
        verify(pagoRepository).findByOrdenId("ORD-123");
    }

    @Test
    void obtenerPagosPorOrden_DebeRetornarListaVacia_CuandoNoExisten() {
        when(pagoRepository.findByOrdenId("ORD-999")).thenReturn(Collections.emptyList());

        List<PagoResponse> resultado = pagoService.obtenerPagosPorOrden("ORD-999");

        assertThat(resultado).isEmpty();
        verify(pagoRepository).findByOrdenId("ORD-999");
    }
}
