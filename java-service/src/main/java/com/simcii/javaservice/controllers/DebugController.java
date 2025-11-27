package com.simcii.javaservice.controllers;

import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.models.Lectura;
import com.simcii.javaservice.repositories.DispositivoRepository;
import com.simcii.javaservice.repositories.LecturaRepository;
import com.simcii.javaservice.services.LecturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    @Autowired
    private DispositivoRepository dispositivoRepository;
    
    @Autowired
    private LecturaRepository lecturaRepository;
    
    @Autowired
    private LecturaService lecturaService;
    
    @GetMapping("/test")
    public String test() {
        return "DebugController funcionando - " + java.time.LocalDateTime.now();
    }
    
    @GetMapping("/sensores")
    public List<Dispositivo> listarSensores() {
        List<Dispositivo> todos = dispositivoRepository.findAll();
        List<Dispositivo> sensores = todos.stream()
            .filter(d -> d.getClass().getSimpleName().contains("Sensor"))
            .collect(Collectors.toList());
            
        System.out.println("🔍 Sensores encontrados: " + sensores.size());
        for (Dispositivo s : sensores) {
            System.out.println(" - " + s.getNombre() + " | Tipo: " + s.getClass().getSimpleName() + " | ID: " + s.getId());
        }
        return sensores;
    }
    
    @GetMapping("/estado")
    public String estado() {
        long totalDispositivos = dispositivoRepository.count();
        List<Dispositivo> todos = dispositivoRepository.findAll();
        long sensores = todos.stream()
            .filter(d -> d.getClass().getSimpleName().contains("Sensor"))
            .count();
        long actuadores = todos.stream()
            .filter(d -> d.getClass().getSimpleName().contains("Actuador"))
            .count();
            
        return "Estado del Sistema:\n" +
               " - Total dispositivos: " + totalDispositivos + "\n" +
               " - Sensores: " + sensores + "\n" + 
               " - Actuadores: " + actuadores + "\n" +
               " - Hora: " + java.time.LocalDateTime.now();
    }
    
    @PostMapping("/forzar-lecturas")
    public String forzarLecturas() {
        try {
            System.out.println("EJECUTANDO LECTURAS MANUALMENTE...");
            lecturaService.registrarLecturasAutomaticas();
            return "Lecturas forzadas exitosamente - Revisa los logs";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    
    @GetMapping("/lecturas/{dispositivoId}")
    public List<Lectura> getLecturas(@PathVariable Long dispositivoId) {
        List<Lectura> lecturas = lecturaRepository.findByDispositivoIdOrderByFechaHoraDesc(dispositivoId);
        System.out.println("Lecturas para dispositivo " + dispositivoId + ": " + lecturas.size());
        return lecturas;
    }
    
    @GetMapping("/logs-sensores")
    public String logsSensores() {
        List<Dispositivo> todos = dispositivoRepository.findAll();
        List<Dispositivo> sensores = todos.stream()
            .filter(d -> d.getClass().getSimpleName().contains("Sensor"))
            .filter(Dispositivo::getActivo)
            .collect(Collectors.toList());
            
        StringBuilder log = new StringBuilder();
        log.append("SENSORES ACTIVOS:\n");
        for (Dispositivo sensor : sensores) {
            long countLecturas = lecturaRepository.findByDispositivoIdOrderByFechaHoraDesc(sensor.getId()).size();
            log.append(" - ").append(sensor.getNombre())
               .append(" (ID: ").append(sensor.getId())
               .append(") | Lecturas: ").append(countLecturas)
               .append(" | Tipo: ").append(sensor.getClass().getSimpleName())
               .append("\n");
        }
        return log.toString();
    }
}