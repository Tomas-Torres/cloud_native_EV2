package com.lumina.pagos.listener;

import com.lumina.pagos.config.RabbitMQConfig;
import com.lumina.pagos.dto.PedidoEvent;
import com.lumina.pagos.service.PagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoListener {

    private final PagoService pagoService;

    @RabbitListener(queues = RabbitMQConfig.PEDIDO_PAGOS_QUEUE)
    public void manejarPedidoCreado(PedidoEvent pedidoEvent) {
        log.info("Recibido evento de pedido: {}", pedidoEvent.getPedidoId());

        try {
            pagoService.procesarPagoDesdeEvento(pedidoEvent);
        } catch (Exception e) {
            log.error("Error procesando pago para pedido {}", pedidoEvent.getPedidoId(), e);
            throw e;
        }
    }
}
