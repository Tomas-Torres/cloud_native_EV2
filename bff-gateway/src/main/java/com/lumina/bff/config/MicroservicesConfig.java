package com.lumina.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class MicroservicesConfig {

    @Value("${microservices.usuarios.url}")
    private String usuariosUrl;

    @Value("${microservices.productos.url}")
    private String productosUrl;

    @Value("${microservices.bodega.url}")
    private String bodegaUrl;

    @Value("${microservices.carrito.url}")
    private String carritoUrl;

    @Value("${microservices.delivery.url}")
    private String deliveryUrl;

    @Value("${microservices.pagos.url}")
    private String pagosUrl;
}
