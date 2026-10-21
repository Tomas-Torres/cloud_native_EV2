import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { Plus, Pencil, Trash2, X, Save, Shield, Package, Warehouse, AlertTriangle, ArrowUp, ArrowDown } from 'lucide-react';
import { productosService, bodegaService } from '../services/api';
import { useAuth } from '../context/AuthContext';

const CATEGORIAS = ['Poleras', 'Chaquetas', 'Pantalones', 'Calzado', 'Accesorios'];
const MARCAS = ['Nike', 'Adidas', 'The North Face'];

function AdminPage() {
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editando, setEditando] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [tab, setTab] = useState('productos');
  const [inventario, setInventario] = useState([]);
  const [alertas, setAlertas] = useState([]);
  const [stockForm, setStockForm] = useState({ productoId: '', cantidad: '' });
  const [stockAction, setStockAction] = useState('agregar');
  const [form, setForm] = useState({
    nombre: '',
    descripcion: '',
    precio: '',
    imagenUrl: '',
    categoria: 'Poleras',
    marca: 'Nike',
  });

  useEffect(() => {
    if (!isAuthenticated || user?.rol !== 'ADMIN') {
      navigate('/login');
      return;
    }
    cargarProductos();
    cargarInventario();
  }, [isAuthenticated, user]);

  const cargarProductos = async () => {
    try {
      const res = await productosService.listar();
      const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
      setProductos(data);
    } catch (err) {
      setError('Error al cargar productos');
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setForm({ nombre: '', descripcion: '', precio: '', imagenUrl: '', categoria: 'Poleras', marca: 'Nike' });
    setEditando(null);
    setShowForm(false);
  };

  const handleEdit = (producto) => {
    setForm({
      nombre: producto.nombre,
      descripcion: producto.descripcion || '',
      precio: String(producto.precio),
      imagenUrl: producto.imagenUrl || '',
      categoria: producto.categoria || 'Poleras',
      marca: producto.marca?.nombre || producto.marca || 'Nike',
    });
    setEditando(producto.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!confirm('¿Seguro que deseas eliminar este producto?')) return;
    try {
      await productosService.eliminar(id);
      setProductos(productos.filter((p) => p.id !== id));
      setSuccess('Producto eliminado');
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError('Error al eliminar producto');
      setTimeout(() => setError(''), 3000);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    const payload = {
      nombre: form.nombre,
      descripcion: form.descripcion,
      precio: parseFloat(form.precio),
      imagenUrl: form.imagenUrl || `https://placehold.co/400x300/333/fff?text=${encodeURIComponent(form.nombre)}`,
      categoria: form.categoria,
      activo: true,
    };

    try {
      if (editando) {
        await productosService.actualizar(editando, payload);
        setSuccess('Producto actualizado');
      } else {
        await productosService.crear(payload);
        setSuccess('Producto creado');
      }
      resetForm();
      cargarProductos();
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      const msg = err.response?.data?.message || 'Error al guardar producto';
      setError(typeof msg === 'string' ? msg : 'Error al guardar producto');
      setTimeout(() => setError(''), 3000);
    }
  };

  const cargarInventario = async () => {
    try {
      const [invRes, alertRes] = await Promise.all([bodegaService.inventario(), bodegaService.alertas()]);
      const invData = typeof invRes.data === 'string' ? JSON.parse(invRes.data) : invRes.data;
      const alertData = typeof alertRes.data === 'string' ? JSON.parse(alertRes.data) : alertRes.data;
      setInventario(invData);
      setAlertas(alertData);
    } catch (err) {
      console.error('Error cargando inventario:', err);
    }
  };

  const handleStockSubmit = async (e) => {
    e.preventDefault();
    setError(''); setSuccess('');
    const productoId = parseInt(stockForm.productoId);
    const cantidad = parseInt(stockForm.cantidad);
    try {
      if (stockAction === 'agregar') {
        await bodegaService.agregarStock(productoId, cantidad);
        setSuccess(`Stock agregado (+${cantidad})`);
      } else {
        await bodegaService.descontarStock(productoId, cantidad);
        setSuccess(`Stock descontado (-${cantidad})`);
      }
      setStockForm({ productoId: '', cantidad: '' });
      cargarInventario();
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      const msg = err.response?.data?.message || 'Error al actualizar stock';
      setError(typeof msg === 'string' ? msg : 'Error al actualizar stock');
      setTimeout(() => setError(''), 3000);
    }
  };

  const handleInitStock = async (producto) => {
    try {
      await bodegaService.crearInventario({
        productoId: producto.id,
        nombreProducto: producto.nombre,
        stock: 50,
        stockMinimo: 10,
        ubicacionBodega: 'Bodega Principal',
      });
      setSuccess(`Inventario creado para ${producto.nombre}`);
      cargarInventario();
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError('Error al crear inventario');
      setTimeout(() => setError(''), 3000);
    }
  };

  const formatPrice = (price) => {
    return new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(price);
  };

  if (!isAuthenticated || user?.rol !== 'ADMIN') {
    return null;
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center justify-between mb-8">
        <div className="flex items-center gap-3">
          <Shield className="w-8 h-8 text-lumina-600" />
          <div>
            <h1 className="text-3xl font-bold">Panel de Administracion</h1>
            <p className="text-sm text-gray-500">Gestiona productos y stock</p>
          </div>
        </div>
        <button
          onClick={() => { resetForm(); setShowForm(true); }}
          className="btn-primary flex items-center gap-2"
        >
          <Plus className="w-4 h-4" />
          Nuevo Producto
        </button>
      </div>

      {/* Tabs */}
      <div className="flex gap-1 mb-6 bg-gray-100 dark:bg-gray-800 rounded-lg p-1 w-fit">
        <button
          onClick={() => setTab('productos')}
          className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${
            tab === 'productos' ? 'bg-white dark:bg-gray-700 shadow-sm text-lumina-600' : 'text-gray-500 hover:text-gray-700'
          }`}
        >
          <Package className="w-4 h-4 inline mr-1.5" />Productos
        </button>
        <button
          onClick={() => setTab('bodega')}
          className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${
            tab === 'bodega' ? 'bg-white dark:bg-gray-700 shadow-sm text-lumina-600' : 'text-gray-500 hover:text-gray-700'
          }`}
        >
          <Warehouse className="w-4 h-4 inline mr-1.5" />Bodega
          {alertas.length > 0 && (
            <span className="ml-1.5 bg-red-500 text-white text-xs px-1.5 py-0.5 rounded-full">{alertas.length}</span>
          )}
        </button>
      </div>

      {error && (
        <div className="bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 text-sm p-3 rounded-lg mb-4">
          {error}
        </div>
      )}
      {success && (
        <div className="bg-green-50 dark:bg-green-900/20 text-green-600 dark:text-green-400 text-sm p-3 rounded-lg mb-4">
          {success}
        </div>
      )}

      {/* Productos Tab */}
      {tab === 'productos' && (<>
      {/* Formulario */}
      <AnimatePresence>
        {showForm && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            className="overflow-hidden mb-8"
          >
            <div className="card p-6">
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-lg font-bold">
                  {editando ? 'Editar Producto' : 'Nuevo Producto'}
                </h2>
                <button onClick={resetForm} className="p-2 hover:bg-gray-100 dark:hover:bg-gray-800 rounded-lg">
                  <X className="w-5 h-5" />
                </button>
              </div>
              <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium mb-1">Nombre</label>
                  <input
                    type="text"
                    value={form.nombre}
                    onChange={(e) => setForm({ ...form, nombre: e.target.value })}
                    required
                    className="input-field"
                    placeholder="Polera Deportiva..."
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Precio (CLP)</label>
                  <input
                    type="number"
                    value={form.precio}
                    onChange={(e) => setForm({ ...form, precio: e.target.value })}
                    required
                    min="1"
                    className="input-field"
                    placeholder="29990"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Categoria</label>
                  <select
                    value={form.categoria}
                    onChange={(e) => setForm({ ...form, categoria: e.target.value })}
                    className="input-field"
                  >
                    {CATEGORIAS.map((c) => (
                      <option key={c} value={c}>{c}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Marca</label>
                  <select
                    value={form.marca}
                    onChange={(e) => setForm({ ...form, marca: e.target.value })}
                    className="input-field"
                  >
                    {MARCAS.map((m) => (
                      <option key={m} value={m}>{m}</option>
                    ))}
                  </select>
                </div>
                <div className="md:col-span-2">
                  <label className="block text-sm font-medium mb-1">Descripcion</label>
                  <input
                    type="text"
                    value={form.descripcion}
                    onChange={(e) => setForm({ ...form, descripcion: e.target.value })}
                    className="input-field"
                    placeholder="Descripcion del producto..."
                  />
                </div>
                <div className="md:col-span-2">
                  <label className="block text-sm font-medium mb-1">URL Imagen (opcional)</label>
                  <input
                    type="text"
                    value={form.imagenUrl}
                    onChange={(e) => setForm({ ...form, imagenUrl: e.target.value })}
                    className="input-field"
                    placeholder="https://ejemplo.com/imagen.jpg"
                  />
                  {form.imagenUrl && (
                    <div className="mt-2">
                      {form.imagenUrl.includes('imgs.search.brave.com') && (
                        <p className="text-xs text-yellow-600 dark:text-yellow-400 mb-2">⚠️ Los links de Brave Search no funcionan. Usa la URL directa de la imagen (clic derecho → Copiar direccion de imagen).</p>
                      )}
                      <div className="w-24 h-24 rounded-lg overflow-hidden bg-gray-100 dark:bg-gray-700 border">
                        <img
                          src={form.imagenUrl}
                          alt="Preview"
                          className="w-full h-full object-cover"
                          onError={(e) => { e.target.src = `https://placehold.co/96x96/333/fff?text=Error`; }}
                        />
                      </div>
                    </div>
                  )}
                </div>
                <div className="md:col-span-2">
                  <button type="submit" className="btn-primary flex items-center gap-2">
                    <Save className="w-4 h-4" />
                    {editando ? 'Guardar Cambios' : 'Crear Producto'}
                  </button>
                </div>
              </form>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Tabla de productos */}
      {loading ? (
        <div className="text-center py-12 text-gray-500">Cargando productos...</div>
      ) : (
        <div className="card overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 dark:bg-gray-800/50">
                <tr>
                  <th className="text-left px-4 py-3 font-medium">Producto</th>
                  <th className="text-left px-4 py-3 font-medium">Categoria</th>
                  <th className="text-left px-4 py-3 font-medium">Marca</th>
                  <th className="text-right px-4 py-3 font-medium">Precio</th>
                  <th className="text-center px-4 py-3 font-medium">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100 dark:divide-gray-800">
                {productos.map((producto) => (
                  <tr key={producto.id} className="hover:bg-gray-50 dark:hover:bg-gray-800/30 transition-colors">
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-3">
                        <img
                          src={producto.imagenUrl || `https://placehold.co/40x40/333/fff?text=${producto.nombre?.charAt(0)}`}
                          alt={producto.nombre}
                          className="w-10 h-10 rounded-lg object-cover"
                          onError={(e) => { e.target.src = `https://placehold.co/40x40/333/fff?text=${producto.nombre?.charAt(0)}`; }}
                        />
                        <div>
                          <p className="font-medium">{producto.nombre}</p>
                          <p className="text-xs text-gray-500 truncate max-w-[200px]">{producto.descripcion}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-gray-600 dark:text-gray-400">{producto.categoria}</td>
                    <td className="px-4 py-3 text-gray-600 dark:text-gray-400">{producto.marca?.nombre || producto.marca || '-'}</td>
                    <td className="px-4 py-3 text-right font-medium">{formatPrice(producto.precio)}</td>
                    <td className="px-4 py-3">
                      <div className="flex items-center justify-center gap-2">
                        <button
                          onClick={() => handleEdit(producto)}
                          className="p-1.5 rounded-lg hover:bg-blue-50 dark:hover:bg-blue-900/20 text-blue-600 transition-colors"
                          title="Editar"
                        >
                          <Pencil className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleDelete(producto.id)}
                          className="p-1.5 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20 text-red-600 transition-colors"
                          title="Eliminar"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {productos.length === 0 && (
            <div className="text-center py-12 text-gray-500">
              <Package className="w-12 h-12 mx-auto mb-3 text-gray-300" />
              <p>No hay productos. Crea el primero.</p>
            </div>
          )}
        </div>
      )}
      </>)}

      {/* Bodega Tab */}
      {tab === 'bodega' && (
        <div className="space-y-6">
          {/* Alertas */}
          {alertas.length > 0 && (
            <div className="card p-4 border-l-4 border-red-500">
              <h3 className="font-bold text-red-600 flex items-center gap-2 mb-3">
                <AlertTriangle className="w-5 h-5" /> Alertas de Stock Critico
              </h3>
              <div className="space-y-2">
                {alertas.map((a) => (
                  <div key={a.id} className="flex items-center justify-between bg-red-50 dark:bg-red-900/20 rounded-lg p-3 text-sm">
                    <span className="font-medium">{a.nombreProducto}</span>
                    <span className="text-red-600">Stock: {a.stockActual} / Min: {a.stockMinimo}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Ajustar Stock */}
          <div className="card p-6">
            <h3 className="font-bold mb-4">Ajustar Stock</h3>
            <form onSubmit={handleStockSubmit} className="flex flex-wrap items-end gap-4">
              <div>
                <label className="block text-sm font-medium mb-1">Producto</label>
                <select
                  value={stockForm.productoId}
                  onChange={(e) => setStockForm({ ...stockForm, productoId: e.target.value })}
                  required
                  className="input-field"
                >
                  <option value="">Seleccionar...</option>
                  {inventario.map((inv) => (
                    <option key={inv.productoId} value={inv.productoId}>{inv.nombreProducto} (Stock: {inv.stock})</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Cantidad</label>
                <input
                  type="number"
                  min="1"
                  value={stockForm.cantidad}
                  onChange={(e) => setStockForm({ ...stockForm, cantidad: e.target.value })}
                  required
                  className="input-field w-28"
                  placeholder="10"
                />
              </div>
              <div className="flex gap-2">
                <button
                  type="submit"
                  onClick={() => setStockAction('agregar')}
                  className="btn-primary flex items-center gap-1 text-sm"
                >
                  <ArrowUp className="w-4 h-4" /> Agregar
                </button>
                <button
                  type="submit"
                  onClick={() => setStockAction('descontar')}
                  className="px-4 py-2 rounded-lg font-medium text-sm bg-red-600 text-white hover:bg-red-700 transition-colors flex items-center gap-1"
                >
                  <ArrowDown className="w-4 h-4" /> Descontar
                </button>
              </div>
            </form>
          </div>

          {/* Tabla Inventario */}
          <div className="card overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="bg-gray-50 dark:bg-gray-800/50">
                  <tr>
                    <th className="text-left px-4 py-3 font-medium">Producto</th>
                    <th className="text-center px-4 py-3 font-medium">Stock</th>
                    <th className="text-center px-4 py-3 font-medium">Stock Minimo</th>
                    <th className="text-left px-4 py-3 font-medium">Ubicacion</th>
                    <th className="text-center px-4 py-3 font-medium">Estado</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100 dark:divide-gray-800">
                  {inventario.map((inv) => (
                    <tr key={inv.id} className="hover:bg-gray-50 dark:hover:bg-gray-800/30 transition-colors">
                      <td className="px-4 py-3 font-medium">{inv.nombreProducto}</td>
                      <td className="px-4 py-3 text-center font-bold">{inv.stock}</td>
                      <td className="px-4 py-3 text-center text-gray-500">{inv.stockMinimo}</td>
                      <td className="px-4 py-3 text-gray-600 dark:text-gray-400">{inv.ubicacionBodega || '-'}</td>
                      <td className="px-4 py-3 text-center">
                        {inv.stock <= inv.stockMinimo ? (
                          <span className="text-xs font-medium px-2.5 py-0.5 rounded-full bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400">Critico</span>
                        ) : (
                          <span className="text-xs font-medium px-2.5 py-0.5 rounded-full bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400">OK</span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            {inventario.length === 0 && (
              <div className="text-center py-12 text-gray-500">
                <Warehouse className="w-12 h-12 mx-auto mb-3 text-gray-300" />
                <p>No hay inventario registrado.</p>
              </div>
            )}
          </div>

          {/* Init stock for products without inventory */}
          {productos.filter(p => !inventario.find(i => i.productoId === p.id)).length > 0 && (
            <div className="card p-4">
              <h3 className="font-bold mb-3 text-sm">Productos sin inventario</h3>
              <div className="flex flex-wrap gap-2">
                {productos.filter(p => !inventario.find(i => i.productoId === p.id)).map(p => (
                  <button
                    key={p.id}
                    onClick={() => handleInitStock(p)}
                    className="text-xs px-3 py-1.5 rounded-lg border hover:bg-lumina-50 dark:hover:bg-lumina-900/20 transition-colors"
                  >
                    + Crear inventario: {p.nombre}
                  </button>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default AdminPage;
