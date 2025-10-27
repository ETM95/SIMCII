package com.simcii.javaservice.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecturas")
public class Lectura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "dispositivo_id", nullable = false)
    private Long dispositivoId;
    
    @Column(nullable = false)
    private double valor;
    
    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    // Constructores
    public Lectura() {}

    public Lectura(Long dispositivoId, double valor) {
        this.dispositivoId = dispositivoId;
        this.valor = valor;
        this.fecha = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getDispositivoId() { return dispositivoId; }
    public void setDispositivoId(Long dispositivoId) { this.dispositivoId = dispositivoId; }
    
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}