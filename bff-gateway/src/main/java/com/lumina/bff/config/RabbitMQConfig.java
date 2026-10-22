package com.lumina.bff.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    // Nombres centralizados de Colas y Exchanges
    public static final String EXCHANGE_PAGOS = "lumina.pagos.exchange";
    public static final String QUEUE_BODEGA = "lumina.bodega.queue";
    public static final String QUEUE_DELIVERY = "lumina.delivery.queue";
    public static final String ROUTING_KEY_PAGO_EXITOSO = "pago.exitoso";

    // Dead Letter Queue (DLQ)
    public static final String DLQ_EXCHANGE = "lumina.dlq.exchange";
    public static final String DLQ_QUEUE = "lumina.dlq.queue";
    public static final String DLQ_ROUTING_KEY = "dlq.deadletter";

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    // Fuerza la conexión e inicialización inmediata en RabbitMQ al arrancar
    @Bean
    public CommandLineRunner initRabbitMQ(RabbitAdmin rabbitAdmin) {
        return args -> {
            log.info("Iniciando conexión e inicializando colas/exchanges en RabbitMQ...");
            rabbitAdmin.initialize();
            log.info("Colas e intercambios declarados correctamente en RabbitMQ.");
        };
    }

    // 1. Configuración de DLQ
    @Bean
    public DirectExchange dlqExchange() {
        return new DirectExchange(DLQ_EXCHANGE);
    }

    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlqQueue()).to(dlqExchange()).with(DLQ_ROUTING_KEY);
    }

    // 2. Exchanges y Colas Principales
    @Bean
    public DirectExchange pagosExchange() {
        return new DirectExchange(EXCHANGE_PAGOS);
    }

    @Bean
    public Queue bodegaQueue() {
        return QueueBuilder.durable(QUEUE_BODEGA)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue deliveryQueue() {
        return QueueBuilder.durable(QUEUE_DELIVERY)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding bodegaBinding() {
        return BindingBuilder.bind(bodegaQueue()).to(pagosExchange()).with(ROUTING_KEY_PAGO_EXITOSO);
    }

    @Bean
    public Binding deliveryBinding() {
        return BindingBuilder.bind(deliveryQueue()).to(pagosExchange()).with(ROUTING_KEY_PAGO_EXITOSO);
    }
}