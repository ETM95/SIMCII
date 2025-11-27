from flask import Flask, jsonify
from flask_cors import CORS
import logging
import threading
import time
import requests
import sys

from config import config
from patterns.patterns import GestorEventos, ObservadorAlertas, ObservadorEstadisticas
from processors.processors import ProcesadorUmbrales, GeneradorAlertas
from services.data_store import DataStore
from routes import init_routes

# Configurar logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout),
        logging.FileHandler('logs/app.log')
    ]
)
logger = logging.getLogger(__name__)

app = Flask(__name__)
app.config.from_object(config['default'])
CORS(app)

# Inicializar componentes
gestor_eventos = GestorEventos()
data_store = DataStore(gestor_eventos)
procesador_umbrales = ProcesadorUmbrales()
generador_alertas = GeneradorAlertas()

# Registrar observadores
observador_alertas = ObservadorAlertas(data_store)
observador_estadisticas = ObservadorEstadisticas(data_store)
gestor_eventos.agregar_observador(observador_alertas)
gestor_eventos.agregar_observador(observador_estadisticas)

def obtener_dispositivos_desde_java():
    """Obtiene dispositivos reales desde el servicio Java"""
    max_retries = 3
    for attempt in range(max_retries):
        try:
            java_service_url = app.config['JAVA_SERVICE_URL']
            logger.info(f"🔗 Obteniendo dispositivos desde Java: {java_service_url}")
            response = requests.get(f'{java_service_url}/api/dispositivos', timeout=10)
            if response.status_code == 200:
                dispositivos_java = response.json()
                # Convertir al formato esperado por Python
                dispositivos = []
                for disp in dispositivos_java:
                    dispositivo = {
                        'id': disp.get('id'),
                        'nombre': disp.get('nombre'),
                        'tipo': disp.get('tipo'),
                        'ubicacion': disp.get('zona'),  # Mapear zona -> ubicacion
                        'activo': disp.get('activo', True)
                    }
                    dispositivos.append(dispositivo)
                
                data_store.actualizar_dispositivos_simulados(dispositivos)
                logger.info(f"✅ Obtenidos {len(dispositivos)} dispositivos desde Java")
                return dispositivos
            else:
                logger.warning(f"❌ Error HTTP {response.status_code} al obtener dispositivos")
        except requests.exceptions.ConnectionError:
            logger.warning(f"🔌 Intento {attempt + 1}/{max_retries}: No se pudo conectar con Java service")
            if attempt < max_retries - 1:
                time.sleep(5)
        except Exception as e:
            logger.error(f"⚠️ Error inesperado: {e}")
            break
    
    logger.error("❌ No se pudieron obtener dispositivos desde Java")
    return []

def obtener_lecturas_desde_java(dispositivo_id):
    """Obtiene lecturas reales para un dispositivo desde Java"""
    try:
        java_service_url = app.config['JAVA_SERVICE_URL']
        response = requests.get(f'{java_service_url}/api/lecturas/dispositivo/{dispositivo_id}', timeout=5)
        if response.status_code == 200:
            lecturas_java = response.json()
            # Convertir al formato esperado
            lecturas = []
            for lectura in lecturas_java:
                lecturas.append({
                    'valor': lectura.get('valor'),
                    'fecha': lectura.get('fechaHora')  # Mapear fechaHora -> fecha
                })
            return lecturas
    except Exception as e:
        logger.warning(f"⚠️ No se pudieron obtener lecturas para dispositivo {dispositivo_id}: {e}")
    return []

def obtener_umbrales_desde_java(dispositivo_id):
    """Obtiene umbrales reales para un dispositivo desde Java"""
    try:
        java_service_url = app.config['JAVA_SERVICE_URL']
        response = requests.get(f'{java_service_url}/api/umbrales/dispositivo/{dispositivo_id}', timeout=5)
        if response.status_code == 200:
            umbrales_java = response.json()
            # Convertir al formato esperado
            umbrales = []
            for umbral in umbrales_java:
                umbrales.append({
                    'minimo': umbral.get('valorMin'),  # Mapear valorMin -> minimo
                    'maximo': umbral.get('valorMax'),  # Mapear valorMax -> maximo
                    'activo': umbral.get('activo', True)
                })
            return umbrales
    except Exception as e:
        logger.warning(f"⚠️ No se pudieron obtener umbrales para dispositivo {dispositivo_id}: {e}")
    return []

