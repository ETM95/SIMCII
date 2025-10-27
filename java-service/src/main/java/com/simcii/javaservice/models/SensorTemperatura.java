package com.simcii.javaservice.models;

import jakarta.persistence.Entity;
// import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SENSOR_TEMPERATURA")
public class SensorTemperatura extends Sensor {
    private String unidad = "°C";
    private Double temperaturaActual;
    
    public SensorTemperatura() {
        super();
    }
    
    public SensorTemperatura(String nombre, String zona) {
        super();
        this.setNombre(nombre);
        this.setZona(zona);
        this.setActivo(true);
    }

    // Getters y Setters
    public String getUnidad() { 
        return unidad; 
    }
    
    public void setUnidad(String unidad) { 
        this.unidad = unidad; 
    }
    
    public Double getTemperaturaActual() { 
        return temperaturaActual; 
    }
    
    public void setTemperaturaActual(Double temperaturaActual) { 
        this.temperaturaActual = temperaturaActual; 
    }
}