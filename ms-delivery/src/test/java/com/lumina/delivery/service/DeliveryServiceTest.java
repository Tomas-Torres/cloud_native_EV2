package com.lumina.delivery.service;

import com.lumina.delivery.entity.Delivery;
import com.lumina.delivery.entity.EstadoDelivery;
import com.lumina.delivery.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryService deliveryService;

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
                .build();
    }

    @Test
    void obtenerPorId_DebeRetornarDelivery_CuandoExiste() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

        Delivery resultado = deliveryService.obtenerPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getOrdenId()).isEqualTo("ORD-123");
        verify(deliveryRepository).findById(1L);
    }

    @Test
    void obtenerPorOrden_DebeRetornarDelivery_CuandoExiste() {
        when(deliveryRepository.findByOrdenId("ORD-123")).thenReturn(Optional.of(delivery));

        Delivery resultado = deliveryService.obtenerPorOrden("ORD-123");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getOrdenId()).isEqualTo("ORD-123");
        assertThat(resultado.getUsuarioId()).isEqualTo(100L);
        verify(deliveryRepository).findByOrdenId("ORD-123");
    }

    @Test
    void crearDelivery_DebeEstablecerEstadoPreparando_CuandoEsNuevo() {
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Delivery resultado = deliveryService.crearDelivery(delivery);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo(EstadoDelivery.PREPARANDO);
        assertThat(resultado.getHistorial()).isNotEmpty();
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void crearDelivery_DebeAgregarHistorialInicial_CuandoEsNuevo() {
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Delivery resultado = deliveryService.crearDelivery(delivery);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getHistorial()).hasSize(1);
        assertThat(resultado.getHistorial().get(0).getEstado()).isEqualTo(EstadoDelivery.PREPARANDO);
        assertThat(resultado.getHistorial().get(0).getDescripcion()).isEqualTo("Pedido recibido y en preparación");
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void actualizarEstado_DebeActualizarEstado_CuandoExiste() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Delivery resultado = deliveryService.actualizarEstado(1L, EstadoDelivery.REPARTO, "En camino al destino");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo(EstadoDelivery.REPARTO);
        verify(deliveryRepository).findById(1L);
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void actualizarEstado_DebeEstablecerFechaEntregaReal_CuandoEstadoEsFinalizado() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Delivery resultado = deliveryService.actualizarEstado(1L, EstadoDelivery.FINALIZADO, "Entregado exitosamente");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo(EstadoDelivery.FINALIZADO);
        assertThat(resultado.getFechaEntregaReal()).isNotNull();
        verify(deliveryRepository).findById(1L);
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void actualizarEstado_DebeAgregarHistorial_CuandoSeActualiza() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Delivery resultado = deliveryService.actualizarEstado(1L, EstadoDelivery.RECOLECCION, "Producto recogido");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getHistorial()).isNotEmpty();
        verify(deliveryRepository).findById(1L);
        verify(deliveryRepository).save(any(Delivery.class));
    }
}
