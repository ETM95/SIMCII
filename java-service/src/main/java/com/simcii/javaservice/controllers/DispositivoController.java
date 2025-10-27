package com.simcii.javaservice.controllers;

import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.models.Lectura;
import com.simcii.javaservice.models.SensorHumedad;
import com.simcii.javaservice.models.SensorLuz;
import com.simcii.javaservice.models.SensorTemperatura;
import com.simcii.javaservice.models.SistemaRiego;
import com.simcii.javaservice.services.ServicioDispositivo;
import com.simcii.javaservice.services.ServicioLecturas;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import java.util.Map;

@RestController
@RequestMapping("/api/dispositivos")
public class DispositivoController {

    private final ServicioDispositivo servicioDispositivo;
    private final ServicioLecturas servicioLecturas;

    public DispositivoController(ServicioDispositivo servicioDispositivo, ServicioLecturas servicioLecturas) {
        this.servicioDispositivo = servicioDispositivo;
        this.servicioLecturas = servicioLecturas;
    }

    //  LISTAR todos los dispositivos
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        List<Dispositivo> dispositivos = servicioDispositivo.obtenerTodos();
        
        List<Map<String, Object>> response = new ArrayList<>();
        for (Dispositivo dispositivo : dispositivos) {
            Map<String, Object> dispositivoMap = new HashMap<>();
            dispositivoMap.put("id", dispositivo.getId());
            dispositivoMap.put("nombre", dispositivo.getNombre());
            dispositivoMap.put("zona", dispositivo.getZona());
            dispositivoMap.put("tipo", dispositivo.getTipo());
            dispositivoMap.put("activo", dispositivo.isActivo());
            
            String nombre = dispositivo.getNombre().toLowerCase();
            
            // ✅ Detección INFALIBLE por nombre
            if (nombre.contains("temperatura") || nombre.contains("temp")) {
                dispositivoMap.put("tipoEspecifico", "TEMPERATURA");
                dispositivoMap.put("unidad", "°C");
                dispositivoMap.put("valorActual", null);
            } else if (nombre.contains("humedad")) {
                dispositivoMap.put("tipoEspecifico", "HUMEDAD");
                dispositivoMap.put("unidad", "%");
                dispositivoMap.put("valorActual", null);
            } else if (nombre.contains("luz") || nombre.contains("iluminación")) {
                dispositivoMap.put("tipoEspecifico", "LUZ");
                dispositivoMap.put("unidad", "lux");
                dispositivoMap.put("valorActual", null);
            } else if (nombre.contains("riego") || nombre.contains("agua")) {
                dispositivoMap.put("tipoEspecifico", "SISTEMA_RIEGO");
                dispositivoMap.put("regando", false);
                dispositivoMap.put("duracionRiegoMinutos", 5);
            } else {
                dispositivoMap.put("tipoEspecifico", "GENERICO");
            }
            
            response.add(dispositivoMap);
        }
        
