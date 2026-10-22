package com.lumina.pagos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Items del pedido associated a un pago.
 *
 * <p>Se persisten para poder reconstruir el evento "pedido pagado" que se publica en la cola
 * SQS cuando MercadoPago notifica que el pago fue aprobado (el webhook solo conoce el
 * {@code external_reference} = ordenId).</p>
 */
@Entity
@Table(name = "items_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Id del pago al que pertenece el item. */
    @Column(name = "pago_id", nullable = false)
    private Long pagoId;

    /** Orden a la que pertenece el item (clave de negocio). */
    @Column(name = "orden_id", nullable = false)
    private String ordenId;

    /** Id del producto en el catalogo de ms-productos. */
    @Column(name = "producto_id")
    private Long productoId;

    /** Nombre del producto. */
    @Column(name = "nombre_producto")
    private String nombreProducto;

    /** Cantidad comprada. */
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
}
