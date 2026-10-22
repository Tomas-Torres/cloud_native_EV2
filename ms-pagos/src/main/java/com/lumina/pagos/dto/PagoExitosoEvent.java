package com.lumina.pagos.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoExitosoEvent implements Serializable {

    private String ordenId;
    private String usuarioEmail;
    private BigDecimal monto;
    private LocalDateTime fechaPago;

    public PagoExitosoEvent() {}

    public PagoExitosoEvent(String ordenId, String usuarioEmail, BigDecimal monto, LocalDateTime fechaPago) {
        this.ordenId = ordenId;
        this.usuarioEmail = usuarioEmail;
        this.monto = monto;
        this.fechaPago = fechaPago;
    }

    public String getOrdenId() { return ordenId; }
    public void setOrdenId(String ordenId) { this.ordenId = ordenId; }

    public String getUsuarioEmail() { return usuarioEmail; }
    public void setUsuarioEmail(String usuarioEmail) { this.usuarioEmail = usuarioEmail; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
}