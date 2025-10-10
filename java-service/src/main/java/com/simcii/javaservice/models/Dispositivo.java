package com.simcii.javaservice.models;

public abstract class Dispositivo {
    protected Long id;
    protected String nombre;
    protected String zona; // A, B o C
    protected String tipo; // "SENSOR" o "ACTUADOR"
    protected boolean activo;

    public Dispositivo() {}

    public Dispositivo(Long id, String nombre, String zona, String tipo, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.zona = zona;
        this.tipo = tipo;
        this.activo = activo;
    }

    // getters y setters (omito por brevedad - implementar todos)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}

