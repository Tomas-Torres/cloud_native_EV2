package com.lumina.carrito.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "lumina.exchange";
    public static final String QUEUE_CARRITO = "carrito.producto.agregado.queue";
    public static final String ROUTING_KEY_CARRITO = "carrito.producto.agregado";

    @Bean
    public Queue carritoQueue() {
        return QueueBuilder.durable(QUEUE_CARRITO).build();
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding bindingCarrito(Queue carritoQueue, TopicExchange exchange) {
        return BindingBuilder.bind(carritoQueue).to(exchange).with(ROUTING_KEY_CARRITO);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}