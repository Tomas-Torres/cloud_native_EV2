package com.lumina.carrito.service;

import com.lumina.carrito.entity.Carrito;
import com.lumina.carrito.entity.ItemCarrito;
import com.lumina.carrito.repository.CarritoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;

    public Carrito obtenerCarrito(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Carrito nuevo = Carrito.builder().usuarioId(usuarioId).build();
                    return carritoRepository.save(nuevo);
                });
    }

    @Transactional
    public Carrito agregarProducto(Long usuarioId, Map<String, Object> data) {
        Carrito carrito = obtenerCarrito(usuarioId);
        Long productoId = Long.valueOf(data.get("productoId").toString());

        // Verificar si ya existe el producto en el carrito
        var existente = carrito.getItems().stream()
                .filter(item -> item.getProductoId().equals(productoId))
                .findFirst();

        if (existente.isPresent()) {
            existente.get().setCantidad(existente.get().getCantidad() + 1);
        } else {
            ItemCarrito item = ItemCarrito.builder()
                    .productoId(productoId)
                    .nombreProducto((String) data.get("nombreProducto"))
                    .precioUnitario(new java.math.BigDecimal(data.get("precioUnitario").toString()))
                    .cantidad(1)
                    .imagenUrl((String) data.get("imagenUrl"))
                    .carrito(carrito)
                    .build();
            carrito.getItems().add(item);
        }

        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito eliminarProducto(Long usuarioId, Long productoId) {
        Carrito carrito = obtenerCarrito(usuarioId);
        carrito.getItems().removeIf(item -> item.getProductoId().equals(productoId));
        return carritoRepository.save(carrito);
    }

    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = obtenerCarrito(usuarioId);
        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }
}
