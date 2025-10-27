package com.simcii.javaservice.models;

import jakarta.persistence.Entity;

@Entity
public class SistemaRiego extends Actuador {
    private boolean regando = false;
    private int duracionRiegoMinutos = 5;
    
    public SistemaRiego() {
        super();
    }
    
    public SistemaRiego(String nombre, String zona) {
        super();
        this.setNombre(nombre);
        this.setZona(zona);
        this.setActivo(true);
    }

    // Getters y Setters
    public boolean isRegando() { 
        return regando; 
    }
    
    public void setRegando(boolean regando) { 
        this.regando = regando; 
    }
    
    public int getDuracionRiegoMinutos() { 
        return duracionRiegoMinutos; 
    }
    
    public void setDuracionRiegoMinutos(int duracionRiegoMinutos) { 
        this.duracionRiegoMinutos = duracionRiegoMinutos; 
    }
}