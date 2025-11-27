from abc import ABC, abstractmethod
from typing import Dict, Any
from models.models import Dispositivo, Alerta, AlertaCritica
import random

class Procesador(ABC):
    @abstractmethod
    def procesar(self, datos: Dict[str, Any]) -> Dict[str, Any]:
        pass

class ProcesadorUmbrales(Procesador):
    def procesar(self, datos: Dict[str, Any]) -> Dict[str, Any]:
        # Este procesador ahora es redundante ya que la lógica está en app.py
        # pero lo mantenemos por compatibilidad
        return datos

class GeneradorAlertas:
    def __init__(self):
        self.contador_alertas = 0

    def generar_alerta(self, dispositivo: Dispositivo, valor: float, tipo_alerta: str):
        self.contador_alertas += 1
        
        # Mensajes basados en el tipo de alerta
        mensajes = {
            "SENSOR_TEMPERATURA_MINIMO": [f"Temperatura muy baja: {valor:.1f}°C"],
            "SENSOR_TEMPERATURA_MAXIMO": [f"Temperatura muy alta: {valor:.1f}°C"],
            "SENSOR_HUMEDAD_MINIMO": [f"Humedad muy baja: {valor:.1f}%"],
            "SENSOR_HUMEDAD_MAXIMO": [f"Humedad muy alta: {valor:.1f}%"],
            "SENSOR_LUZ_MINIMO": [f"Intensidad lumínica muy baja: {valor:.0f} lux"],
            "SENSOR_LUZ_MAXIMO": [f"Intensidad lumínica muy alta: {valor:.0f} lux"],
        }

        mensaje = random.choice(mensajes.get(tipo_alerta, [f"Alerta: {valor} fuera de rango"]))

        # Determinar si es una alerta crítica basada en valores extremos
        es_critica = False
        if tipo_alerta == "SENSOR_TEMPERATURA_MINIMO" and valor < 10:
            es_critica = True
        elif tipo_alerta == "SENSOR_TEMPERATURA_MAXIMO" and valor > 35:
            es_critica = True
        elif tipo_alerta == "SENSOR_HUMEDAD_MINIMO" and valor < 20:
            es_critica = True
        elif tipo_alerta == "SENSOR_HUMEDAD_MAXIMO" and valor > 85:
            es_critica = True

        if es_critica:
            alerta = AlertaCritica(
                id=self.contador_alertas,
                dispositivo_id=dispositivo.id,
                dispositivo_nombre=dispositivo.nombre,
                valor=round(valor, 1),
                tipo_alerta=tipo_alerta,
                mensaje=f"CRÍTICA: {mensaje}",
                zona=dispositivo.ubicacion,
                umbral_min=dispositivo.rango_min,
                umbral_max=dispositivo.rango_max
            )
            
            alerta.notificar(f"Alerta crítica en {dispositivo.ubicacion}: {mensaje}")
            alerta.registrar_evento("Alerta crítica generada")
        else:
            alerta = Alerta(
                id=self.contador_alertas,
                dispositivo_id=dispositivo.id,
                dispositivo_nombre=dispositivo.nombre,
                valor=round(valor, 1),
                tipo_alerta=tipo_alerta,
                mensaje=mensaje,
                zona=dispositivo.ubicacion,
                umbral_min=dispositivo.rango_min,
                umbral_max=dispositivo.rango_max
            )

        return alerta