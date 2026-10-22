package com.lumina.pagos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Evento "pedido pagado" publicado en la cola SQS "curso-pedidos".
 *
 * Es el contrato entre ms-pagos (productor) y ms-bodega (consumidor). Se mantiene
 * {@code ignoreUnknown = true} para que ms-bodega pueda agregar campos sin romper a los
 * productores ya desplegados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PedidoPagadoDto {

    /** Identificador de la orden (clave de negocio del pedido). */
    private String ordenId;

    /** Usuario que realizo la compra. */
    private Long usuarioId;

    /** Items del pedido a descontar del inventario. */
    private List<ItemPedidoDto> items;

    /** Monto total pagado. */
    private BigDecimal montoTotal;

    /** Moneda del pago (CLP). */
    private String moneda;

    /** Momento en que se aprobo el pago. */
    private LocalDateTime fechaPago;
}
