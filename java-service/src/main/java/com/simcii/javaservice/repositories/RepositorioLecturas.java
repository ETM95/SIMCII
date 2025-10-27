package com.simcii.javaservice.repositories;

import com.simcii.javaservice.models.Lectura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RepositorioLecturas extends JpaRepository<Lectura, Long> {
    
    @Query("SELECT l FROM Lectura l WHERE l.dispositivoId = :dispositivoId ORDER BY l.fecha DESC")
    List<Lectura> findByDispositivoIdOrderByFechaDesc(@Param("dispositivoId") Long dispositivoId);
    
    // Para limitar resultados, usamos Pageable
    // List<Lectura> findTop10ByDispositivoIdOrderByFechaDesc(Long dispositivoId);
}