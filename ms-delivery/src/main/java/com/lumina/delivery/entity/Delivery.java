package com.lumina.delivery.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "deliveries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orden_id", nullable = false)
    private String ordenId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "direccion_entrega", nullable = false)
    private String direccionEntrega;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDelivery estado;

    @Column(name = "repartidor_nombre")
    private String repartidorNombre;

    @Column(name = "fecha_estimada_entrega")
    private LocalDateTime fechaEstimadaEntrega;

    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<HistorialDelivery> historial = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (estado == null) estado = EstadoDelivery.PREPARANDO;
    }
}
