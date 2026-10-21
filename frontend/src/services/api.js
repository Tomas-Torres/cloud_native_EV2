import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('lumina-token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('lumina-token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authService = {
  login: (credentials) => api.post('/usuarios/login', credentials),
  registro: (data) => api.post('/usuarios/registro', data),
  perfil: (id) => api.get(`/usuarios/${id}`),
  listarTodos: () => api.get('/usuarios'),
};

export const productosService = {
  listar: () => api.get('/productos'),
  obtener: (id) => api.get(`/productos/${id}`),
  marcas: () => api.get('/productos/marcas'),
  crear: (data) => api.post('/productos', data),
  actualizar: (id, data) => api.put(`/productos/${id}`, data),
  eliminar: (id) => api.delete(`/productos/${id}`),
};

export const carritoService = {
  obtener: (usuarioId) => api.get(`/carrito/${usuarioId}`),
  agregar: (usuarioId, data) => api.post(`/carrito/${usuarioId}/agregar`, data),
  eliminar: (usuarioId, productoId) => api.delete(`/carrito/${usuarioId}/eliminar/${productoId}`),
};

export const pagosService = {
  crearPreferencia: (data) => api.post('/pagos/crear-preferencia', data),
  obtener: (id) => api.get(`/pagos/${id}`),
  obtenerPorOrden: (ordenId) => api.get(`/pagos/orden/${ordenId}`),
};

export const deliveryService = {
  obtener: (id) => api.get(`/delivery/${id}`),
  obtenerPorOrden: (ordenId) => api.get(`/delivery/orden/${ordenId}`),
  listarTodos: () => api.get('/delivery'),
};

export const bodegaService = {
  inventario: () => api.get('/bodega/inventario'),
  stock: (productoId) => api.get(`/bodega/inventario/${productoId}`),
  crearInventario: (data) => api.post('/bodega/inventario', data),
  agregarStock: (productoId, cantidad) => api.patch(`/bodega/inventario/${productoId}/agregar`, { cantidad }),
  descontarStock: (productoId, cantidad) => api.patch(`/bodega/inventario/${productoId}/descontar`, { cantidad }),
  alertas: () => api.get('/bodega/alertas'),
};


export default api;
