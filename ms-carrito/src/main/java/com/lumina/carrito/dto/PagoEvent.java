package com.lumina.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoEvent {
    private String pagoId;
    private String pedidoId;
    private String usuarioId;
    private BigDecimal monto;
    private String estado;
    private String metodoPago;
    private LocalDateTime fecha;
    private String motivoRechazo;
}
