// utils.js - Con sistema de notificaciones
const Utils = {
    // Mostrar/ocultar elementos
    showElement: (id) => {
        const element = document.getElementById(id);
        if (element) element.classList.remove('hidden');
    },
    
    hideElement: (id) => {
        const element = document.getElementById(id);
        if (element) element.classList.add('hidden');
    },
    
    // Formatear fecha
    formatDate: (date) => {
        return new Date(date).toLocaleDateString('es-ES', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    },
    
    // Mostrar notificación
    showNotification: (message, type = 'info') => {
        // Crear elemento de notificación
        const notification = document.createElement('div');
        const bgColor = type === 'success' ? 'bg-green-500' : 
                       type === 'error' ? 'bg-red-500' : 
                       'bg-blue-500';
        
        notification.className = `fixed top-4 right-4 p-4 rounded-lg shadow-lg z-50 text-white ${bgColor} animate-slide-in`;
        notification.innerHTML = `
            <div class="flex items-center">
                <span class="mr-2">${type === 'success' ? '✅' : type === 'error' ? '❌' : 'ℹ️'}</span>
                <span>${message}</span>
            </div>
        `;
        
        document.body.appendChild(notification);
        
        // Auto-eliminar después de 5 segundos
        setTimeout(() => {
            notification.classList.add('animate-slide-out');
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 5000);
    }
};

// Agregar estilos CSS para las animaciones
const style = document.createElement('style');
style.textContent = `
    @keyframes slide-in {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    @keyframes slide-out {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
    .animate-slide-in {
        animation: slide-in 0.3s ease-out;
    }
    .animate-slide-out {
        animation: slide-out 0.3s ease-in;
    }
`;
document.head.appendChild(style);