        return ResponseEntity.ok(response);
    }

    //  OBTENER dispositivo por ID
    @GetMapping("/{id}")
    public ResponseEntity<Dispositivo> obtener(@PathVariable Long id) {
        Optional<Dispositivo> dispositivo = servicioDispositivo.obtenerPorId(id);
        return dispositivo.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    //  CREAR nuevo dispositivo
    @PostMapping
    public ResponseEntity<Dispositivo> crear(@RequestBody Dispositivo dispositivo) {
        Dispositivo nuevoDispositivo = servicioDispositivo.crear(dispositivo);
        return ResponseEntity.ok(nuevoDispositivo);
    }

    //  ACTUALIZAR dispositivo existente
    @PutMapping("/{id}")
    public ResponseEntity<Dispositivo> actualizar(@PathVariable Long id, @RequestBody Dispositivo dispositivo) {
        Dispositivo dispositivoActualizado = servicioDispositivo.actualizar(id, dispositivo);
        if (dispositivoActualizado != null) {
            return ResponseEntity.ok(dispositivoActualizado);
        }
        return ResponseEntity.notFound().build();
    }

    //  ELIMINAR dispositivo
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        boolean eliminado = servicioDispositivo.eliminar(id);
        if (eliminado) {
            return ResponseEntity.ok("Dispositivo eliminado correctamente");
        }
        return ResponseEntity.notFound().build();
    }

    //  ACTIVAR/DESACTIVAR dispositivo
    @PostMapping("/{id}/activar")
    public ResponseEntity<String> activarDesactivar(@PathVariable Long id, @RequestParam boolean activar) {
        boolean actualizado = servicioDispositivo.activarDesactivar(id, activar);
        if (actualizado) {
            String mensaje = activar ? "Dispositivo activado" : "Dispositivo desactivado";
            return ResponseEntity.ok(mensaje);
        }
        return ResponseEntity.notFound().build();
    }

    //  HISTORIAL de lecturas
    @GetMapping("/{id}/lecturas")
    public ResponseEntity<List<Lectura>> historial(@PathVariable Long id) {
        List<Lectura> lecturas = servicioLecturas.obtenerHistorial(id);
        return ResponseEntity.ok(lecturas);
    }

    //  REGISTRAR nueva lectura (para pruebas)
    @PostMapping("/sensores/temperatura")
    public ResponseEntity<Map<String, Object>> crearSensorTemperatura(@RequestBody SensorTemperatura sensor) {
        sensor.setTipo("SENSOR");
        SensorTemperatura nuevoSensor = (SensorTemperatura) servicioDispositivo.crear(sensor);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", nuevoSensor.getId());
        response.put("nombre", nuevoSensor.getNombre());
        response.put("zona", nuevoSensor.getZona());
        response.put("tipo", nuevoSensor.getTipo());
        response.put("activo", nuevoSensor.isActivo());
        response.put("tipoEspecifico", "TEMPERATURA");
        response.put("unidad", "°C");
        response.put("valorActual", nuevoSensor.getTemperaturaActual());
        
        return ResponseEntity.ok(response);
    }

    //  CREAR SENSOR HUMEDAD ESPECÍFICO  
    @PostMapping("/sensores/humedad")
    public ResponseEntity<Map<String, Object>> crearSensorHumedad(@RequestBody SensorHumedad sensor) {
        sensor.setTipo("SENSOR");
        SensorHumedad nuevoSensor = (SensorHumedad) servicioDispositivo.crear(sensor);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", nuevoSensor.getId());
        response.put("nombre", nuevoSensor.getNombre());
        response.put("zona", nuevoSensor.getZona());
        response.put("tipo", nuevoSensor.getTipo());
        response.put("activo", nuevoSensor.isActivo());
        response.put("tipoEspecifico", "HUMEDAD");
        response.put("unidad", "%");
        response.put("valorActual", nuevoSensor.getHumedadActual());
        
        return ResponseEntity.ok(response);
    }
    // SENSOR LUZ - Respuesta específica
    @PostMapping("/sensores/luz")
    public ResponseEntity<Map<String, Object>> crearSensorLuz(@RequestBody SensorLuz sensor) {
        sensor.setTipo("SENSOR");
        SensorLuz nuevoSensor = (SensorLuz) servicioDispositivo.crear(sensor);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", nuevoSensor.getId());
        response.put("nombre", nuevoSensor.getNombre());
        response.put("zona", nuevoSensor.getZona());
        response.put("tipo", nuevoSensor.getTipo());
        response.put("activo", nuevoSensor.isActivo());
        response.put("tipoEspecifico", "LUZ");
        response.put("unidad", "lux");
        response.put("valorActual", nuevoSensor.getIntensidadLuzActual());
        
        return ResponseEntity.ok(response);
    }

    // SISTEMA RIEGO - Respuesta específica
    @PostMapping("/actuadores/riego")
    public ResponseEntity<Map<String, Object>> crearSistemaRiego(@RequestBody SistemaRiego actuador) {
        actuador.setTipo("ACTUADOR");
        SistemaRiego nuevoActuador = (SistemaRiego) servicioDispositivo.crear(actuador);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", nuevoActuador.getId());
        response.put("nombre", nuevoActuador.getNombre());
        response.put("zona", nuevoActuador.getZona());
        response.put("tipo", nuevoActuador.getTipo());
        response.put("activo", nuevoActuador.isActivo());
        response.put("tipoEspecifico", "SISTEMA_RIEGO");
        response.put("regando", nuevoActuador.isRegando());
        response.put("duracionRiegoMinutos", nuevoActuador.getDuracionRiegoMinutos());
        
        return ResponseEntity.ok(response);
    }
}