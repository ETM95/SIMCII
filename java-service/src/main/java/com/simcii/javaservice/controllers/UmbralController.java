package com.simcii.javaservice.controllers;

import com.simcii.javaservice.models.Umbral;
import com.simcii.javaservice.services.UmbralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/umbrales")
public class UmbralController {
    
    @Autowired
    private UmbralService umbralService;
    
    @PostMapping
    public ResponseEntity<Umbral> crearUmbral(@RequestBody Umbral umbral) {
        Umbral umbralGuardado = umbralService.guardarUmbral(umbral);
        return ResponseEntity.ok(umbralGuardado);
    }
    
    @GetMapping("/dispositivo/{dispositivoId}")
    public ResponseEntity<List<Umbral>> obtenerUmbralesPorDispositivo(@PathVariable Long dispositivoId) {
        // Por ahora retornamos una respuesta simple
        // En una implementación completa, buscarías el dispositivo y luego los umbrales
        return ResponseEntity.ok(List.of());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Umbral> actualizarUmbral(@PathVariable Long id, @RequestBody Umbral umbral) {
        umbral.setId(id);
        Umbral umbralActualizado = umbralService.guardarUmbral(umbral);
        return ResponseEntity.ok(umbralActualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> desactivarUmbral(@PathVariable Long id) {
        umbralService.desactivarUmbral(id);
        return ResponseEntity.ok("Umbral desactivado correctamente");
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Umbral> obtenerUmbralPorId(@PathVariable Long id) {
        Umbral umbral = umbralService.obtenerUmbralPorId(id);
        if (umbral != null) {
            return ResponseEntity.ok(umbral);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}