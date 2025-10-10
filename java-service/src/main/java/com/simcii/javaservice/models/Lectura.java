package com.simcii.javaservice.models;

import java.time.LocalDateTime;

public class Lectura {
    private Long id;
    private Long dispositivoId;
    private double valor;
    private LocalDateTime fecha;

    public Lectura() {}

    public Lectura(Long id, Long dispositivoId, double valor, LocalDateTime fecha) {
        this.id = id;
        this.dispositivoId = dispositivoId;
        this.valor = valor;
        this.fecha = fecha;
    }

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDispositivoId() { return dispositivoId; }
    public void setDispositivoId(Long dispositivoId) { this.dispositivoId = dispositivoId; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}

