package com.lumina.carrito.service;

import com.lumina.carrito.dto.PagoEvent;
import com.lumina.carrito.dto.PedidoEvent;
import com.lumina.carrito.entity.Carrito;
import com.lumina.carrito.entity.ItemCarrito;
import com.lumina.carrito.messaging.PedidoPublisher;
import com.lumina.carrito.repository.CarritoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final PedidoPublisher pedidoPublisher;

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

    @Transactional
    public void crearPedidoDesdeCarrito(Long usuarioId) {
        Carrito carrito = obtenerCarrito(usuarioId);

        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Crear evento de pedido
        String pedidoId = "PED-" + System.currentTimeMillis();
        java.math.BigDecimal total = carrito.getItems().stream()
                .map(item -> item.getPrecioUnitario().multiply(java.math.BigDecimal.valueOf(item.getCantidad())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        List<PedidoEvent.ItemPedidoEvent> items = carrito.getItems().stream()
                .map(item -> new PedidoEvent.ItemPedidoEvent(
                        item.getProductoId(),
                        item.getNombreProducto(),
                        item.getCantidad(),
                        item.getPrecioUnitario()
                ))
                .collect(Collectors.toList());

        PedidoEvent pedidoEvent = new PedidoEvent(
                pedidoId,
                String.valueOf(usuarioId),
                total,
                "PENDIENTE",
                java.time.LocalDateTime.now(),
                items
        );

        // Publicar evento a RabbitMQ
        pedidoPublisher.publicarPedidoCreado(pedidoEvent);

        // Vaciar carrito después de crear el pedido
        carrito.getItems().clear();
        carritoRepository.save(carrito);

        log.info("Pedido creado y evento publicado: {}", pedidoId);
    }

    @Transactional
    public void actualizarEstadoPedidoDesdePago(PagoEvent pagoEvent) {
        log.info("Actualizando estado del pedido {} basado en pago {}", pagoEvent.getPedidoId(), pagoEvent.getEstado());

        // Nota: En una implementación completa, aquí se debería:
        // 1. Buscar el pedido en la base de datos
        // 2. Actualizar su estado según el estado del pago
        // 3. Si el pago es exitoso, marcar el pedido como PAGADO
        // 4. Si el pago falló, marcar el pedido como CANCELADO

        // Por ahora, solo logueamos el evento
        log.info("Estado del pedido {} actualizado a: {}", pagoEvent.getPedidoId(), pagoEvent.getEstado());
    }
}
