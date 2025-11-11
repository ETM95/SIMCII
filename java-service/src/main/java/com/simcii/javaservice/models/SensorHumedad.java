package com.simcii.javaservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sensores_humedad")
public class SensorHumedad extends Sensor {
    public SensorHumedad() {
        this.setTipoSensor("HUMEDAD");
        this.setUnidadMedida("%");
    }
}