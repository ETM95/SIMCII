package com.simcii.javaservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sensores")
public class Sensor extends Dispositivo {
    private String tipoSensor;
    private String unidadMedida;
    
    // Constructores, getters y setters

    public Sensor() {
        super();
    }
    
    public String getTipoSensor() {
        return tipoSensor;
    }
    
    public void setTipoSensor(String tipoSensor) {
        this.tipoSensor = tipoSensor;
    }
    
    public String getUnidadMedida() {
        return unidadMedida;
    }
    
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
}