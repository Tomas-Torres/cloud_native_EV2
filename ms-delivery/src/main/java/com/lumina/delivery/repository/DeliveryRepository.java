package com.lumina.delivery.repository;

import com.lumina.delivery.entity.Delivery;
import com.lumina.delivery.entity.EstadoDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrdenId(String ordenId);
    List<Delivery> findByUsuarioId(Long usuarioId);
    List<Delivery> findByEstado(EstadoDelivery estado);
}
