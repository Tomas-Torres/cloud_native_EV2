package com.lumina.pagos.controller;

import com.lumina.pagos.dto.PagoExitosoEvent;
import com.lumina.pagos.service.PagoEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/pagos/test")
public class PagoTestController {

    private final PagoEventPublisher publisher;

    public PagoTestController(PagoEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping("/simular-pago")
    public ResponseEntity<String> simularPago(@RequestParam String ordenId, @RequestParam String email, @RequestParam BigDecimal monto) {
        PagoExitosoEvent event = new PagoExitosoEvent(ordenId, email, monto, LocalDateTime.now());
        publisher.publicarPagoExitoso(event);
        return ResponseEntity.ok("Evento de pago exitoso enviado a RabbitMQ para la orden " + ordenId);
    }
}