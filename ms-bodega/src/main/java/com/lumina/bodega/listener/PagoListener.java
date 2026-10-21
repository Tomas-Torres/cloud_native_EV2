package com.lumina.bodega.listener;

import com.lumina.bodega.config.RabbitMQConfig;
import com.lumina.bodega.dto.PagoEvent;
import com.lumina.bodega.service.BodegaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PagoListener {

    private final BodegaService bodegaService;

    @RabbitListener(queues = RabbitMQConfig.PAGO_BODEGA_QUEUE)
    public void manejarPagoExitoso(PagoEvent pagoEvent) {
        log.info("Recibido evento de pago: {} para pedido: {}", pagoEvent.getEstado(), pagoEvent.getPedidoId());

        try {
            if ("APROBADO".equals(pagoEvent.getEstado())) {
                bodegaService.procesarActualizacionStockDesdePago(pagoEvent);
            } else if ("RECHAZADO".equals(pagoEvent.getEstado()) || "CANCELADO".equals(pagoEvent.getEstado())) {
                bodegaService.liberarStockReservado(pagoEvent);
            }
        } catch (Exception e) {
            log.error("Error procesando evento de pago {} para pedido {}", pagoEvent.getEstado(), pagoEvent.getPedidoId(), e);
            throw e;
        }
    }
}
