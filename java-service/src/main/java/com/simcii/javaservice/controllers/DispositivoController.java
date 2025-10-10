package com.simcii.javaservice.controllers;

import com.simcii.javaservice.repositories.RepositorioDispositivos;
import com.simcii.javaservice.repositories.RepositorioLecturas;
import com.simcii.javaservice.models.Lectura;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dispositivos")
public class DispositivoController {

    private final RepositorioDispositivos repoDispositivos;
    private final RepositorioLecturas repoLecturas;

    public DispositivoController(RepositorioDispositivos repoDispositivos, RepositorioLecturas repoLecturas) {
        this.repoDispositivos = repoDispositivos;
        this.repoLecturas = repoLecturas;
    }

    @GetMapping
    public ResponseEntity<List<Map<String,Object>>> listar() {
        return ResponseEntity.ok(repoDispositivos.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String,Object>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(repoDispositivos.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody Map<String,Object> body) {
        String nombre = (String)body.get("nombre");
        String zona = (String)body.get("zona");
        String tipo = (String)body.get("tipo");
        boolean activo = body.get("activo") == null ? true : (Boolean)body.get("activo");
        repoDispositivos.crear(nombre, zona, tipo, activo);
        return ResponseEntity.ok("Dispositivo creado");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody Map<String,Object> body) {
        String nombre = (String)body.get("nombre");
        String zona = (String)body.get("zona");
        boolean activo = body.get("activo") == null ? true : (Boolean)body.get("activo");
        repoDispositivos.actualizar(id, nombre, zona, activo);
        return ResponseEntity.ok("Dispositivo actualizado");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        repoDispositivos.eliminar(id);
        return ResponseEntity.ok("Dispositivo eliminado");
    }

    // activar o desactivar actuador manualmente
    @PostMapping("/{id}/activar")
    public ResponseEntity<String> activarActuador(@PathVariable Long id, @RequestParam boolean activar) {
        // Aquí solo actualizamos campo activo. En un sistema real, habría comunicación con hardware.
        repoDispositivos.actualizar(id, (String)repoDispositivos.obtenerPorId(id).get("nombre"),
                (String)repoDispositivos.obtenerPorId(id).get("zona"), activar);
        return ResponseEntity.ok("Actuador actualizado");
    }

    // historial lecturas
    @GetMapping("/{id}/lecturas")
    public ResponseEntity<List<Map<String,Object>>> historial(@PathVariable Long id,
                                                              @RequestParam(defaultValue = "100") int limite) {
        return ResponseEntity.ok(repoLecturas.historialPorDispositivo(id, limite));
    }
}

