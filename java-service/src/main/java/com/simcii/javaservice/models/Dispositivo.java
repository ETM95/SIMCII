package com.simcii.javaservice.models;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "tipo_dispositivo")
public abstract class Dispositivo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "zona", nullable = false, length = 1)
    private String zona; // A, B o C
    
    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo; // "SENSOR" o "ACTUADOR"
    
    @Column(name = "activo")
    private boolean activo = true;

    // Constructor por defecto (OBLIGATORIO para JPA)
    public Dispositivo() {}

    // Constructor completo
    public Dispositivo(String nombre, String zona, String tipo, boolean activo) {
        this.nombre = nombre;
        this.zona = zona;
        this.tipo = tipo;
        this.activo = activo;
    }

    // Constructor sin ID (para crear nuevos)
    public Dispositivo(String nombre, String zona, String tipo) {
        this.nombre = nombre;
        this.zona = zona;
        this.tipo = tipo;
        this.activo = true; // Por defecto activo
    }

    // GETTERS Y SETTERS (TODOS deben ser public)
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getNombre() { 
        return nombre; 
    }
    
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }

    public String getZona() { 
        return zona; 
    }
    
    public void setZona(String zona) { 
        this.zona = zona; 
    }

    public String getTipo() { 
        return tipo; 
    }
    
    public void setTipo(String tipo) { 
        this.tipo = tipo; 
    }

    public boolean isActivo() { 
        return activo; 
    }
    
    public void setActivo(boolean activo) { 
        this.activo = activo; 
    }

    // Método toString() para debugging
    @Override
    public String toString() {
        return "Dispositivo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", zona='" + zona + '\'' +
                ", tipo='" + tipo + '\'' +
                ", activo=" + activo +
                '}';
    }
}