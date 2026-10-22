package com.lumina.pagos.controller;

import com.lumina.pagos.dto.CrearPreferenciaRequest;
import com.lumina.pagos.dto.PagoResponse;
import com.lumina.pagos.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Slf4j
public class PagoController {

    private final PagoService pagoService;

    @PostMapping("/crear-preferencia")
    public ResponseEntity<PagoResponse> crearPreferencia(@Valid @RequestBody CrearPreferenciaRequest request) {
        PagoResponse response = pagoService.crearPreferencia(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/notifications")
    public ResponseEntity<Void> recibirNotificacion(
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "id", required = false) Long id,
            @RequestBody(required = false) Map<String, Object> body) {

        log.info("Webhook recibido - topic: {}, id: {}, body: {}", topic, id, body);

        // MercadoPago envía notificaciones de dos formas:
        // 1. Query params: ?topic=payment&id=12345
        // 2. Body JSON: { "type": "payment", "data": { "id": "12345" } }

        if (topic != null && id != null) {
            pagoService.procesarNotificacion(topic, id);
        } else if (body != null && body.containsKey("type")) {
            String type = (String) body.get("type");
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            if (data != null && data.containsKey("id")) {
                Long resourceId = Long.valueOf(data.get("id").toString());
                pagoService.procesarNotificacion(type, resourceId);
            }
        }

        // Siempre responder 200 para que MercadoPago no reintente
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponse> obtenerPago(@PathVariable Long id) {
        PagoResponse response = pagoService.obtenerPago(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<PagoResponse>> obtenerPagosPorOrden(@PathVariable String ordenId) {
        List<PagoResponse> pagos = pagoService.obtenerPagosPorOrden(ordenId);
        return ResponseEntity.ok(pagos);
    }
}
