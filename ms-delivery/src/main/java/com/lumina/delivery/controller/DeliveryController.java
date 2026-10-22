package com.lumina.delivery.controller;

import com.lumina.delivery.entity.Delivery;
import com.lumina.delivery.entity.EstadoDelivery;
import com.lumina.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/{id}")
    public ResponseEntity<Delivery> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.obtenerPorId(id));
    }

    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<Delivery> obtenerPorOrden(@PathVariable String ordenId) {
        return ResponseEntity.ok(deliveryService.obtenerPorOrden(ordenId));
    }

    @PostMapping
    public ResponseEntity<Delivery> crear(@RequestBody Delivery delivery) {
        return ResponseEntity.status(201).body(deliveryService.crearDelivery(delivery));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Delivery> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        EstadoDelivery estado = EstadoDelivery.valueOf(body.get("estado").toUpperCase());
        String descripcion = body.getOrDefault("descripcion", "Estado actualizado a " + estado);
        return ResponseEntity.ok(deliveryService.actualizarEstado(id, estado, descripcion));
    }
    
    @GetMapping
    public ResponseEntity<java.util.List<Delivery>> listarTodos() {
        return ResponseEntity.ok(deliveryService.listarTodos());
    }
}
