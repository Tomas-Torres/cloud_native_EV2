package com.lumina.bodega.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaStockDto {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private Integer stockActual;
    private Integer stockMinimo;
    private Boolean resuelta;
    private LocalDateTime fechaCreacion;
}
