// ======================== CONFIGURACIÓN GLOBAL ========================
const API_BASE_URL = '/api/v1';

const appState = {
    token: null,
    user: null,
    userRole: 'user',
    marcas: [],
    gafas: []
};

// ======================== INICIALIZACIÓN ========================
document.addEventListener('DOMContentLoaded', () => {
    setupEventListeners();
    initializeApp();
});

function setupEventListeners() {
    document.getElementById('loginForm')?.addEventListener('submit', login);
    document.getElementById('logoutBtn')?.addEventListener('click', logout);
    
    document.querySelectorAll('.tab-button').forEach(btn => {
        btn.addEventListener('click', switchTab);
    });
    
    document.getElementById('marcaForm')?.addEventListener('submit', createMarca);
    document.getElementById('gafaForm')?.addEventListener('submit', createGafa);
}

function initializeApp() {
    const savedToken = localStorage.getItem('token');
    const savedUser = localStorage.getItem('user');
    const savedRole = localStorage.getItem('userRole');
    
    if (savedToken && savedUser) {
        appState.token = savedToken;
        appState.user = savedUser;
        appState.userRole = savedRole || 'user';
        showDashboard();
        loadData();
    }
}

// ======================== AUTENTICACIÓN ========================
async function login(e) {
    e.preventDefault();
    
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    
    if (!username || !password) {
        showLoginErrorMessage('Por favor completa todos los campos');
        return;
    }
    
    const loginBtn = document.querySelector('#loginForm button');
    const buttonText = document.getElementById('loginButtonText');
    const spinner = document.getElementById('loginSpinner');
    
    try {
        loginBtn.disabled = true;
        buttonText.classList.add('hidden');
        spinner.classList.remove('hidden');
        
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Credenciales inválidas');
        }
        
        const data = await response.json();
        const token = data.token || data.accessToken || data.jwt || data.access_token;
        
        if (!token) {
            throw new Error('No se recibió token de autenticación');
        }
        
        // Determinar rol basado en el username
        const userRole = username.toLowerCase() === 'admin' ? 'admin' : 'user';
        
        appState.token = token;
        appState.user = username;
        appState.userRole = userRole;
        
        localStorage.setItem('token', token);
        localStorage.setItem('user', username);
        localStorage.setItem('userRole', userRole);
        
        showToast(`¡Bienvenido ${username}! 👋`, 'success');
        
        setTimeout(() => {
            showDashboard();
            loadData();
        }, 600);
        
    } catch (error) {
        showLoginErrorMessage(error.message || 'Error al iniciar sesión');
    } finally {
        loginBtn.disabled = false;
        buttonText.classList.remove('hidden');
        spinner.classList.add('hidden');
    }
}

function showLoginErrorMessage(message) {
    const errorDiv = document.getElementById('loginError');
    document.getElementById('loginErrorText').textContent = message;
    errorDiv.classList.remove('hidden');
    
    setTimeout(() => {
        errorDiv.classList.add('hidden');
    }, 5000);
}

function logout() {
    if (!confirm('¿Estás seguro de que deseas cerrar sesión?')) return;
    
    appState.token = null;
    appState.user = null;
    appState.userRole = 'user';
    appState.marcas = [];
    appState.gafas = [];
    
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('userRole');
    
    document.getElementById('loginForm').reset();
    document.getElementById('loginError').classList.add('hidden');
    
    showLoginScreen();
    showToast('Sesión cerrada correctamente 👋', 'info');
}

// ======================== NAVEGACIÓN DE UI ========================
function showLoginScreen() {
    document.getElementById('loginScreen').classList.remove('hidden');
    document.getElementById('dashboardScreen').classList.add('hidden');
}

