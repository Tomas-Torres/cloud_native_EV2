package com.lumina.bff.service;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Service;

@Service
public class RabbitAdminService {

    private final AmqpAdmin amqpAdmin;

    public RabbitAdminService(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    public String createQueue(String queueName) {
        if (queueName == null || queueName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la cola no puede estar vacío.");
        }
        Queue newQueue = new Queue(queueName.trim(), true);
        amqpAdmin.declareQueue(newQueue);
        return "Cola '" + queueName + "' creada exitosamente en RabbitMQ.";
    }

    public String deleteQueue(String queueName) {
        if (queueName == null || queueName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la cola no puede estar vacío.");
        }
        boolean deleted = amqpAdmin.deleteQueue(queueName.trim());
        if (deleted) {
            return "Cola '" + queueName + "' eliminada exitosamente.";
        } else {
            return "La cola '" + queueName + "' no existe o no se pudo eliminar.";
        }
    }
}