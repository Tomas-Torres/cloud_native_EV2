package com.lumina.bodega.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioDto {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private Integer stock;
    private Integer stockMinimo;
    private String ubicacionBodega;
    private LocalDateTime fechaActualizacion;
    private boolean stockCritico;
}
