// ...existing code...
package com.simcii.javaservice.services;

import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.repositories.DispositivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DispositivoService {
    
    @Autowired
    private DispositivoRepository dispositivoRepository;
    
    public List<Dispositivo> findAll() {
        return dispositivoRepository.findAll();
    }
    
    public Optional<Dispositivo> findById(Long id) {
        return dispositivoRepository.findById(id);
    }
    
    public Dispositivo save(Dispositivo dispositivo) {
        dispositivo.setFechaCreacion(LocalDateTime.now());
        return dispositivoRepository.save(dispositivo);
    }
    
    public Dispositivo update(Long id, Dispositivo dispositivoDetails) {
        return dispositivoRepository.findById(id)
            .map(dispositivo -> {
                dispositivo.setNombre(dispositivoDetails.getNombre());
                dispositivo.setDescripcion(dispositivoDetails.getDescripcion());
                dispositivo.setUbicacion(dispositivoDetails.getUbicacion());
                dispositivo.setActivo(dispositivoDetails.getActivo());
                dispositivo.setFechaActualizacion(LocalDateTime.now());
                return dispositivoRepository.save(dispositivo);
            })
            .orElseThrow(() -> new RuntimeException("Dispositivo no encontrado"));
    }
    
    public void deleteById(Long id) {
        dispositivoRepository.deleteById(id);
    }
    
    public List<Dispositivo> findActivos() {
        return dispositivoRepository.findByActivoTrue();
    }
}
// ...existing code...