// charts.js - Gráficos con datos de Python
const Charts = {
    chart: null,
    
    init: () => {
        const ctx = document.getElementById('temperatureChart');
        if (!ctx) return;
        
        Charts.chart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: Charts.generateTimeLabels(),
                datasets: [{
                    label: 'Temperatura (°C)',
                    data: Charts.generateInitialData(),
                    borderColor: '#10b981',
                    backgroundColor: 'rgba(16, 185, 129, 0.1)',
                    tension: 0.4,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: false,
                        min: 15,
                        max: 35,
                        title: {
                            display: true,
                            text: 'Temperatura (°C)'
                        }
                    },
                    x: {
                        title: {
                            display: true,
                            text: 'Tiempo'
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: true,
                        position: 'top'
                    },
                    title: {
                        display: true,
                        text: 'Temperatura en Tiempo Real - Zona A'
                    }
                }
            }
        });
        
        // Actualizar gráfico periódicamente
        setInterval(Charts.updateChart, 10000);
    },
    
    generateTimeLabels: () => {
        const labels = [];
        const now = new Date();
        for (let i = 9; i >= 0; i--) {
            const time = new Date(now.getTime() - i * 60000);
            labels.push(time.toLocaleTimeString('es-ES', { 
                hour: '2-digit', 
                minute: '2-digit' 
            }));
        }
        return labels;
    },
    
    generateInitialData: () => {
        const data = [];
        for (let i = 0; i < 10; i++) {
            data.push((Math.random() * 10 + 20).toFixed(1));
        }
        return data;
    },
    
    updateChart: () => {
        if (!Charts.chart) return;
        
        // Simular nueva lectura (en un sistema real, esto vendría de la API)
        const newValue = (Math.random() * 10 + 20).toFixed(1);
        
        // Actualizar datos
        Charts.chart.data.labels.push(new Date().toLocaleTimeString('es-ES', { 
            hour: '2-digit', 
            minute: '2-digit' 
        }));
        Charts.chart.data.datasets[0].data.push(newValue);
        
        // Mantener solo los últimos 10 puntos
        if (Charts.chart.data.labels.length > 10) {
            Charts.chart.data.labels.shift();
            Charts.chart.data.datasets[0].data.shift();
        }
        
        Charts.chart.update('none');
    }
};