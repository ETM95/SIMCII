// app.js - Actualizado para el schema de la BD
const App = {
    // Configuración de APIs
    JAVA_API_BASE_URL: 'http://localhost:8080/api/dispositivos',
    PYTHON_API_BASE_URL: 'http://localhost:8000/api',
    
    // Estado de la aplicación
    currentEditingDevice: null,
    
    // Inicializar la aplicación
    init: () => {
        App.setupEventListeners();
        App.showDashboard();
        App.updateCurrentTime();
        setInterval(App.updateCurrentTime, 1000);
        App.startAutoUpdates();
    },
    
    // Configurar event listeners
    setupEventListeners: () => {
        const logoutBtn = document.getElementById('btn-logout');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', App.handleLogout);
        }
        
        const deviceForm = document.getElementById('device-form');
        if (deviceForm) {
            deviceForm.addEventListener('submit', App.handleDeviceSubmit);
            
            const descInput = document.getElementById('deviceDescription');
            if (descInput) {
                descInput.addEventListener('input', App.updateCharCount);
            }
        }
    },
    
    // Manejar logout
    handleLogout: () => {
        window.location.href = '/logout';
    },
    
    // Manejar envío del formulario de dispositivo
    handleDeviceSubmit: async (e) => {
        e.preventDefault();
        
        // Construir objeto según el schema de la BD
        const deviceData = {
            nombre: document.getElementById('deviceName').value,
            tipo: document.getElementById('deviceType').value,
            zona: document.getElementById('deviceLocation').value,
            activo: true
        };
        
        // Agregar descripción si existe (aunque no está en el schema, por si acaso)
        const descripcion = document.getElementById('deviceDescription').value;
        if (descripcion) {
            deviceData.descripcion = descripcion;
        }
        
        try {
            if (App.currentEditingDevice) {
                // Modo edición
                await App.updateDevice(App.currentEditingDevice.id, deviceData);
                Utils.showNotification('Dispositivo actualizado correctamente', 'success');
            } else {
                // Modo creación
                await App.createDevice(deviceData);
                Utils.showNotification('Dispositivo creado correctamente', 'success');
            }
            
            closeDeviceForm();
            Devices.fetchDevicesFromAPI(); // Recargar lista
        } catch (error) {
            console.error('Error al guardar dispositivo:', error);
            Utils.showNotification('Error al guardar el dispositivo', 'error');
        }
    },
    
    // Crear nuevo dispositivo
    createDevice: async (deviceData) => {
        const response = await fetch(App.JAVA_API_BASE_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(deviceData)
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error ${response.status}: ${errorText}`);
        }
        
        return await response.json();
    },
    
    // Actualizar dispositivo existente
    updateDevice: async (deviceId, deviceData) => {
        const response = await fetch(`${App.JAVA_API_BASE_URL}/${deviceId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(deviceData)
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error ${response.status}: ${errorText}`);
        }
        
        return await response.json();
    },
    
    // Eliminar dispositivo
    deleteDevice: async (deviceId) => {
        const response = await fetch(`${App.JAVA_API_BASE_URL}/${deviceId}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error ${response.status}: ${errorText}`);
        }
        
        return true;
    },
    
    // Resto de métodos permanecen igual...
    updateCharCount: () => {
        const input = document.getElementById('deviceDescription');
        const counter = document.getElementById('chars-remaining');
        const descCounter = document.getElementById('desc-counter');
        
        if (input && counter && descCounter) {
            const remaining = 15 - input.value.length;
            counter.textContent = remaining;
            descCounter.textContent = `(${input.value.length}/15)`;
        }
    },
    
    updateCurrentTime: () => {
        const timeElement = document.getElementById('current-time');
        if (timeElement) {
            const now = new Date();
            const timeString = now.toLocaleTimeString('es-ES', { 
                hour: '2-digit', 
                minute: '2-digit',
                second: '2-digit'
            });
            const dateString = now.toLocaleDateString('es-ES', {
                weekday: 'long',
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });
            
            timeElement.textContent = `${dateString} - ${timeString}`;
        }
    },
    
    showDashboard: () => {
        Devices.renderDevices();
        Alerts.renderAlerts();
        Charts.init();
    },
    
    startAutoUpdates: () => {
        setInterval(() => {
            Devices.fetchDevicesFromAPI();
            Alerts.fetchAlertsFromAPI();
            App.updateStatsFromAPI();
        }, 5000);
        
        Devices.fetchDevicesFromAPI();
        Alerts.fetchAlertsFromAPI();
        App.updateStatsFromAPI();
    },
    
    updateStatsFromAPI: async () => {
        try {
            const response = await fetch(`${App.PYTHON_API_BASE_URL}/estadisticas/zonas`);
            if (response.ok) {
                const data = await response.json();
                App.updateDashboardStats(data.estadisticas);
            }
        } catch (error) {
            console.error('Error fetching stats:', error);
        }
    },
    
    updateDashboardStats: (stats) => {
        if (!stats) return;
        
        let totalTemp = 0, totalHumidity = 0, tempCount = 0, humidityCount = 0;
        
        Object.values(stats).forEach(zona => {
            if (zona.estadisticas && zona.estadisticas.temperatura) {
                totalTemp += zona.estadisticas.temperatura.promedio;
                tempCount++;
            }
            if (zona.estadisticas && zona.estadisticas.humedad) {
                totalHumidity += zona.estadisticas.humedad.promedio;
                humidityCount++;
            }
        });
        
        const avgTemp = tempCount > 0 ? (totalTemp / tempCount).toFixed(1) : '--';
        const avgHumidity = humidityCount > 0 ? (totalHumidity / humidityCount).toFixed(1) : '--';
        
        document.getElementById('tempPromedio').textContent = avgTemp + '°C';
        document.getElementById('humedadPromedio').textContent = avgHumidity + '%';
    }
};
// Funciones globales para los modales - Actualizadas
function showDeviceForm(device = null) {
    const modal = document.getElementById('deviceModal');
    const form = document.getElementById('device-form');
    const title = modal.querySelector('h3');
    const submitBtn = document.getElementById('submit-btn');
    
    // Limpiar formulario
    form.reset();
    
    if (device) {
        // Modo edición
        title.textContent = 'Editar Dispositivo';
        submitBtn.textContent = 'Actualizar';
        
        // Llenar formulario con datos del dispositivo
        document.getElementById('deviceName').value = device.nombre || '';
        document.getElementById('deviceType').value = device.tipo || '';
        document.getElementById('deviceDescription').value = device.descripcion || '';
        document.getElementById('deviceLocation').value = device.zona || ''; // Cambiado de ubicacion a zona
        
        App.currentEditingDevice = device;
    } else {
        // Modo creación
        title.textContent = 'Agregar Dispositivo';
        submitBtn.textContent = 'Guardar';
        App.currentEditingDevice = null;
    }
    
    // Actualizar contador de caracteres
    App.updateCharCount();
    
    // Mostrar modal
    Utils.showElement('deviceModal');
}

function closeDeviceForm() {
    Utils.hideElement('deviceModal');
    App.currentEditingDevice = null;
    
    const form = document.getElementById('device-form');
    form.reset();
}

function confirmDeleteDevice(deviceId) {
    const device = Devices.devices.find(d => d.id === deviceId);
    if (device) {
        window.currentDeviceToDelete = deviceId;
        document.getElementById('delete-message').textContent = 
            `¿Estás seguro de que deseas eliminar el dispositivo "${device.nombre}"?`;
        Utils.showElement('deleteModal');
    }
}

function closeDeleteModal() {
    Utils.hideElement('deleteModal');
    window.currentDeviceToDelete = null;
}

async function confirmDelete() {
    if (window.currentDeviceToDelete) {
        try {
            await App.deleteDevice(window.currentDeviceToDelete);
            closeDeleteModal();
            Devices.fetchDevicesFromAPI();
            Utils.showNotification('Dispositivo eliminado correctamente', 'success');
        } catch (error) {
            console.error('Error al eliminar dispositivo:', error);
            Utils.showNotification('Error al eliminar el dispositivo', 'error');
        }
    }
}

function exportAlertasCSV() {
    Alerts.exportToCSV();
}