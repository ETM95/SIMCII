// ...existing code...
package com.simcii.javaservice.services;

import com.simcii.javaservice.models.Lectura;
import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.models.Sensor;
import com.simcii.javaservice.models.SensorHumedad;
import com.simcii.javaservice.models.SensorLuz;
import com.simcii.javaservice.models.SensorTemperatura;
import com.simcii.javaservice.repositories.LecturaRepository;
import com.simcii.javaservice.repositories.DispositivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class LecturaService {
    
    @Autowired
    private LecturaRepository lecturaRepository;
    
    @Autowired
    private DispositivoRepository dispositivoRepository;
    
    @Autowired
    private AlertaService alertaService;

    @Autowired
    private UmbralService umbralService;
    
    private Random random = new Random();
    
    @Scheduled(fixedRate = 10000)
public void registrarLecturasAutomaticas() {
    System.out.println("=== INICIANDO LECTURAS AUTOMÁTICAS ===");
    
    try {
        List<Dispositivo> todos = dispositivoRepository.findAll();
        List<Dispositivo> sensores = todos.stream()
            .filter(d -> d.getClass().getSimpleName().contains("Sensor"))
            .filter(Dispositivo::getActivo)
            .collect(Collectors.toList());
            
        System.out.println("Sensores activos encontrados: " + sensores.size());
        
        for (Dispositivo sensor : sensores) {
            // CAMBIO AQUÍ: Usar UmbralService en lugar de generarValorAleatorio
            Double valor = umbralService.generarValorSegunUmbral(sensor);
            System.out.println("Generando lectura: " + valor + " para " + sensor.getNombre() + " (ID: " + sensor.getId() + ")");
            
            Lectura lectura = new Lectura();
            lectura.setDispositivo(sensor);
            lectura.setValor(valor);
            lectura.setUnidad(obtenerUnidad(sensor));
            
            Lectura saved = lecturaRepository.save(lectura);
            System.out.println("Lectura guardada ID: " + saved.getId());
        }
        
        System.out.println("=== LECTURAS COMPLETADAS ===");
        
    } catch (Exception e) {
        System.err.println("ERROR en lecturas automáticas: " + e.getMessage());
        e.printStackTrace();
    }
}


    
    private String obtenerUnidad(Dispositivo dispositivo) {
        if (dispositivo instanceof Sensor) {
            return ((Sensor) dispositivo).getUnidadMedida();
        }
        return "unidad";
    }
    
    public List<Lectura> obtenerHistorialPorDispositivo(Long dispositivoId) {
        return lecturaRepository.findByDispositivoIdOrderByFechaHoraDesc(dispositivoId);
    }
    
    public List<Lectura> obtenerUltimasLecturas(Long dispositivoId, int cantidad) {
        return lecturaRepository.findUltimasLecturas(dispositivoId, cantidad);
    }
}
// ...existing code...