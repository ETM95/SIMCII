package com.simcii.javaservice.repositories;

import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.models.Umbral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UmbralRepository extends JpaRepository<Umbral, Long> {
     List<Umbral> findByDispositivo(Dispositivo dispositivo);
    List<Umbral> findByDispositivoAndActivoTrue(Dispositivo dispositivo);
    List<Umbral> findByDispositivoId(Long dispositivoId); // MÉTODO FALTANTE
}
