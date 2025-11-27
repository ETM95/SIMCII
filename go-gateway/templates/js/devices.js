const Devices = {
    devices: [],
    
    // Tipos de dispositivos 
    deviceTypes: {
        'SENSOR_TEMPERATURA': { name: '🌡️ Sensor Temperatura', unit: '°C' },
        'SENSOR_HUMEDAD': { name: '💧 Sensor Humedad', unit: '%' },
        'SENSOR_LUZ': { name: '💡 Sensor Luz', unit: 'lux' },
        'ACTUADOR': { name: '⚡ Actuador', unit: '' }
    },
    
    // Obtener dispositivos desde API Java
    fetchDevicesFromAPI: async () => {
        try {
            const response = await fetch(App.JAVA_API_BASE_URL);
            if (response.ok) {
                const data = await response.json();
                Devices.devices = data || [];
                Devices.renderDevices();
            } else {
                throw new Error('Error en la respuesta del servidor');
            }
        } catch (error) {
            console.error('Error fetching devices from Java API:', error);
            Devices.showError('Error al cargar dispositivos');
            Devices.devices = [];
            Devices.renderDevices();
        }
    },
    
    // Renderizar lista de dispositivos
    renderDevices: () => {
        const devicesList = document.getElementById('devicesList');
        if (!devicesList) return;
        
        devicesList.innerHTML = '';
        
        if (Devices.devices.length === 0) {
            devicesList.innerHTML = `
                <div class="text-center py-8 text-gray-500">
                    <div class="text-4xl mb-2">🔌</div>
                    <p>No hay dispositivos registrados</p>
                    <button onclick="showDeviceForm()" 
                            class="mt-4 text-green-600 hover:text-green-700 font-medium">
                        Agregar primer dispositivo
                    </button>
                </div>
            `;
            return;
        }
        
        Devices.devices.forEach(device => {
            const deviceType = Devices.deviceTypes[device.tipo] || { name: '❓ Dispositivo', unit: '' };
            const deviceElement = document.createElement('div');
            deviceElement.className = 'bg-gray-50 rounded-lg p-4 border border-gray-200 hover:border-green-300 transition duration-200';
            deviceElement.innerHTML = `
                <div class="flex justify-between items-start">
                    <div class="flex items-center space-x-3">
                        <div class="text-2xl">${deviceType.name.split(' ')[0]}</div>
                        <div class="flex-1">
                            <h4 class="font-semibold text-gray-800">${device.nombre}</h4>
                            <p class="text-sm text-gray-600">${deviceType.name}</p>
                            <div class="flex items-center space-x-4 mt-1">
                                <span class="text-xs bg-green-100 text-green-800 px-2 py-1 rounded-full">
                                    Zona ${device.zona}
                                </span>
                                <span class="text-xs ${device.activo ? 'text-green-600' : 'text-red-600'}">
                                    ${device.activo ? 'Activo' : 'Inactivo'}
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="text-right">
                        <div class="text-lg font-bold text-gray-800">
                            ${Devices.getLastReading(device.id) || '--'}${deviceType.unit}
                        </div>
                        <div class="flex space-x-2 mt-2">
                            <button onclick="Devices.editDevice(${device.id})" 
                                    class="text-xs px-2 py-1 bg-blue-100 text-blue-800 rounded hover:bg-blue-200 transition duration-200">
                                Editar
                            </button>
                            <button onclick="confirmDeleteDevice(${device.id})" 
                                    class="text-xs px-2 py-1 bg-red-100 text-red-800 rounded hover:bg-red-200 transition duration-200">
                                Eliminar
                            </button>
                        </div>
                    </div>
                </div>
            `;
            devicesList.appendChild(deviceElement);
        });
        
        // Actualizar estadísticas
        Devices.updateStats();
    },
    
    // Obtener última lectura (esto necesitaría una API adicional)
    getLastReading: (deviceId) => {
        // Por ahora devolvemos un valor simulado
        // En producción, necesitarías una API para obtener lecturas
        const readings = {
            1: '24.5',
            2: '65.2',
            3: '750'
        };
        return readings[deviceId] || null;
    },
    
    // Editar dispositivo
    editDevice: async (deviceId) => {
        try {
            const device = Devices.devices.find(d => d.id === deviceId);
            if (device) {
                App.currentEditingDevice = device;
                showDeviceForm(device);
            }
        } catch (error) {
            console.error('Error al cargar dispositivo para editar:', error);
            Devices.showError('Error al cargar dispositivo');
        }
    },
    
    // Actualizar estadísticas del dashboard
    updateStats: () => {
        const activeDevices = Devices.devices.filter(d => d.activo).length;
        document.getElementById('totalDispositivos').textContent = activeDevices;
    },
    
    // Mostrar mensaje de error
    showError: (message) => {
        // Puedes implementar un sistema de notificaciones más elegante
        console.error('Error:', message);
        alert(message);
    }
};