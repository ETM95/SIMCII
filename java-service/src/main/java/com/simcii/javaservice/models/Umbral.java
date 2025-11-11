package com.simcii.javaservice.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "umbrales")
public class Umbral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "dispositivo_id")
    private Dispositivo dispositivo;
    
    private Double valorMin;
    private Double valorMax;
    private String tipoAlerta;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    
    // Constructores
    public Umbral() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }
    
    public Umbral(Dispositivo dispositivo, Double valorMin, Double valorMax, String tipoAlerta) {
        this.dispositivo = dispositivo;
        this.valorMin = valorMin;
        this.valorMax = valorMax;
        this.tipoAlerta = tipoAlerta;
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
    }
    
    public Umbral(Dispositivo dispositivo, Double valorMin, Double valorMax, String tipoAlerta, Boolean activo) {
        this.dispositivo = dispositivo;
        this.valorMin = valorMin;
        this.valorMax = valorMax;
        this.tipoAlerta = tipoAlerta;
        this.activo = activo;
        this.fechaCreacion = LocalDateTime.now();
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
    
    public Double getValorMin() {
        return valorMin;
    }
    
    public void setValorMin(Double valorMin) {
        this.valorMin = valorMin;
    }
    
    public Double getValorMax() {
        return valorMax;
    }
    
    public void setValorMax(Double valorMax) {
        this.valorMax = valorMax;
    }
    
    public String getTipoAlerta() {
        return tipoAlerta;
    }
    
    public void setTipoAlerta(String tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}