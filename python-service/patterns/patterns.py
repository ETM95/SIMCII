from abc import ABC, abstractmethod
from typing import List, Dict, Any
import threading
from queue import Queue
import time

class ObservadorEvento(ABC):
    @abstractmethod
    def actualizar(self, evento: str, datos: Dict[str, Any]):
        pass

class SujetoEvento(ABC):
    def __init__(self):
        self._observadores: List[ObservadorEvento] = []

    def agregar_observador(self, observador: ObservadorEvento):
        self._observadores.append(observador)

    def eliminar_observador(self, observador: ObservadorEvento):
        self._observadores.remove(observador)

    def notificar_observadores(self, evento: str, datos: Dict[str, Any]):
        for observador in self._observadores:
            observador.actualizar(evento, datos)

class GestorEventos(SujetoEvento):
    def __init__(self):
        super().__init__()
        self._cola_eventos = Queue()
        self._procesando = False
        self._hilo_procesador = None

    def publicar_evento(self, evento: str, datos: Dict[str, Any]):
        self._cola_eventos.put((evento, datos))
        if not self._procesando:
            self._iniciar_procesador()

    def _iniciar_procesador(self):
        self._procesando = True
        self._hilo_procesador = threading.Thread(target=self._procesar_eventos)
        self._hilo_procesador.daemon = True
        self._hilo_procesador.start()

    def _procesar_eventos(self):
        while not self._cola_eventos.empty():
            try:
                evento, datos = self._cola_eventos.get_nowait()
                self.notificar_observadores(evento, datos)
                self._cola_eventos.task_done()
            except:
                break
        self._procesando = False

# Observadores concretos
class ObservadorAlertas(ObservadorEvento):
    def __init__(self, data_store):
        self.data_store = data_store

    def actualizar(self, evento: str, datos: Dict[str, Any]):
        if evento == "nueva_alerta":
            self._manejar_nueva_alerta(datos)
        elif evento == "alerta_resuelta":
            self._manejar_alerta_resuelta(datos)

    def _manejar_nueva_alerta(self, datos: Dict[str, Any]):
        print(f"📢 Nueva alerta recibida: {datos.get('tipo_alerta')} - {datos.get('mensaje')}")

    def _manejar_alerta_resuelta(self, datos: Dict[str, Any]):
        print(f"✅ Alerta resuelta: {datos.get('id')}")

class ObservadorEstadisticas(ObservadorEvento):
    def __init__(self, data_store):
        self.data_store = data_store

    def actualizar(self, evento: str, datos: Dict[str, Any]):
        if evento == "nueva_lectura":
            self._actualizar_estadisticas(datos)

    def _actualizar_estadisticas(self, datos: Dict[str, Any]):
        pass