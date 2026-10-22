package com.lumina.carrito.controller;

import com.lumina.carrito.entity.Carrito;
import com.lumina.carrito.service.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<Carrito> obtenerCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(usuarioId));
    }

    @PostMapping("/{usuarioId}/agregar")
    public ResponseEntity<Carrito> agregarProducto(@PathVariable Long usuarioId, @RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(carritoService.agregarProducto(usuarioId, data));
    }

    @DeleteMapping("/{usuarioId}/eliminar/{productoId}")
    public ResponseEntity<Carrito> eliminarProducto(@PathVariable Long usuarioId, @PathVariable Long productoId) {
        return ResponseEntity.ok(carritoService.eliminarProducto(usuarioId, productoId));
    }

    @DeleteMapping("/{usuarioId}/vaciar")
    public ResponseEntity<Void> vaciarCarrito(@PathVariable Long usuarioId) {
        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
