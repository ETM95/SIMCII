from typing import Optional
from datetime import datetime
import random

class Dispositivo:
    def __init__(self, id: int, nombre: str, tipo: str, ubicacion: str, 
                 activo: bool = True, rango_min: Optional[float] = None, 
                 rango_max: Optional[float] = None, unidad_medida: Optional[str] = None):
        self.id = id
        self.nombre = nombre
        self.tipo = tipo
        self.ubicacion = ubicacion
        self.activo = activo
        self.rango_min = rango_min
        self.rango_max = rango_max
        self.unidad_medida = unidad_medida
        self.ultima_lectura = None
        self.probabilidad_alerta = 0.0  # No usamos probabilidad en modo real

    def to_dict(self):
        return {
            'id': self.id,
            'nombre': self.nombre,
            'tipo': self.tipo,
            'ubicacion': self.ubicacion,
            'activo': self.activo,
            'rango_min': self.rango_min,
            'rango_max': self.rango_max,
            'unidad_medida': self.unidad_medida,
            'ultima_lectura': self.ultima_lectura
        }

class Alerta:
    def __init__(self, id: int, dispositivo_id: int, dispositivo_nombre: str, 
                 valor: float, tipo_alerta: str, mensaje: str, zona: str,
                 umbral_min: Optional[float] = None, umbral_max: Optional[float] = None):
        self.id = id
        self.dispositivo_id = dispositivo_id
        self.dispositivo_nombre = dispositivo_nombre
        self.valor = valor
        self.tipo_alerta = tipo_alerta
        self.mensaje = mensaje
        self.zona = zona
        self.umbral_min = umbral_min
        self.umbral_max = umbral_max
        self.fecha_creacion = datetime.now().isoformat()
        self.activa = True
        self.nivel_criticidad = self._calcular_criticidad()

    def _calcular_criticidad(self):
        if self.tipo_alerta == "TEMPERATURA_FUERA_RANGO":
            return 3 if (self.valor > 32 or self.valor < 14) else 2
        elif self.tipo_alerta == "HUMEDAD_FUERA_RANGO":
            return 3 if (self.valor > 80 or self.valor < 25) else 2
        elif self.tipo_alerta == "LUZ_FUERA_RANGO":
            return 3 if (self.valor > 1000 or self.valor < 50) else 2
        else:
            return 1

    def to_dict(self):
        return {
            'id': self.id,
            'dispositivo_id': self.dispositivo_id,
            'dispositivo_nombre': self.dispositivo_nombre,
            'valor': self.valor,
            'tipo_alerta': self.tipo_alerta,
            'mensaje': self.mensaje,
            'zona': self.zona,
            'umbral_min': self.umbral_min,
            'umbral_max': self.umbral_max,
            'fecha_creacion': self.fecha_creacion,
            'activa': self.activa,
            'nivel_criticidad': self.nivel_criticidad
        }

class AlertaCritica(Alerta):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.nivel_criticidad = 4  # Máxima criticidad

    def notificar(self, mensaje: str):
        print(f"🚨 ALERTA CRÍTICA: {mensaje}")

    def registrar_evento(self, evento: str):
        print(f"📝 Evento registrado: {evento}")