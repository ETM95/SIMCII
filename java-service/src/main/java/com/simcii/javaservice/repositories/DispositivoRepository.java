package com.simcii.javaservice.repositories;

import com.simcii.javaservice.models.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {
    List<Dispositivo> findByActivoTrue();
    
    // @Query("SELECT d FROM Dispositivo d WHERE d INSTANCE OF Sensor")
    // List<Dispositivo> findAllSensores();
    
    @Query("SELECT d FROM Dispositivo d WHERE TYPE(d) = Actuador")
    List<Dispositivo> findAllActuadores();
}