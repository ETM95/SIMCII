// alerts.js - Actualizado para usar alertas de Python
const Alerts = {
    alerts: [],
    
    // Obtener alertas desde API Python
    fetchAlertsFromAPI: async () => {
        try {
            const response = await fetch(`${App.API_BASE_URL}/alertas/activas`);
            if (response.ok) {
                const data = await response.json();
                Alerts.alerts = data.alertas || [];
                Alerts.renderAlerts();
            }
        } catch (error) {
            console.error('Error fetching alerts:', error);
            // Usar alertas simuladas como fallback
            Alerts.alerts = Alerts.getSimulatedAlerts();
            Alerts.renderAlerts();
        }
    },
    
    // Alertas simuladas de respaldo
    getSimulatedAlerts: () => {
        return [
            {
                id: 1,
                dispositivo_nombre: 'Sensor Temp A1',
                tipo_alerta: 'TEMPERATURA_FUERA_RANGO',
                mensaje: 'Temperatura crítica: 35.2°C',
                valor: 35.2,
                zona: 'A',
                activa: true,
                fecha_creacion: new Date().toISOString(),
                nivel_criticidad: 2
            },
            {
                id: 2,
                dispositivo_nombre: 'Sensor Hum B2',
                tipo_alerta: 'HUMEDAD_FUERA_RANGO',
                mensaje: 'Humedad baja: 25.1%',
                valor: 25.1,
                zona: 'B',
                activa: true,
                fecha_creacion: new Date().toISOString(),
                nivel_criticidad: 1
            }
        ];
    },
    
    // Renderizar lista de alertas
    renderAlerts: () => {
        const alertsList = document.getElementById('alertsList');
        if (!alertsList) return;
        
        const activeAlerts = Alerts.alerts.filter(alert => alert.activa);
        document.getElementById('totalAlertas').textContent = activeAlerts.length;
        
        alertsList.innerHTML = '';
        
        if (activeAlerts.length === 0) {
            alertsList.innerHTML = `
                <div class="text-center py-8 text-gray-500">
                    <div class="text-4xl mb-2">✅</div>
                    <p>No hay alertas activas</p>
                    <p class="text-sm mt-1">Todo funciona correctamente</p>
                </div>
            `;
            return;
        }
        
        // Ordenar por criticidad (mayor primero)
        activeAlerts.sort((a, b) => (b.nivel_criticidad || 1) - (a.nivel_criticidad || 1));
        
        activeAlerts.slice(0, 10).forEach(alert => {
            const alertElement = document.createElement('div');
            const alertColor = Alerts.getAlertColor(alert.nivel_criticidad);
            alertElement.className = `bg-${alertColor}-50 border border-${alertColor}-200 rounded-lg p-4`;
            alertElement.innerHTML = `
                <div class="flex justify-between items-start">
                    <div class="flex items-start space-x-3">
                        <div class="text-xl mt-1">${Alerts.getAlertIcon(alert.nivel_criticidad)}</div>
                        <div class="flex-1">
                            <p class="text-sm font-medium text-gray-800">${alert.dispositivo_nombre}</p>
                            <p class="text-sm text-gray-600 mt-1">${alert.mensaje}</p>
                            <div class="flex items-center space-x-3 mt-2">
                                <span class="text-xs bg-${alertColor}-100 text-${alertColor}-800 px-2 py-1 rounded-full">
                                    Zona ${alert.zona}
                                </span>
                                <span class="text-xs text-gray-500">
                                    ${Alerts.formatDate(alert.fecha_creacion)}
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="text-right">
                        <span class="text-xs font-semibold text-${alertColor}-600">
                            ${Alerts.getCriticidadText(alert.nivel_criticidad)}
                        </span>
                        <div class="text-sm font-bold text-gray-800 mt-1">
                            ${alert.valor}${Alerts.getAlertUnit(alert.tipo_alerta)}
                        </div>
                    </div>
                </div>
            `;
            alertsList.appendChild(alertElement);
        });
    },
    
    // Obtener color según nivel de criticidad
    getAlertColor: (nivel) => {
        switch(nivel) {
            case 3: return 'red';
            case 2: return 'orange';
            case 1: return 'yellow';
            default: return 'gray';
        }
    },
    
    // Obtener icono según nivel de criticidad
    getAlertIcon: (nivel) => {
        switch(nivel) {
            case 3: return '🚨';
            case 2: return '⚠️';
            case 1: return '🔔';
            default: return '📢';
        }
    },
    
    // Obtener texto de criticidad
    getCriticidadText: (nivel) => {
        switch(nivel) {
            case 3: return 'CRÍTICA';
            case 2: return 'ALTA';
            case 1: return 'MEDIA';
            default: return 'BAJA';
        }
    },
    
    // Obtener unidad de medida según tipo de alerta
    getAlertUnit: (tipoAlerta) => {
        if (tipoAlerta.includes('TEMPERATURA')) return '°C';
        if (tipoAlerta.includes('HUMEDAD')) return '%';
        if (tipoAlerta.includes('LUZ')) return 'lux';
        return '';
    },
    
    // Formatear fecha
    formatDate: (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleTimeString('es-ES', { 
            hour: '2-digit', 
            minute: '2-digit' 
        });
    },
    
    // Exportar alertas a CSV usando la API de Python
    exportToCSV: () => {
        // Redirigir al endpoint de reportes de Python
        window.open(`${App.API_BASE_URL}/reportes/alertas/csv`, '_blank');
    }
};