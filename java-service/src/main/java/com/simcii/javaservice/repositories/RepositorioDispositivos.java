package com.simcii.javaservice.repositories;

import com.simcii.javaservice.models.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioDispositivos extends JpaRepository<Dispositivo, Long> {
    //   Spring Data JPA genera AUTOMÁTICAMENTE estos métodos:
    // - save(Dispositivo) → Crear/Actualizar
    // - findAll() → Listar todos
    // - findById(Long) → Obtener por ID  
    // - deleteById(Long) → Eliminar
    // - count() → Contar
    // - existsById(Long) → Verificar existencia
    
    //  YA NO NECESITAS: listarTodos(), obtenerPorId(), crear(), actualizar(), eliminar()
}