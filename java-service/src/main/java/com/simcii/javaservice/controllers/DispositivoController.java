package com.simcii.javaservice.controllers;

import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.services.DispositivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dispositivos")
@CrossOrigin(origins = "*")
public class DispositivoController {
    
    @Autowired
    private DispositivoService dispositivoService;
    
    @GetMapping
    public List<Dispositivo> getAllDispositivos() {
        return dispositivoService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Dispositivo> getDispositivoById(@PathVariable Long id) {
        Optional<Dispositivo> dispositivo = dispositivoService.findById(id);
        return dispositivo.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Dispositivo createDispositivo(@RequestBody Dispositivo dispositivo) {
        return dispositivoService.save(dispositivo);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Dispositivo> updateDispositivo(@PathVariable Long id, @RequestBody Dispositivo dispositivoDetails) {
        try {
            Dispositivo updatedDispositivo = dispositivoService.update(id, dispositivoDetails);
            return ResponseEntity.ok(updatedDispositivo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDispositivo(@PathVariable Long id) {
        dispositivoService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}