function showDashboard() {
    document.getElementById('loginScreen').classList.add('hidden');
    document.getElementById('dashboardScreen').classList.remove('hidden');
    
    document.getElementById('userNameDisplay').textContent = appState.user;
    
    const roleDisplay = document.getElementById('userRoleDisplay');
    if (appState.userRole === 'admin') {
        roleDisplay.innerHTML = '<i class="fas fa-crown mr-1"></i> Administrador';
        roleDisplay.className = 'inline-flex items-center gap-1 px-3 py-1 bg-primary-500/20 text-primary-300 rounded-full text-xs font-semibold border border-primary-500/30';
    } else {
        roleDisplay.innerHTML = '<i class="fas fa-user mr-1"></i> Usuario';
        roleDisplay.className = 'inline-flex items-center gap-1 px-3 py-1 bg-blue-500/20 text-blue-300 rounded-full text-xs font-semibold border border-blue-500/30';
    }
    
    updateAdminUI();
}

function updateAdminUI() {
    const isAdmin = appState.userRole === 'admin';
    const marcasFormCard = document.getElementById('marcasFormCard');
    const gafasFormCard = document.getElementById('gafasFormCard');
    
    if (isAdmin) {
        marcasFormCard.classList.remove('hidden');
        gafasFormCard.classList.remove('hidden');
    } else {
        marcasFormCard.classList.add('hidden');
        gafasFormCard.classList.add('hidden');
    }
}

function switchTab(e) {
    const tabName = e.target.dataset.tab;
    if (!tabName) return;
    
    document.querySelectorAll('.tab-button').forEach(btn => {
        btn.classList.remove('active', 'text-primary-400', 'border-b-primary-500');
        btn.classList.add('text-gray-500', 'border-b-transparent');
    });
    e.target.classList.add('active', 'text-primary-400', 'border-b-primary-500');
    e.target.classList.remove('text-gray-500', 'border-b-transparent');
    
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.add('hidden');
    });
    document.getElementById(`${tabName}Tab`)?.classList.remove('hidden');
}

// ======================== API COMUNICACIÓN ========================
async function apiFetch(url, method = 'GET', body = null) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${appState.token}`
        }
    };
    
    if (body) {
        options.body = JSON.stringify(body);
    }
    
    try {
        const response = await fetch(url, options);
        
        if (response.status === 401) {
            showToast('⏱️ Sesión expirada. Por favor inicia sesión nuevamente.', 'error');
            setTimeout(() => logout(), 2000);
            throw new Error('Unauthorized');
        }
        
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Error ${response.status}`);
        }
        
        return await response.json().catch(() => null);
    } catch (error) {
        throw error;
    }
}

// ======================== CARGAR DATOS ========================
async function loadData() {
    try {
        showLoading(true);
        await Promise.all([loadMarcas(), loadGafas()]);
    } catch (error) {
        showToast('❌ Error al cargar los datos', 'error');
    } finally {
        showLoading(false);
    }
}

async function loadMarcas() {
    try {
        const data = await apiFetch(`${API_BASE_URL}/marcas`, 'GET');
        appState.marcas = Array.isArray(data) ? data : [];
        renderMarcas();
        updateMarcasSelect();
    } catch (error) {
        showToast('❌ Error al cargar marcas', 'error');
        appState.marcas = [];
        renderMarcas();
    }
}

async function loadGafas() {
    try {
        const data = await apiFetch(`${API_BASE_URL}/gafas`, 'GET');
        appState.gafas = Array.isArray(data) ? data : [];
        renderGafas();
    } catch (error) {
        showToast('❌ Error al cargar gafas', 'error');
        appState.gafas = [];
        renderGafas();
    }
}

