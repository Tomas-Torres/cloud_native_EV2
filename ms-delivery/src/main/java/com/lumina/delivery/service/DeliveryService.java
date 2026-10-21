package com.lumina.delivery.service;

import com.lumina.delivery.entity.Delivery;
import com.lumina.delivery.entity.EstadoDelivery;
import com.lumina.delivery.entity.HistorialDelivery;
import com.lumina.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public Delivery obtenerPorId(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery no encontrado con ID: " + id));
    }

    public Delivery obtenerPorOrden(String ordenId) {
        return deliveryRepository.findByOrdenId(ordenId)
                .orElseThrow(() -> new RuntimeException("Delivery no encontrado para orden: " + ordenId));
    }

    @Transactional
    public Delivery crearDelivery(Delivery delivery) {
        delivery.setEstado(EstadoDelivery.PREPARANDO);

        HistorialDelivery historial = HistorialDelivery.builder()
                .estado(EstadoDelivery.PREPARANDO)
                .descripcion("Pedido recibido y en preparación")
                .delivery(delivery)
                .build();
        delivery.getHistorial().add(historial);

        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery actualizarEstado(Long id, EstadoDelivery nuevoEstado, String descripcion) {
        Delivery delivery = obtenerPorId(id);
        delivery.setEstado(nuevoEstado);

        if (nuevoEstado == EstadoDelivery.FINALIZADO) {
            delivery.setFechaEntregaReal(java.time.LocalDateTime.now());
        }

        HistorialDelivery historial = HistorialDelivery.builder()
                .estado(nuevoEstado)
                .descripcion(descripcion)
                .delivery(delivery)
                .build();
        delivery.getHistorial().add(historial);

        log.info("Delivery {} actualizado a estado: {}", id, nuevoEstado);
        return deliveryRepository.save(delivery);
    }
    public java.util.List<Delivery> listarTodos() {
        return deliveryRepository.findAll();
    }
}
