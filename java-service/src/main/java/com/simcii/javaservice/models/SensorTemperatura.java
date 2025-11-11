package com.simcii.javaservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sensores_temperatura")
public class SensorTemperatura extends Sensor {
    private Double rangoMin;
    private Double rangoMax;
    
    public SensorTemperatura() {
        this.setTipoSensor("TEMPERATURA");
        this.setUnidadMedida("°C");
        this.rangoMin = -10.0;
        this.rangoMax = 50.0;
    }
    
    // Getters y setters
    public Double getRangoMin() { return rangoMin; }
    public void setRangoMin(Double rangoMin) { this.rangoMin = rangoMin; }
    
    public Double getRangoMax() { return rangoMax; }
    public void setRangoMax(Double rangoMax) { this.rangoMax = rangoMax; }
}