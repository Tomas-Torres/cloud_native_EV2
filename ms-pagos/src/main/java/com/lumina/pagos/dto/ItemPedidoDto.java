package com.lumina.pagos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Linea de un pedido pagado (contrato del mensaje SQS).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemPedidoDto {

    /** Id del producto en el catalogo de ms-productos. */
    private Long productoId;

    /** Nombre del producto (informativo, no bloqueante). */
    private String nombreProducto;

    /** Cantidad a descontar del inventario de bodega. */
    private Integer cantidad;
}
