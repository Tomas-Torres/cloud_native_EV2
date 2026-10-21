package com.lumina.pagos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.lumina.pagos.entity.EstadoPago;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoDto {

    private String ordenId;
    private BigDecimal montoTotal;
    private String moneda;
    private EstadoPago estado;
    private String mercadopagoPaymentId;
    private LocalDateTime fechaCreacion;
    private String initPoint;

}
