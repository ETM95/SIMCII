package com.simcii.javaservice.models;

import java.time.LocalDateTime;

public class Alerta {
    private Long id;
    private Dispositivo dispositivo;
    private Umbral umbral;
    private Double valorActual;
    private String mensaje;
    private Boolean activa;
    private LocalDateTime fechaCreacion;
    
    // Constructores
    public Alerta() {
        this.fechaCreacion = LocalDateTime.now();
        this.activa = true;
    }
    
    public Alerta(Dispositivo dispositivo, Umbral umbral, Double valorActual, String mensaje) {
        this();
        this.dispositivo = dispositivo;
        this.umbral = umbral;
        this.valorActual = valorActual;
        this.mensaje = mensaje;
    }
    
    // Getters y Setters
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
    
    public Umbral getUmbral() {
        return umbral;
    }
    
    public void setUmbral(Umbral umbral) {
        this.umbral = umbral;
    }
    
    public Double getValorActual() {
        return valorActual;
    }
    
    public void setValorActual(Double valorActual) {
        this.valorActual = valorActual;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public Boolean getActiva() {
        return activa;
    }
    
    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}