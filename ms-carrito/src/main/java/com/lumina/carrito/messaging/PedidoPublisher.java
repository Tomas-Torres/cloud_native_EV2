package com.lumina.carrito.messaging;

import com.lumina.carrito.config.RabbitMQConfig;
import com.lumina.carrito.dto.PedidoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publicarPedidoCreado(PedidoEvent pedidoEvent) {
        log.info("Publicando evento de pedido creado: {}", pedidoEvent.getPedidoId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PEDIDO_EXCHANGE,
                RabbitMQConfig.PEDIDO_PAGOS_ROUTING_KEY,
                pedidoEvent
        );
    }
}
