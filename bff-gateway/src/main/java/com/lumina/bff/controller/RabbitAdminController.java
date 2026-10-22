package com.lumina.bff.controller;

import com.lumina.bff.service.RabbitAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/queues")
public class RabbitAdminController {

    private final RabbitAdminService rabbitAdminService;

    public RabbitAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    @PostMapping
    public ResponseEntity<String> createQueue(@RequestParam String queueName) {
        try {
            String result = rabbitAdminService.createQueue(queueName);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear la cola: " + e.getMessage());
        }
    }

    @DeleteMapping("/{queueName}")
    public ResponseEntity<String> deleteQueue(@PathVariable String queueName) {
        try {
            String result = rabbitAdminService.deleteQueue(queueName);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar la cola: " + e.getMessage());
        }
    }
}