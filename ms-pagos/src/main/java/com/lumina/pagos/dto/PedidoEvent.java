package com.lumina.pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoEvent {
    private String pedidoId;
    private String usuarioId;
    private BigDecimal total;
    private String estado;
    private LocalDateTime fecha;
    private List<ItemPedidoEvent> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemPedidoEvent {
        private Long productoId;
        private String nombre;
        private Integer cantidad;
        private BigDecimal precioUnitario;
    }
}
