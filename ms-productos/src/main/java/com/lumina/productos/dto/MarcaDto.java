package com.lumina.productos.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarcaDto {
    private Long id;
    private String nombre;
    private String logoUrl;
}
