package com.simcii.javaservice.models;

import jakarta.persistence.Entity;

@Entity
public abstract class Sensor extends Dispositivo {
    
    public Sensor() { 
        super();
        this.setTipo("SENSOR");  // ✅ Usar setter en lugar de acceso directo
    }
}