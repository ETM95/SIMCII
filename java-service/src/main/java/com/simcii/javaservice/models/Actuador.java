package com.simcii.javaservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "actuadores")
public class Actuador extends Dispositivo {
    private String tipoActuador;
    private Boolean estado;
    private String modoOperacion;
    
    // Constructores, getters y setters

    public Actuador() {
        super();
        this.estado = false;
    }
    
    public String getTipoActuador() {
        return tipoActuador;
    }
    
    public void setTipoActuador(String tipoActuador) {
        this.tipoActuador = tipoActuador;
    }
    
    public Boolean getEstado() {
        return estado;
    }
    
    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
    
    public String getModoOperacion() {
        return modoOperacion;
    }
    
    public void setModoOperacion(String modoOperacion) {
        this.modoOperacion = modoOperacion;
    }
}