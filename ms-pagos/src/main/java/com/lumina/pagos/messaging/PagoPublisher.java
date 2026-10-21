package com.lumina.pagos.messaging;

import com.lumina.pagos.config.RabbitMQConfig;
import com.lumina.pagos.dto.PagoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publicarPagoExitoso(PagoEvent pagoEvent) {
        log.info("Publicando evento de pago exitoso: {}", pagoEvent.getPagoId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAGO_EXCHANGE,
                RabbitMQConfig.PAGO_EXITOSO_ROUTING_KEY,
                pagoEvent
        );
    }

    public void publicarPagoFallido(PagoEvent pagoEvent) {
        log.info("Publicando evento de pago fallido: {}", pagoEvent.getPagoId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAGO_EXCHANGE,
                RabbitMQConfig.PAGO_FALLIDO_ROUTING_KEY,
                pagoEvent
        );
    }
}
