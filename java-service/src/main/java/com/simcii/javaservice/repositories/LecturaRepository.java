package com.simcii.javaservice.repositories;

import com.simcii.javaservice.models.Lectura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LecturaRepository extends JpaRepository<Lectura, Long> {
    List<Lectura> findByDispositivoIdOrderByFechaHoraDesc(Long dispositivoId);
    
    @Query("SELECT l FROM Lectura l WHERE l.dispositivo.id = :dispositivoId AND l.fechaHora BETWEEN :inicio AND :fin ORDER BY l.fechaHora DESC")
    List<Lectura> findByDispositivoIdAndFechaHoraBetween(Long dispositivoId, LocalDateTime inicio, LocalDateTime fin);
    
    @Query(value = "SELECT * FROM lecturas WHERE dispositivo_id = :dispositivoId ORDER BY fecha_hora DESC LIMIT :limit", nativeQuery = true)
    List<Lectura> findUltimasLecturas(Long dispositivoId, int limit);
}