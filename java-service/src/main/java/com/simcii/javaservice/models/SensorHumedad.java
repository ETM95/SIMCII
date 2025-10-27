package com.simcii.javaservice.models;

import jakarta.persistence.Entity;
// import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SENSOR_HUMEDAD")
public class SensorHumedad extends Sensor {
    private String unidad = "%";
    private Double humedadActual;
    
    public SensorHumedad() {
        super();
    }
    
    public SensorHumedad(String nombre, String zona) {
        super();
        this.setNombre(nombre);
        this.setZona(zona);
        this.setActivo(true);
    }

    // Getters y Setters CORREGIDOS
    public String getUnidad() { 
        return unidad; 
    }
    
    public void setUnidad(String unidad) { 
        this.unidad = unidad; 
    }
    
    public Double getHumedadActual() { 
        return humedadActual; 
    }
    
    public void setHumedadActual(Double humedadActual) { 
        this.humedadActual = humedadActual; 
    }
}