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
                
            System.out.println("🔍 Sensores activos encontrados: " + sensores.size());
            
            for (Dispositivo sensor : sensores) {
                Double valor = generarValorAleatorio(sensor);
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
    
    private Double generarValorAleatorio(Dispositivo dispositivo) {
        // Lógica para generar valores según el tipo de sensor
         Random random = new Random();
    
        if (dispositivo instanceof SensorTemperatura) {
            // Temperatura: 15°C a 35°C (ambiente normal)
            return 15 + (random.nextDouble() * 20);
        } 
        else if (dispositivo instanceof SensorHumedad) {
            // Humedad: 30% a 80%
            return 30 + (random.nextDouble() * 50);
        }
        else if (dispositivo instanceof SensorLuz) {
            // Luz: 0 a 1000 lux
            return random.nextDouble() * 1000;
        }
        else if (dispositivo instanceof Sensor) {
            // Sensor genérico
            return random.nextDouble() * 100;
        }
        
        return 0.0;
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