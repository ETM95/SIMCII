package com.simcii.javaservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class EventoService {

    @Value("${python.service.url:http://python-service:8000}")
    private String pythonServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    public void enviarEventoPython(String tipoEvento, Map<String, Object> datos) {
        try {
            String url = pythonServiceUrl + "/api/v1/eventos";
            
            Map<String, Object> evento = Map.of(
                "id", UUID.randomUUID().toString(),
                "tipo", tipoEvento,
                "datos", datos
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(evento, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            System.out.println("✅ Evento enviado a Python Service: " + tipoEvento + 
                             " | Status: " + response.getStatusCode());
            
        } catch (Exception e) {
            System.err.println("❌ Error enviando evento a Python Service: " + e.getMessage());
            // En desarrollo, logueamos el error sin interrumpir el flujo principal
        }
    }
}