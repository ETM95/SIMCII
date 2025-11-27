package com.simcii.javaservice.controllers;

import com.simcii.javaservice.models.Lectura;
import com.simcii.javaservice.services.LecturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecturas")
@CrossOrigin(origins = "*")
public class LecturaController {
    
    @Autowired
    private LecturaService lecturaService;
    
    @GetMapping("/dispositivo/{dispositivoId}")
    public List<Lectura> getHistorialPorDispositivo(@PathVariable Long dispositivoId) {
        return lecturaService.obtenerHistorialPorDispositivo(dispositivoId);
    }
    
    @GetMapping("/dispositivo/{dispositivoId}/ultimas/{cantidad}")
    public List<Lectura> getUltimasLecturas(@PathVariable Long dispositivoId, @PathVariable int cantidad) {
        return lecturaService.obtenerUltimasLecturas(dispositivoId, cantidad);
    }
}