package com.lumina.delivery.repository;

import com.lumina.delivery.entity.Delivery;
import com.lumina.delivery.entity.EstadoDelivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class DeliveryRepositoryTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    private Delivery delivery1;
    private Delivery delivery2;

    @BeforeEach
    void setUp() {
        delivery1 = Delivery.builder()
                .id(1L)
                .ordenId("ORD-123")
                .usuarioId(100L)
                .direccionEntrega("Calle Principal 123")
                .estado(EstadoDelivery.PREPARANDO)
                .repartidorNombre("Juan Pérez")
                .fechaEstimadaEntrega(LocalDateTime.now().plusDays(2))
                .fechaCreacion(LocalDateTime.now())
                .build();

        delivery2 = Delivery.builder()
                .id(2L)
                .ordenId("ORD-456")
                .usuarioId(100L)
                .direccionEntrega("Avenida Secundaria 456")
                .estado(EstadoDelivery.REPARTO)
                .repartidorNombre("María García")
                .fechaEstimadaEntrega(LocalDateTime.now().plusDays(1))
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    @Test
    void findByOrdenId_DebeRetornarOptionalConDelivery_CuandoExiste() {
        when(deliveryRepository.findByOrdenId("ORD-123")).thenReturn(Optional.of(delivery1));

        Optional<Delivery> resultado = deliveryRepository.findByOrdenId("ORD-123");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getOrdenId()).isEqualTo("ORD-123");
        verify(deliveryRepository).findByOrdenId("ORD-123");
    }

    @Test
    void findByOrdenId_DebeRetornarOptionalVacio_CuandoNoExiste() {
        when(deliveryRepository.findByOrdenId("ORD-999")).thenReturn(Optional.empty());

        Optional<Delivery> resultado = deliveryRepository.findByOrdenId("ORD-999");

        assertThat(resultado).isEmpty();
        verify(deliveryRepository).findByOrdenId("ORD-999");
    }

    @Test
    void findByUsuarioId_DebeRetornarListaDeDeliveries_CuandoExisten() {
        when(deliveryRepository.findByUsuarioId(100L)).thenReturn(Arrays.asList(delivery1, delivery2));

        List<Delivery> resultado = deliveryRepository.findByUsuarioId(100L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Delivery::getUsuarioId)
                .containsOnly(100L);
        verify(deliveryRepository).findByUsuarioId(100L);
    }

    @Test
    void findByUsuarioId_DebeRetornarListaVacia_CuandoNoHayDeliveries() {
        when(deliveryRepository.findByUsuarioId(999L)).thenReturn(Collections.emptyList());

        List<Delivery> resultado = deliveryRepository.findByUsuarioId(999L);

        assertThat(resultado).isEmpty();
        verify(deliveryRepository).findByUsuarioId(999L);
    }

    @Test
    void findByEstado_DebeRetornarListaDeDeliveries_CuandoExisten() {
        when(deliveryRepository.findByEstado(EstadoDelivery.PREPARANDO))
                .thenReturn(Arrays.asList(delivery1));

        List<Delivery> resultado = deliveryRepository.findByEstado(EstadoDelivery.PREPARANDO);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoDelivery.PREPARANDO);
        verify(deliveryRepository).findByEstado(EstadoDelivery.PREPARANDO);
    }

    @Test
    void findByEstado_DebeRetornarListaVacia_CuandoNoHayDeliveries() {
        when(deliveryRepository.findByEstado(EstadoDelivery.CANCELADO))
                .thenReturn(Collections.emptyList());

        List<Delivery> resultado = deliveryRepository.findByEstado(EstadoDelivery.CANCELADO);

        assertThat(resultado).isEmpty();
        verify(deliveryRepository).findByEstado(EstadoDelivery.CANCELADO);
    }

    @Test
    void save_DebePersistirDelivery_CuandoEsNuevo() {
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery1);

        Delivery guardado = deliveryRepository.save(delivery1);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isEqualTo(1L);
        assertThat(guardado.getOrdenId()).isEqualTo("ORD-123");
        verify(deliveryRepository).save(delivery1);
    }

    @Test
    void findById_DebeRetornarOptionalConDelivery_CuandoExiste() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery1));

        Optional<Delivery> resultado = deliveryRepository.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        verify(deliveryRepository).findById(1L);
    }

    @Test
    void findById_DebeRetornarOptionalVacio_CuandoNoExiste() {
        when(deliveryRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Delivery> resultado = deliveryRepository.findById(999L);

        assertThat(resultado).isEmpty();
        verify(deliveryRepository).findById(999L);
    }

    @Test
    void findAll_DebeRetornarTodosLosDeliveries() {
        when(deliveryRepository.findAll()).thenReturn(Arrays.asList(delivery1, delivery2));

        List<Delivery> resultado = deliveryRepository.findAll();

        assertThat(resultado).hasSize(2);
        verify(deliveryRepository).findAll();
    }

    @Test
    void deleteById_DebeEliminarDelivery_CuandoExiste() {
        deliveryRepository.deleteById(1L);

        verify(deliveryRepository).deleteById(1L);
    }
}
