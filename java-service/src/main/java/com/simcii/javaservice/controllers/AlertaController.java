package com.simcii.javaservice.controllers;

import com.simcii.javaservice.models.Umbral;
import com.simcii.javaservice.services.AlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@CrossOrigin(origins = "*")
public class AlertaController {
    
    @Autowired
    private AlertaService alertaService;
    
    @PostMapping("/umbrales")
    public Umbral configurarUmbral(@RequestBody Umbral umbral) {
        return alertaService.configurarUmbral(umbral);
    }
    
    @GetMapping("/dispositivo/{dispositivoId}/umbrales")
    public List<Umbral> getUmbralesPorDispositivo(@PathVariable Long dispositivoId) {
        return alertaService.obtenerUmbralesPorDispositivo(dispositivoId);
    }
}