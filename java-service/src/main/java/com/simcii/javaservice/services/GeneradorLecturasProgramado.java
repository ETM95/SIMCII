package com.simcii.javaservice.services;

import com.simcii.javaservice.models.Lectura;
import com.simcii.javaservice.repositories.RepositorioLecturas;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class GeneradorLecturasProgramado {

    private final JdbcTemplate jdbc;
    private final RepositorioLecturas repoLecturas;
    private final Random random = new Random();

    public GeneradorLecturasProgramado(JdbcTemplate jdbc, RepositorioLecturas repoLecturas) {
        this.jdbc = jdbc;
        this.repoLecturas = repoLecturas;
    }

    // Ejecuta cada 10 segundos:
    @Scheduled(fixedRate = 10000)
    public void generarLecturas() {
        List<Map<String,Object>> sensores = jdbc.queryForList("SELECT id, tipo FROM dispositivos WHERE tipo = 'SENSOR' AND activo = true");
        for (Map<String,Object> fila : sensores) {
            Long id = ((Number)fila.get("id")).longValue();
            String tipo = (String)fila.get("tipo"); // no usado aquí
            double valor = generarValorAleatorio(id);
            Lectura lectura = new Lectura(null, id, valor, LocalDateTime.now());
            repoLecturas.guardar(lectura);

            // Si excede umbral, insertar una fila en tabla alertas (schema y lógica pueden añadirse)
            // Aquí solo guardamos lecturas; el servicio de Python se encargará de análisis/alertas.
        }
    }

    private double generarValorAleatorio(Long id) {
        // Generador simple por tipo de sensor (mejorar si guardas el subtipo)
        // Valore ejemplo entre 0 y 100
        return 10 + random.nextDouble() * 80;
    }
}