// ======================== RENDERIZADO: MARCAS ========================
function renderMarcas() {
    const tbody = document.getElementById('marcasTableBody');
    const empty = document.getElementById('marcasEmpty');
    
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    if (appState.marcas.length === 0) {
        empty?.classList.remove('hidden');
        return;
    }
    
    empty?.classList.add('hidden');
    
    appState.marcas.forEach(marca => {
        const row = document.createElement('tr');
        row.className = 'transition';
        row.innerHTML = `
            <td class="px-8 py-4 text-sm text-gray-300 font-mono">${truncateUUID(marca.id)}</td>
            <td class="px-8 py-4 text-sm font-medium text-white">${escapeHtml(marca.nombre)}</td>
            <td class="px-8 py-4 text-sm">
                ${appState.userRole === 'admin' 
                    ? `<button onclick="deleteMarcaUI('${marca.id}')" class="px-4 py-2 bg-red-500/20 text-red-400 hover:bg-red-500/30 border border-red-500/30 rounded-lg text-xs font-semibold transition btn-icon">
                        <i class="fas fa-trash mr-1"></i>Eliminar
                      </button>`
                    : '<span class="text-gray-600">-</span>'
                }
            </td>
        `;
        tbody.appendChild(row);
    });
}

function updateMarcasSelect() {
    const select = document.getElementById('gafaMarca');
    if (!select) return;
    
    const currentValue = select.value;
    
    select.innerHTML = '<option value="">Selecciona una marca...</option>';
    appState.marcas.forEach(marca => {
        const option = document.createElement('option');
        option.value = marca.id;
        option.textContent = marca.nombre;
        select.appendChild(option);
    });
    
    if (currentValue) {
        select.value = currentValue;
    }
}

// ======================== RENDERIZADO: GAFAS ========================
function renderGafas() {
    const tbody = document.getElementById('gafasTableBody');
    const empty = document.getElementById('gafasEmpty');
    
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    if (appState.gafas.length === 0) {
        empty?.classList.remove('hidden');
        return;
    }
    
    empty?.classList.add('hidden');
    
    appState.gafas.forEach(gafa => {
        const marcaNombre = gafa.marca?.nombre || gafa.marcaNombre || 'N/A';
        const row = document.createElement('tr');
        row.className = 'transition';
        row.innerHTML = `
            <td class="px-8 py-4 text-sm text-gray-300 font-mono">${truncateUUID(gafa.id)}</td>
            <td class="px-8 py-4 text-sm font-medium text-white">${escapeHtml(gafa.modelo)}</td>
            <td class="px-8 py-4 text-sm">
                <span class="inline-block px-3 py-1 bg-primary-500/20 text-primary-300 rounded-full text-xs font-semibold border border-primary-500/30">
                    ${escapeHtml(marcaNombre)}
                </span>
            </td>
            <td class="px-8 py-4 text-sm">
                ${appState.userRole === 'admin'
                    ? `<button onclick="deleteGafaUI('${gafa.id}')" class="px-4 py-2 bg-red-500/20 text-red-400 hover:bg-red-500/30 border border-red-500/30 rounded-lg text-xs font-semibold transition btn-icon">
                        <i class="fas fa-trash mr-1"></i>Eliminar
                      </button>`
                    : '<span class="text-gray-600">-</span>'
                }
            </td>
        `;
        tbody.appendChild(row);
    });
}

// ======================== CREAR MARCA ========================
async function createMarca(e) {
    e.preventDefault();
    
    const nombre = document.getElementById('marcaNombre').value.trim();
    
    if (!nombre) {
        showToast('⚠️ Por favor ingresa el nombre de la marca', 'error');
        return;
    }
    
    try {
        showLoading(true);
        
        const data = await apiFetch(`${API_BASE_URL}/marcas`, 'POST', { nombre });
        
        if (data && data.id) {
            appState.marcas.push(data);
            renderMarcas();
            updateMarcasSelect();
            document.getElementById('marcaForm').reset();
            showToast(`✅ Marca "${nombre}" creada exitosamente`, 'success');
        }
    } catch (error) {
        showToast(`❌ ${error.message || 'Error al crear la marca'}`, 'error');
    } finally {
        showLoading(false);
    }
}

