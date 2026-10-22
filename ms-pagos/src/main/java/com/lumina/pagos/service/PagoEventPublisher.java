package com.lumina.pagos.service;

import com.lumina.pagos.dto.PagoExitosoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PagoEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PagoEventPublisher.class);

    public static final String EXCHANGE_PAGOS = "lumina.pagos.exchange";
    public static final String ROUTING_KEY_PAGO_EXITOSO = "pago.exitoso";

    private final RabbitTemplate rabbitTemplate;

    public PagoEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarPagoExitoso(PagoExitosoEvent event) {
        log.info("Publicando evento de pago exitoso para la orden ID: {}", event.getOrdenId());
        rabbitTemplate.convertAndSend(EXCHANGE_PAGOS, ROUTING_KEY_PAGO_EXITOSO, event);
    }
}