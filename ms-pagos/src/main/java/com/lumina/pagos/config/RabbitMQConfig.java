package com.lumina.pagos.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange para eventos de pedidos
    public static final String PEDIDO_EXCHANGE = "pedido.exchange";

    // Exchange para eventos de pagos
    public static final String PAGO_EXCHANGE = "pago.exchange";

    // Queues
    public static final String PEDIDO_PAGOS_QUEUE = "pedido.pagos.queue";
    public static final String PAGO_BODEGA_QUEUE = "pago.bodega.queue";

    // Routing keys
    public static final String PEDIDO_PAGOS_ROUTING_KEY = "pedido.creado.pagos";
    public static final String PAGO_EXITOSO_ROUTING_KEY = "pago.exitoso";
    public static final String PAGO_FALLIDO_ROUTING_KEY = "pago.fallido";

    @Bean
    public TopicExchange pedidoExchange() {
        return new TopicExchange(PEDIDO_EXCHANGE);
    }

    @Bean
    public TopicExchange pagoExchange() {
        return new TopicExchange(PAGO_EXCHANGE);
    }

    @Bean
    public Queue pedidoPagosQueue() {
        return QueueBuilder.durable(PEDIDO_PAGOS_QUEUE).build();
    }

    @Bean
    public Queue pagoBodegaQueue() {
        return QueueBuilder.durable(PAGO_BODEGA_QUEUE).build();
    }

    @Bean
    public Binding pedidoPagosBinding() {
        return BindingBuilder.bind(pedidoPagosQueue())
                .to(pedidoExchange())
                .with(PEDIDO_PAGOS_ROUTING_KEY);
    }

    @Bean
    public Binding pagoBodegaBinding() {
        return BindingBuilder.bind(pagoBodegaQueue())
                .to(pagoExchange())
                .with(PAGO_EXITOSO_ROUTING_KEY);
    }
}
