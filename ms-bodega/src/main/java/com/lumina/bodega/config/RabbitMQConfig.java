package com.lumina.bodega.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange para eventos de pagos
    public static final String PAGO_EXCHANGE = "pago.exchange";

    // Queue
    public static final String PAGO_BODEGA_QUEUE = "pago.bodega.queue";

    // Routing keys
    public static final String PAGO_EXITOSO_ROUTING_KEY = "pago.exitoso";
    public static final String PAGO_FALLIDO_ROUTING_KEY = "pago.fallido";

    @Bean
    public TopicExchange pagoExchange() {
        return new TopicExchange(PAGO_EXCHANGE);
    }

    @Bean
    public Queue pagoBodegaQueue() {
        return QueueBuilder.durable(PAGO_BODEGA_QUEUE).build();
    }

    @Bean
    public Binding pagoBodegaBinding() {
        return BindingBuilder.bind(pagoBodegaQueue())
                .to(pagoExchange())
                .with(PAGO_EXITOSO_ROUTING_KEY);
    }

    @Bean
    public Binding pagoBodegaFallidoBinding() {
        return BindingBuilder.bind(pagoBodegaQueue())
                .to(pagoExchange())
                .with(PAGO_FALLIDO_ROUTING_KEY);
    }
}
