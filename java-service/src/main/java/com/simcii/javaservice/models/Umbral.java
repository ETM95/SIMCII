package com.simcii.javaservice.models;

public class Umbral {
    private Long id;
    private Long dispositivoId;
    private Double minimo;
    private Double maximo;

    public Umbral() {}

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDispositivoId() { return dispositivoId; }
    public void setDispositivoId(Long dispositivoId) { this.dispositivoId = dispositivoId; }
    public Double getMinimo() { return minimo; }
    public void setMinimo(Double minimo) { this.minimo = minimo; }
    public Double getMaximo() { return maximo; }
    public void setMaximo(Double maximo) { this.maximo = maximo; }
}
