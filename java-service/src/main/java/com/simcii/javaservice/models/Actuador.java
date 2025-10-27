package com.simcii.javaservice.models;

import jakarta.persistence.Entity;

@Entity
public abstract class Actuador extends Dispositivo {
    
    public Actuador() { 
        super();
        this.setTipo("ACTUADOR");
    }
}