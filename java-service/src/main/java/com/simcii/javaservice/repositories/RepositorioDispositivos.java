package com.simcii.javaservice.repositories;

import com.simcii.javaservice.models.Dispositivo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class RepositorioDispositivos {

    private final JdbcTemplate jdbc;

    public RepositorioDispositivos(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> listarTodos() {
        return jdbc.queryForList("SELECT id, nombre, zona, tipo, activo FROM dispositivos");
    }

    public Map<String, Object> obtenerPorId(Long id) {
        return jdbc.queryForMap("SELECT id, nombre, zona, tipo, activo FROM dispositivos WHERE id = ?", id);
    }

    public int crear(String nombre, String zona, String tipo, boolean activo) {
        return jdbc.update("INSERT INTO dispositivos(nombre, zona, tipo, activo) VALUES (?, ?, ?, ?)",
                nombre, zona, tipo, activo);
    }

    public int actualizar(Long id, String nombre, String zona, boolean activo) {
        return jdbc.update("UPDATE dispositivos SET nombre=?, zona=?, activo=? WHERE id=?", nombre, zona, activo, id);
    }

    public int eliminar(Long id) {
        return jdbc.update("DELETE FROM dispositivos WHERE id=?", id);
    }
}
