package com.lumina.carrito.listener;

import com.lumina.carrito.config.RabbitMQConfig;
import com.lumina.carrito.dto.PagoEvent;
import com.lumina.carrito.service.CarritoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PagoListener {

    private final CarritoService carritoService;

    @RabbitListener(queues = RabbitMQConfig.PAGOS_RESULTADO_QUEUE)
    public void manejarResultadoPago(PagoEvent pagoEvent) {
        log.info("Recibido evento de pago: {} para pedido: {}", pagoEvent.getEstado(), pagoEvent.getPedidoId());

        try {
            carritoService.actualizarEstadoPedidoDesdePago(pagoEvent);
        } catch (Exception e) {
            log.error("Error actualizando estado del pedido para pago {}", pagoEvent.getPagoId(), e);
            throw e;
        }
    }
}
