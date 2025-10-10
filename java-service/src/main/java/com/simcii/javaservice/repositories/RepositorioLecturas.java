package com.simcii.javaservice.repositories;

import com.simcii.javaservice.models.Lectura;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class RepositorioLecturas {

    private final JdbcTemplate jdbc;

    public RepositorioLecturas(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public int guardar(Lectura lectura) {
        return jdbc.update("INSERT INTO lecturas(dispositivo_id, valor, fecha) VALUES (?, ?, ?)",
                lectura.getDispositivoId(), lectura.getValor(), Timestamp.valueOf(lectura.getFecha()));
    }

    public List<Map<String,Object>> historialPorDispositivo(Long dispositivoId, int limite) {
        return jdbc.queryForList(
                "SELECT id, dispositivo_id, valor, fecha FROM lecturas WHERE dispositivo_id = ? ORDER BY fecha DESC LIMIT ?",
                dispositivoId, limite);
    }

    public List<Map<String,Object>> ultimasPorZona(String zona) {
        return jdbc.queryForList(
                "SELECT l.* FROM lecturas l JOIN dispositivos d ON l.dispositivo_id = d.id WHERE d.zona = ? ORDER BY l.fecha DESC LIMIT 100",
                zona);
    }
}

