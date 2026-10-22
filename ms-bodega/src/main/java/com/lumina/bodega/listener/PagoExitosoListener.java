package com.lumina.bodega.listener;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class PagoExitosoListener {

    private static final Logger log = LoggerFactory.getLogger(PagoExitosoListener.class);
    public static final String QUEUE_BODEGA = "lumina.bodega.queue";

    @RabbitListener(queues = QUEUE_BODEGA)
    public void procesarReservaBodega(Object payload, Channel channel, Message message) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String body = new String(message.getBody(), StandardCharsets.UTF_8);

        try {
            log.info("MS-BODEGA: Recibido evento de pago -> {}", body);

            // TODO: Aquí irá la lógica real de actualización de inventario en BD
            
            channel.basicAck(deliveryTag, false);
            log.info("MS-BODEGA: Inventario procesado con éxito (ACK).");

        } catch (Exception e) {
            log.error("MS-BODEGA: Error al procesar reserva ({}) - Redirigiendo a DLQ mediante NACK.", e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}