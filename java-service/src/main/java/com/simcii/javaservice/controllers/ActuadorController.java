package com.simcii.javaservice.controllers;

import com.simcii.javaservice.models.Actuador;
import com.simcii.javaservice.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actuadores")
@CrossOrigin(origins = "*")
public class ActuadorController {
    
    @Autowired
    private ActuadorService actuadorService;
    
    @GetMapping
    public List<Actuador> getAllActuadores() {
        return actuadorService.findAllActuadores();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Actuador> getActuadorById(@PathVariable Long id) {
        return actuadorService.findActuadorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/activar")
    public ResponseEntity<Actuador> activarActuador(@PathVariable Long id) {
        try {
            Actuador actuador = actuadorService.activarActuador(id);
            return ResponseEntity.ok(actuador);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/desactivar")
    public ResponseEntity<Actuador> desactivarActuador(@PathVariable Long id) {
        try {
            Actuador actuador = actuadorService.desactivarActuador(id);
            return ResponseEntity.ok(actuador);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/modo")
    public ResponseEntity<Actuador> cambiarModoOperacion(@PathVariable Long id, @RequestBody String modo) {
        try {
            Actuador actuador = actuadorService.cambiarModoOperacion(id, modo);
            return ResponseEntity.ok(actuador);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}