def procesar_dispositivos_reales():
    """Procesa dispositivos reales y genera alertas basadas en datos reales"""
    if not data_store.simulacion_activa:
        return

    # Actualizar dispositivos cada minuto
    obtener_dispositivos_desde_java()

    # Procesar cada dispositivo real
    for dispositivo in data_store.dispositivos_simulados:
        if not dispositivo.activo:
            continue

        # Obtener lecturas reales del dispositivo
        lecturas = obtener_lecturas_desde_java(dispositivo.id)
        if not lecturas:
            logger.debug(f"📭 No hay lecturas para dispositivo {dispositivo.nombre} (ID: {dispositivo.id})")
            continue

        # Tomar la lectura más reciente
        ultima_lectura = lecturas[0] if lecturas else None
        if not ultima_lectura:
            continue

        valor = ultima_lectura.get('valor')
        if valor is None:
            continue

        dispositivo.ultima_lectura = valor

        # Obtener umbrales reales del dispositivo
        umbrales = obtener_umbrales_desde_java(dispositivo.id)
        if umbrales:
            # Usar el primer umbral activo
            umbral_activo = next((u for u in umbrales if u.get('activo')), umbrales[0] if umbrales else None)
            if umbral_activo:
                dispositivo.rango_min = umbral_activo.get('minimo')
                dispositivo.rango_max = umbral_activo.get('maximo')
                logger.debug(f"📊 Umbrales para {dispositivo.nombre}: {dispositivo.rango_min} - {dispositivo.rango_max}")

        # Evaluar si la lectura está fuera de los umbrales
        tipo_alerta = None
        if (dispositivo.rango_min is not None and dispositivo.rango_max is not None):
            if valor < dispositivo.rango_min:
                tipo_alerta = f"{dispositivo.tipo}_MINIMO"
                logger.info(f"📉 Valor por debajo del mínimo: {dispositivo.nombre} = {valor} < {dispositivo.rango_min}")
            elif valor > dispositivo.rango_max:
                tipo_alerta = f"{dispositivo.tipo}_MAXIMO"
                logger.info(f"📈 Valor por encima del máximo: {dispositivo.nombre} = {valor} > {dispositivo.rango_max}")

        if tipo_alerta:
            # Verificar si ya existe una alerta similar activa
            alerta_existente = next(
                (a for a in data_store.alertas 
                 if a.dispositivo_id == dispositivo.id 
                 and a.tipo_alerta == tipo_alerta 
                 and a.activa),
                None
            )

            if not alerta_existente:
                nueva_alerta = generador_alertas.generar_alerta(dispositivo, valor, tipo_alerta)
                data_store.agregar_alerta(nueva_alerta)
                logger.info(f"🚨 Nueva alerta REAL: {dispositivo.nombre} - {tipo_alerta} - Valor: {valor}")

    # Limpiar alertas antiguas
    data_store.limpiar_alertas_antiguas()

def procesamiento_background():
    """Tarea en segundo plano para el procesamiento continuo de datos reales"""
    logger.info("🔄 Iniciando hilo de procesamiento en segundo plano")
    while True:
        try:
            procesar_dispositivos_reales()
            time.sleep(app.config['SIMULATION_INTERVAL'])
        except Exception as e:
            logger.error(f"💥 Error en procesamiento: {e}")
            time.sleep(30)

def init_app():
    """Inicializa la aplicación"""
    data_store.simulacion_activa = True
    logger.info("🚀 Iniciando módulo Python de Alertas SIMCII - MODO DATOS REALES")
    
    # Obtener dispositivos iniciales
    obtener_dispositivos_desde_java()
    
    # Iniciar tarea de procesamiento en segundo plano
    hilo_procesamiento = threading.Thread(target=procesamiento_background)
    hilo_procesamiento.daemon = True
    hilo_procesamiento.start()

@app.before_first_request
def startup_event():
    init_app()

@app.route('/')
def root():
    return jsonify({
        "servicio": "Módulo Python - Alertas SIMCII",
        "version": "3.0.0",
        "estado": "activo",
        "modo": "DATOS_REALES",
        "procesamiento_activo": data_store.simulacion_activa,
        "dispositivos_monitoreados": len(data_store.dispositivos_simulados),
        "alertas_activas": len(data_store.obtener_alertas_activas()),
        "java_service_url": app.config['JAVA_SERVICE_URL'],
        "timestamp": time.time()
    })

@app.route('/health')
def health_check():
    alertas_activas = len(data_store.obtener_alertas_activas())
    return jsonify({
        "status": "healthy",
        "service": "python-alertas",
        "modo": "datos_reales",
        "procesamiento_activo": data_store.simulacion_activa,
        "dispositivos_monitoreados": len(data_store.dispositivos_simulados),
        "alertas_activas": alertas_activas,
        "timestamp": time.time()
    })

# Inicializar rutas
init_routes(app)

if __name__ == '__main__':
    logger.info("🚀 Servicio Python de Alertas SIMCII v3.0 iniciado - MODO DATOS REALES")
    logger.info(f"🔗 Java Service URL: {app.config['JAVA_SERVICE_URL']}")
    logger.info("📊 Procesamiento activo: Generando alertas basadas en datos reales")
    
    # Inicializar la aplicación
    init_app()
    
    app.run(
        host=app.config['HOST'], 
        port=app.config['PORT'], 
        debug=app.config['DEBUG'],
        use_reloader=False
    )