package com.simcii.javaservice.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecturas")
public class Lectura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "dispositivo_id")
    private Dispositivo dispositivo;
    
    private Double valor;
    private LocalDateTime fechaHora;
    private String unidad;
    
    // Constructores
    public Lectura() {
        this.fechaHora = LocalDateTime.now();
    }
    
    public Lectura(Dispositivo dispositivo, Double valor, String unidad) {
        this.dispositivo = dispositivo;
        this.valor = valor;
        this.unidad = unidad;
        this.fechaHora = LocalDateTime.now();
    }
    
    public Lectura(Dispositivo dispositivo, Double valor, LocalDateTime fechaHora, String unidad) {
        this.dispositivo = dispositivo;
        this.valor = valor;
        this.fechaHora = fechaHora;
        this.unidad = unidad;
    }
    
    // Getters y setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Dispositivo getDispositivo() {
        return dispositivo;
    }
    
    public void setDispositivo(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }
    
    public Double getValor() {
        return valor;
    }
    
    public void setValor(Double valor) {
        this.valor = valor;
    }
    
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
    
    public String getUnidad() {
        return unidad;
    }
    
    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }
}