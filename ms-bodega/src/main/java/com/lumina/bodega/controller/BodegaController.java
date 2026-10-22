package com.lumina.bodega.controller;

import com.lumina.bodega.entity.AlertaStock;
import com.lumina.bodega.entity.Inventario;
import com.lumina.bodega.service.BodegaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bodega")
@RequiredArgsConstructor
public class BodegaController {

    private final BodegaService bodegaService;

    @GetMapping("/inventario")
    public ResponseEntity<List<Inventario>> listarInventario() {
        return ResponseEntity.ok(bodegaService.listarInventario());
    }

    @GetMapping("/inventario/{productoId}")
    public ResponseEntity<Inventario> obtenerStock(@PathVariable Long productoId) {
        return ResponseEntity.ok(bodegaService.obtenerStock(productoId));
    }

    @PostMapping("/inventario")
    public ResponseEntity<Inventario> crearInventario(@RequestBody Inventario inventario) {
        return ResponseEntity.status(201).body(bodegaService.crearInventario(inventario));
    }

    @PatchMapping("/inventario/{productoId}/agregar")
    public ResponseEntity<Inventario> agregarStock(@PathVariable Long productoId, @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(bodegaService.actualizarStock(productoId, body.get("cantidad")));
    }

    @PatchMapping("/inventario/{productoId}/descontar")
    public ResponseEntity<Inventario> descontarStock(@PathVariable Long productoId, @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(bodegaService.descontarStock(productoId, body.get("cantidad")));
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<AlertaStock>> obtenerAlertas() {
        return ResponseEntity.ok(bodegaService.obtenerAlertas());
    }
}
