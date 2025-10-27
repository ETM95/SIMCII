package com.simcii.javaservice.models;

import jakarta.persistence.Entity;
// import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SENSOR_LUZ")
public class SensorLuz extends Sensor {
    private String unidad = "lux";
    private Double intensidadLuzActual;
    
    public SensorLuz() {
        super();
    }
    
    public SensorLuz(String nombre, String zona) {
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
    
    public Double getIntensidadLuzActual() { 
        return intensidadLuzActual; 
    }
    
    public void setIntensidadLuzActual(Double intensidadLuzActual) { 
        this.intensidadLuzActual = intensidadLuzActual; 
    }
}