package com.simcii.javaservice.services;

import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.repositories.RepositorioDispositivos;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioDispositivo {
    
    private final RepositorioDispositivos repositorioDispositivos;
    
    public ServicioDispositivo(RepositorioDispositivos repositorioDispositivos) {
        this.repositorioDispositivos = repositorioDispositivos;
    }
    
    //  Obtener todos los dispositivos
    public List<Dispositivo> obtenerTodos() {
        return repositorioDispositivos.findAll();
    }
    
    //  Obtener dispositivo por ID
    public Optional<Dispositivo> obtenerPorId(Long id) {
        return repositorioDispositivos.findById(id);
    }
    
    //  Crear nuevo dispositivo
    public Dispositivo crear(Dispositivo dispositivo) {
        return repositorioDispositivos.save(dispositivo);
    }
    
    //  Actualizar dispositivo existente
    public Dispositivo actualizar(Long id, Dispositivo dispositivo) {
        // Verificar que el dispositivo existe
        if (repositorioDispositivos.existsById(id)) {
            dispositivo.setId(id); // Asegurar que tiene el ID correcto
            return repositorioDispositivos.save(dispositivo);
        }
        return null; // O lanzar una excepción
    }
    
    //  Eliminar dispositivo
    public boolean eliminar(Long id) {
        if (repositorioDispositivos.existsById(id)) {
            repositorioDispositivos.deleteById(id);
            return true;
        }
        return false;
    }
    
    //  Activar/desactivar dispositivo
    public boolean activarDesactivar(Long id, boolean activar) {
        Optional<Dispositivo> dispositivoOpt = repositorioDispositivos.findById(id);
        if (dispositivoOpt.isPresent()) {
            Dispositivo dispositivo = dispositivoOpt.get();
            dispositivo.setActivo(activar);
            repositorioDispositivos.save(dispositivo);
            return true;
        }
        return false;
    }
}