async function deleteMarcaUI(id) {
    const marca = appState.marcas.find(m => m.id === id);
    if (!marca) return;
    
    if (!confirm(`⚠️ ¿Eliminar la marca "${marca.nombre}"?\n\nEsta acción no se puede deshacer.`)) return;
    
    try {
        showLoading(true);
        await apiFetch(`${API_BASE_URL}/marcas/${id}`, 'DELETE');
        appState.marcas = appState.marcas.filter(m => m.id !== id);
        renderMarcas();
        updateMarcasSelect();
        showToast('✅ Marca eliminada exitosamente', 'success');
    } catch (error) {
        showToast(`❌ ${error.message || 'Error al eliminar la marca'}`, 'error');
    } finally {
        showLoading(false);
    }
}

// ======================== CREAR GAFA ========================
async function createGafa(e) {
    e.preventDefault();
    
    const modelo = document.getElementById('gafaModelo').value.trim();
    const marcaId = document.getElementById('gafaMarca').value;
    
    if (!modelo || !marcaId) {
        showToast('⚠️ Por favor completa todos los campos', 'error');
        return;
    }
    
    try {
        showLoading(true);
        
        // PAYLOAD CORRECTO: marcaId como UUID string, modelo como string
        const payload = { 
            marcaId,
            modelo 
        };
        
        const data = await apiFetch(`${API_BASE_URL}/gafas`, 'POST', payload);
        
        if (data && data.id) {
            appState.gafas.push(data);
            renderGafas();
            document.getElementById('gafaForm').reset();
            showToast(`✅ Modelo "${modelo}" creado exitosamente`, 'success');
        }
    } catch (error) {
        showToast(`❌ ${error.message || 'Error al crear la gafa'}`, 'error');
    } finally {
        showLoading(false);
    }
}

async function deleteGafaUI(id) {
    const gafa = appState.gafas.find(g => g.id === id);
    if (!gafa) return;
    
    if (!confirm(`⚠️ ¿Eliminar el modelo "${gafa.modelo}"?\n\nEsta acción no se puede deshacer.`)) return;
    
    try {
        showLoading(true);
        await apiFetch(`${API_BASE_URL}/gafas/${id}`, 'DELETE');
        appState.gafas = appState.gafas.filter(g => g.id !== id);
        renderGafas();
        showToast('✅ Modelo eliminado exitosamente', 'success');
    } catch (error) {
        showToast(`❌ ${error.message || 'Error al eliminar la gafa'}`, 'error');
    } finally {
        showLoading(false);
    }
}

// ======================== UTILIDADES ========================
function escapeHtml(text) {
    if (typeof text !== 'string') return '';
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}

function truncateUUID(uuid) {
    if (typeof uuid !== 'string') return 'N/A';
    return uuid.substring(0, 8) + '...';
}

function showLoading(show) {
    const spinner = document.getElementById('loadingSpinner');
    if (show) {
        spinner?.classList.remove('hidden');
    } else {
        spinner?.classList.add('hidden');
    }
}

function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;
    
    let bgClass = 'bg-blue-500/20 border-blue-500/30 text-blue-300';
    let icon = 'ℹ️';
    
    if (type === 'success') {
        bgClass = 'bg-green-500/20 border-green-500/30 text-green-300';
        icon = '✅';
    } else if (type === 'error') {
        bgClass = 'bg-red-500/20 border-red-500/30 text-red-300';
        icon = '❌';
    }
    
    const toast = document.createElement('div');
    toast.className = `${bgClass} border px-6 py-4 rounded-lg shadow-lg slide-down backdrop-blur-sm flex items-center gap-3 pointer-events-auto max-w-sm`;
    toast.innerHTML = `
        <span class="text-lg">${icon}</span>
        <span class="flex-1 text-sm font-medium">${escapeHtml(message)}</span>
        <button onclick="this.parentElement.remove()" class="text-xs opacity-50 hover:opacity-75 transition">
            <i class="fas fa-close"></i>
        </button>
    `;
    
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.remove();
    }, 5000);
}

