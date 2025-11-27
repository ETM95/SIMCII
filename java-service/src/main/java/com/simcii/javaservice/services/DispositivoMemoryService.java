package com.simcii.javaservice.services;

import com.simcii.javaservice.models.*;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;  // Cambiado de javax.annotation

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DispositivoMemoryService {
    
    private final Map<Long, Dispositivo> dispositivos = new ConcurrentHashMap<>();
    private final Map<Long, List<Lectura>> lecturas = new ConcurrentHashMap<>();
    private final Map<Long, List<Alerta>> alertas = new ConcurrentHashMap<>();
    private final Map<Long, Umbral> umbrales = new ConcurrentHashMap<>();
    
    private Long nextId = 1L;
    
    @PostConstruct
    public void inicializarDispositivos() {
        System.out.println("🎯 INICIALIZANDO DISPOSITIVOS EN MEMORIA...");
        
        List<String> zonas = Arrays.asList("A", "B", "C");
        
        for (String zona : zonas) {
            // Sensores para la zona
            crearSensorTemperatura(zona);
            crearSensorHumedad(zona);
            crearSensorLuz(zona);
            
            // Actuadores para la zona
            crearActuadorRiego(zona);
            crearActuadorVentilacion(zona);
            crearActuadorIluminacion(zona);
        }
        
        System.out.println("✅ " + dispositivos.size() + " dispositivos inicializados en memoria");
        listarDispositivos();
    }
    
    private void crearSensorTemperatura(String zona) {
        SensorTemperatura sensor = new SensorTemperatura();
        sensor.setId(nextId++);
        sensor.setNombre("Temperatura Zona " + zona);
        sensor.setDescripcion("Sensor de temperatura para zona " + zona);
        sensor.setUbicacion("Zona " + zona);
        sensor.setActivo(true);
        sensor.setTipoSensor("TEMPERATURA");
        sensor.setUnidadMedida("°C");
        sensor.setRangoMin(-10.0);
        sensor.setRangoMax(50.0);
        
        dispositivos.put(sensor.getId(), sensor);
        lecturas.put(sensor.getId(), new ArrayList<>());
        
        // Crear umbral por defecto
        Umbral umbral = new Umbral();
        umbral.setId(nextId++);
        umbral.setDispositivo(sensor);
        umbral.setValorMin(18.0);
        umbral.setValorMax(28.0);
        umbral.setTipoAlerta("TEMPERATURA_FUERA_RANGO");
        umbral.setActivo(true);
        
        umbrales.put(sensor.getId(), umbral);
    }
    
    private void crearSensorHumedad(String zona) {
        SensorHumedad sensor = new SensorHumedad();
        sensor.setId(nextId++);
        sensor.setNombre("Humedad Zona " + zona);
        sensor.setDescripcion("Sensor de humedad para zona " + zona);
        sensor.setUbicacion("Zona " + zona);
        sensor.setActivo(true);
        sensor.setTipoSensor("HUMEDAD");
        sensor.setUnidadMedida("%");
        
        dispositivos.put(sensor.getId(), sensor);
        lecturas.put(sensor.getId(), new ArrayList<>());
        
        // Crear umbral por defecto
        Umbral umbral = new Umbral();
        umbral.setId(nextId++);
        umbral.setDispositivo(sensor);
        umbral.setValorMin(40.0);
        umbral.setValorMax(70.0);
        umbral.setTipoAlerta("HUMEDAD_FUERA_RANGO");
        umbral.setActivo(true);
        
        umbrales.put(sensor.getId(), umbral);
    }
    
    private void crearSensorLuz(String zona) {
        SensorLuz sensor = new SensorLuz();
        sensor.setId(nextId++);
        sensor.setNombre("Luz Zona " + zona);
        sensor.setDescripcion("Sensor de luz para zona " + zona);
        sensor.setUbicacion("Zona " + zona);
        sensor.setActivo(true);
        sensor.setTipoSensor("LUZ");
        sensor.setUnidadMedida("lux");
        sensor.setTipoLuz("VISIBLE");
        
        dispositivos.put(sensor.getId(), sensor);
        lecturas.put(sensor.getId(), new ArrayList<>());
        
        // Crear umbral por defecto
        Umbral umbral = new Umbral();
        umbral.setId(nextId++);
        umbral.setDispositivo(sensor);
        umbral.setValorMin(200.0);
        umbral.setValorMax(800.0);
        umbral.setTipoAlerta("LUZ_FUERA_RANGO");
        umbral.setActivo(true);
        
        umbrales.put(sensor.getId(), umbral);
    }
    
    private void crearActuadorRiego(String zona) {
        Actuador actuador = new Actuador();
        actuador.setId(nextId++);
        actuador.setNombre("Riego Zona " + zona);
        actuador.setDescripcion("Sistema de riego para zona " + zona);
        actuador.setUbicacion("Zona " + zona);
        actuador.setActivo(true);
        actuador.setTipoActuador("RIEGO");
        actuador.setModoOperacion("AUTOMATICO");
        actuador.setEstado(false);
        
        dispositivos.put(actuador.getId(), actuador);
    }
    
    private void crearActuadorVentilacion(String zona) {
        Actuador actuador = new Actuador();
        actuador.setId(nextId++);
        actuador.setNombre("Ventilación Zona " + zona);
        actuador.setDescripcion("Sistema de ventilación para zona " + zona);
        actuador.setUbicacion("Zona " + zona);
        actuador.setActivo(true);
        actuador.setTipoActuador("VENTILACION");
        actuador.setModoOperacion("AUTOMATICO");
        actuador.setEstado(false);
        
        dispositivos.put(actuador.getId(), actuador);
    }
    
    private void crearActuadorIluminacion(String zona) {
        Actuador actuador = new Actuador();
        actuador.setId(nextId++);
        actuador.setNombre("Iluminación Zona " + zona);
        actuador.setDescripcion("Sistema de iluminación para zona " + zona);
        actuador.setUbicacion("Zona " + zona);
        actuador.setActivo(true);
        actuador.setTipoActuador("ILUMINACION");
        actuador.setModoOperacion("AUTOMATICO");
        actuador.setEstado(false);
        
        dispositivos.put(actuador.getId(), actuador);
    }
    
    public List<Dispositivo> obtenerTodosDispositivos() {
        return new ArrayList<>(dispositivos.values());
    }
    
    public Optional<Dispositivo> obtenerDispositivoPorId(Long id) {
        return Optional.ofNullable(dispositivos.get(id));
    }
    
    public List<Lectura> obtenerLecturasPorDispositivo(Long dispositivoId) {
        return lecturas.getOrDefault(dispositivoId, new ArrayList<>());
    }
    
    public List<Lectura> obtenerUltimasLecturas(Long dispositivoId, int cantidad) {
        List<Lectura> lecturasDispositivo = lecturas.getOrDefault(dispositivoId, new ArrayList<>());
        return lecturasDispositivo.stream()
                .sorted((l1, l2) -> l2.getFechaHora().compareTo(l1.getFechaHora()))
                .limit(cantidad)
                .toList();
    }
    
    public void agregarLectura(Lectura lectura) {
        Long dispositivoId = lectura.getDispositivo().getId();
        lecturas.computeIfAbsent(dispositivoId, k -> new ArrayList<>()).add(lectura);
    }
    
    public List<Alerta> obtenerAlertasActivas() {
        return alertas.values().stream()
                .flatMap(List::stream)
                .filter(Alerta::getActiva)
                .toList();
    }
    
    public List<Alerta> obtenerAlertasPorDispositivo(Long dispositivoId) {
        return alertas.getOrDefault(dispositivoId, new ArrayList<>()).stream()
                .filter(Alerta::getActiva)
                .toList();
    }
    
    public void agregarAlerta(Alerta alerta) {
        Long dispositivoId = alerta.getDispositivo().getId();
        alertas.computeIfAbsent(dispositivoId, k -> new ArrayList<>()).add(alerta);
    }
    
    public Optional<Umbral> obtenerUmbralPorDispositivo(Long dispositivoId) {
        return Optional.ofNullable(umbrales.get(dispositivoId));
    }
    
    private void listarDispositivos() {
        System.out.println("\n📋 LISTA DE DISPOSITIVOS CREADOS:");
        dispositivos.values().forEach(disp -> {
            System.out.println(" - " + disp.getNombre() + " (ID: " + disp.getId() + ")");
        });
        System.out.println("🎯 Total: " + dispositivos.size() + " dispositivos\n");
    }
}