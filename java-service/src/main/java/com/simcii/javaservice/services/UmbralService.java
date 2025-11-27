package com.simcii.javaservice.services;

import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.models.Umbral;
import com.simcii.javaservice.models.SensorTemperatura;
import com.simcii.javaservice.models.SensorHumedad;
import com.simcii.javaservice.models.SensorLuz;
import com.simcii.javaservice.repositories.UmbralRepository;
import com.simcii.javaservice.repositories.DispositivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Random;

@Service
public class UmbralService {
    
    @Autowired
    private UmbralRepository umbralRepository;
    
    @Autowired
    private DispositivoRepository dispositivoRepository;
    
    private Random random = new Random();
    
    @PostConstruct
    public void inicializarUmbralesPorDefecto() {
        System.out.println("=== INICIALIZANDO UMBRALES POR DEFECTO ===");
        
        try {
            List<Dispositivo> sensores = dispositivoRepository.findAll().stream()
                .filter(d -> d.getClass().getSimpleName().contains("Sensor"))
                .filter(Dispositivo::getActivo)
                .toList();
            
            System.out.println("Sensores encontrados para umbrales: " + sensores.size());
            
            for (Dispositivo sensor : sensores) {
                // Verificar si ya existe umbral para este sensor
                if (umbralRepository.findByDispositivo(sensor).isEmpty()) {
                    Umbral umbral = crearUmbralPorDefecto(sensor);
                    umbralRepository.save(umbral);
                    System.out.println("Umbral creado para: " + sensor.getNombre() + " | Rango: " + 
                                     umbral.getValorMin() + " - " + umbral.getValorMax());
                }
            }
            
            System.out.println("=== INICIALIZACIÓN DE UMBRALES COMPLETADA ===");
            
        } catch (Exception e) {
            System.err.println("Error inicializando umbrales: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private Umbral crearUmbralPorDefecto(Dispositivo sensor) {
        Umbral umbral = new Umbral();
        umbral.setDispositivo(sensor);
        umbral.setActivo(true);
        
        if (sensor instanceof SensorTemperatura) {
            umbral.setValorMin(18.0);
            umbral.setValorMax(28.0);
            umbral.setTipoAlerta("TEMPERATURA_FUERA_RANGO");
        } else if (sensor instanceof SensorHumedad) {
            umbral.setValorMin(40.0);
            umbral.setValorMax(70.0);
            umbral.setTipoAlerta("HUMEDAD_FUERA_RANGO");
        } else if (sensor instanceof SensorLuz) {
            umbral.setValorMin(200.0);
            umbral.setValorMax(800.0);
            umbral.setTipoAlerta("LUZ_FUERA_RANGO");
        } else {
            // Sensor genérico
            umbral.setValorMin(0.0);
            umbral.setValorMax(100.0);
            umbral.setTipoAlerta("VALOR_FUERA_RANGO");
        }
        
        return umbral;
    }
    
    public Double generarValorSegunUmbral(Dispositivo dispositivo) {
        try {
            List<Umbral> umbrales = umbralRepository.findByDispositivo(dispositivo);
            
            if (!umbrales.isEmpty()) {
                Umbral umbral = umbrales.get(0); // Tomar el primer umbral activo
                double rango = umbral.getValorMax() - umbral.getValorMin();
                double valor = umbral.getValorMin() + (random.nextDouble() * rango);
                
                System.out.println("Valor generado desde umbral: " + valor + 
                                 " | Rango: " + umbral.getValorMin() + "-" + umbral.getValorMax() +
                                 " | Sensor: " + dispositivo.getNombre());
                
                return valor;
            }
        } catch (Exception e) {
            System.err.println("Error generando valor desde umbral: " + e.getMessage());
        }
        
        // Fallback a valores por defecto
        return generarValorPorDefecto(dispositivo);
    }
    
    private Double generarValorPorDefecto(Dispositivo dispositivo) {
        if (dispositivo instanceof SensorTemperatura) {
            return 15 + (random.nextDouble() * 20);
        } else if (dispositivo instanceof SensorHumedad) {
            return 30 + (random.nextDouble() * 50);
        } else if (dispositivo instanceof SensorLuz) {
            return random.nextDouble() * 1000;
        }
        return random.nextDouble() * 100;
    }

    // Revisar esto
    public Umbral guardarUmbral(Umbral umbral) {
        return umbralRepository.save(umbral);
    }
    
    public Umbral obtenerUmbralPorId(Long id) {
        return umbralRepository.findById(id).orElse(null);
    }
    
    public void desactivarUmbral(Long id) {
        Umbral umbral = umbralRepository.findById(id).orElse(null);
        if (umbral != null) {
            umbral.setActivo(false);
            umbralRepository.save(umbral);
        }
    }
    
    public List<Umbral> obtenerUmbralesPorDispositivo(Long dispositivoId) {
        return umbralRepository.findByDispositivoId(dispositivoId);
    }
}