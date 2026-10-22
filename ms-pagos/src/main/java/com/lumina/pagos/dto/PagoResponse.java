package com.lumina.pagos.dto;

import com.lumina.pagos.entity.EstadoPago;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponse {

    private Long id;
    private String ordenId;
    private Long usuarioId;
    private BigDecimal montoTotal;
    private String moneda;
    private EstadoPago estado;
    private String mercadopagoPreferenceId;
    private String mercadopagoPaymentId;
    private String mercadopagoStatus;
    private String metodoPago;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String initPoint;
    private String sandboxInitPoint;
}
