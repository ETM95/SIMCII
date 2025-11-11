// ...existing code...
package com.simcii.javaservice.services;

import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.models.Umbral;
import com.simcii.javaservice.repositories.UmbralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertaService {
    
    @Autowired
    private UmbralRepository umbralRepository;
    
    public void verificarUmbrales(Dispositivo dispositivo, Double valor) {
        List<Umbral> umbrales = umbralRepository.findByDispositivoAndActivoTrue(dispositivo);
        
        for (Umbral umbral : umbrales) {
            if (valor < umbral.getValorMin() || valor > umbral.getValorMax()) {
                generarAlerta(dispositivo, valor, umbral);
            }
        }
    }
    
    private void generarAlerta(Dispositivo dispositivo, Double valor, Umbral umbral) {
        // Aquí puedes implementar la lógica de alerta
        // Por ejemplo: enviar email, notificación, log, etc.
        System.out.println("ALERTA: Dispositivo " + dispositivo.getNombre() + 
                          " valor " + valor + " fuera de rango [" + 
                          umbral.getValorMin() + " - " + umbral.getValorMax() + "]");
    }
    
    public Umbral configurarUmbral(Umbral umbral) {
        return umbralRepository.save(umbral);
    }
    
    public List<Umbral> obtenerUmbralesPorDispositivo(Long dispositivoId) {
        return umbralRepository.findByDispositivoId(dispositivoId);
    }
}
// ...existing code...