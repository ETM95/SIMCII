package com.simcii.javaservice.services;

import com.simcii.javaservice.models.Lectura;
import com.simcii.javaservice.repositories.RepositorioLecturas;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioLecturas {
    
    private final RepositorioLecturas repositorioLecturas;
    
    public ServicioLecturas(RepositorioLecturas repositorioLecturas) {
        this.repositorioLecturas = repositorioLecturas;
    }
    
    //  Guardar nueva lectura
    public Lectura guardarLectura(Long dispositivoId, double valor) {
        Lectura lectura = new Lectura();
        lectura.setDispositivoId(dispositivoId);
        lectura.setValor(valor);
        lectura.setFecha(LocalDateTime.now());
        return repositorioLecturas.save(lectura);
    }
    
    //  Obtener historial de lecturas
    public List<Lectura> obtenerHistorial(Long dispositivoId) {
        return repositorioLecturas.findByDispositivoIdOrderByFechaDesc(dispositivoId);
    }
    
    //  Obtener última lectura
    public Optional<Lectura> obtenerUltimaLectura(Long dispositivoId) {
        List<Lectura> lecturas = repositorioLecturas.findByDispositivoIdOrderByFechaDesc(dispositivoId);
        return lecturas.isEmpty() ? Optional.empty() : Optional.of(lecturas.get(0));
    }
}