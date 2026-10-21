package com.lumina.carrito.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarritoDto {
    private Long id;
    private Long usuarioId;
    private List<ItemCarritoDto> items;
    private BigDecimal total;
}
