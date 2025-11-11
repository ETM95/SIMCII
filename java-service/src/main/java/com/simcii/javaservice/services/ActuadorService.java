package com.simcii.javaservice.services;

import com.simcii.javaservice.models.Actuador;
import com.simcii.javaservice.repositories.DispositivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActuadorService {
    
    @Autowired
    private DispositivoRepository dispositivoRepository;
    
    public List<Actuador> findAllActuadores() {
        return dispositivoRepository.findAllActuadores().stream()
                .map(dispositivo -> (Actuador) dispositivo)
                .toList();
    }
    
    public Optional<Actuador> findActuadorById(Long id) {
        return dispositivoRepository.findById(id)
                .filter(dispositivo -> dispositivo instanceof Actuador)
                .map(dispositivo -> (Actuador) dispositivo);
    }
    
    public Actuador activarActuador(Long id) {
        return cambiarEstadoActuador(id, true);
    }
    
    public Actuador desactivarActuador(Long id) {
        return cambiarEstadoActuador(id, false);
    }
    
    private Actuador cambiarEstadoActuador(Long id, boolean estado) {
        return findActuadorById(id)
                .map(actuador -> {
                    actuador.setEstado(estado);
                    return (Actuador) dispositivoRepository.save(actuador);
                })
                .orElseThrow(() -> new RuntimeException("Actuador no encontrado"));
    }
    
    public Actuador cambiarModoOperacion(Long id, String modo) {
        return findActuadorById(id)
                .map(actuador -> {
                    actuador.setModoOperacion(modo);
                    return (Actuador) dispositivoRepository.save(actuador);
                })
                .orElseThrow(() -> new RuntimeException("Actuador no encontrado"));
    }
}