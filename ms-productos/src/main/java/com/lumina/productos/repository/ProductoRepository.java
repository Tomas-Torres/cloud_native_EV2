package com.lumina.productos.repository;

import com.lumina.productos.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrue();
    List<Producto> findByCategoria(String categoria);
    List<Producto> findByMarcaId(Long marcaId);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
