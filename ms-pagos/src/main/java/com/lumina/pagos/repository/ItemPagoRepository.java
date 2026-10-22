package com.lumina.pagos.repository;

import com.lumina.pagos.entity.ItemPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPagoRepository extends JpaRepository<ItemPago, Long> {

    List<ItemPago> findByOrdenId(String ordenId);

    List<ItemPago> findByPagoId(Long pagoId);
}
