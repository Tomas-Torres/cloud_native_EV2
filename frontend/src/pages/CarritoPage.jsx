import { useState } from 'react';
import { motion } from 'framer-motion';
import { Trash2, Plus, Minus, ShoppingBag, CreditCard, Loader2 } from 'lucide-react';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { pagosService, bodegaService } from '../services/api';
import { Link, useNavigate } from 'react-router-dom';

function CarritoPage() {
  const { items, removeItem, updateQuantity, totalItems, totalPrice, clearCart } = useCart();
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [pagando, setPagando] = useState(false);
  const [error, setError] = useState('');

  const handlePagar = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    setPagando(true);
    setError('');

    try {
      const ordenId = 'ORD-' + Date.now();
      const payload = {
        ordenId,
        usuarioId: user?.id || 1,
        items: items.map((item) => ({
          titulo: item.nombre,
          cantidad: item.cantidad,
          precioUnitario: Number(item.precio),
          descripcion: item.marca || 'Producto',
          imagenUrl: item.imagen || '',
        })),
      };

      const response = await pagosService.crearPreferencia(payload);
      const data = typeof response.data === 'string' ? JSON.parse(response.data) : response.data;

      const checkoutUrl = data.sandboxInitPoint || data.initPoint;
      if (checkoutUrl) {
        const pedido = { ordenId, estado: 'reparto', timestamp: Date.now(), items: items.length, total: totalPrice };
        const pedidosKey = `lumina-pedidos-${user?.id || 'guest'}`;
        const pedidos = JSON.parse(localStorage.getItem(pedidosKey) || '[]');
        pedidos.push(pedido);
        localStorage.setItem(pedidosKey, JSON.stringify(pedidos));
        localStorage.setItem('lumina-last-orden', ordenId);
        // Descontar stock de cada producto
        await Promise.all(items.map(item => bodegaService.descontarStock(item.id, item.cantidad)));
        clearCart();
        window.location.href = checkoutUrl;
      } else {
        setError('No se pudo obtener el enlace de pago. Verifica que el backend esté corriendo.');
      }
    } catch (err) {
      console.error('Error al crear preferencia:', err);
      if (err.code === 'ERR_NETWORK') {
        setError('El servidor de pagos no está disponible. Levanta el backend con: docker-compose up -d');
      } else {
        const msg = err.response?.data?.message || err.response?.data;
        setError(typeof msg === 'string' ? msg : 'Error al procesar el pago. Intenta de nuevo.');
      }
    } finally {
      setPagando(false);
    }
  };

  const formatPrice = (price) => {
    return new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(price);
  };

  if (items.length === 0) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-20 text-center">
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
          <ShoppingBag className="w-16 h-16 mx-auto mb-4 text-gray-300 dark:text-gray-600" />
          <h2 className="text-2xl font-bold mb-2">Tu carrito está vacío</h2>
          <p className="text-gray-500 mb-6">Agrega productos para empezar a comprar</p>
          <Link to="/productos" className="btn-primary">
            Ver Productos
          </Link>
        </motion.div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold mb-8">Carrito de Compras</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Items */}
        <div className="lg:col-span-2 space-y-4">
          {items.map((item) => (
            <motion.div
              key={item.id}
              layout
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: 20 }}
              className="card p-4 flex items-center gap-4"
            >
              <img
                src={item.imagen}
                alt={item.nombre}
                className="w-20 h-20 object-cover rounded-lg"
              />
              <div className="flex-1 min-w-0">
                <h3 className="font-semibold truncate">{item.nombre}</h3>
                <p className="text-sm text-gray-500">{item.marca}</p>
                <p className="font-bold text-lumina-600 mt-1">{formatPrice(item.precio)}</p>
              </div>
              <div className="flex items-center gap-2">
                <button
                  onClick={() => updateQuantity(item.id, item.cantidad - 1)}
                  className="p-1 rounded-md hover:bg-gray-100 dark:hover:bg-gray-700"
                >
                  <Minus className="w-4 h-4" />
                </button>
                <span className="w-8 text-center font-medium">{item.cantidad}</span>
                <button
                  onClick={() => updateQuantity(item.id, item.cantidad + 1)}
                  className="p-1 rounded-md hover:bg-gray-100 dark:hover:bg-gray-700"
                >
                  <Plus className="w-4 h-4" />
                </button>
              </div>
              <button
                onClick={() => removeItem(item.id)}
                className="p-2 text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors"
              >
                <Trash2 className="w-5 h-5" />
              </button>
            </motion.div>
          ))}
        </div>

        {/* Resumen */}
        <div className="card p-6 h-fit sticky top-24">
          <h2 className="font-bold text-lg mb-4">Resumen del Pedido</h2>
          <div className="space-y-3 text-sm">
            <div className="flex justify-between">
              <span className="text-gray-500">Productos ({totalItems})</span>
              <span>{formatPrice(totalPrice)}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-500">Envío</span>
              <span className="text-green-600 font-medium">Gratis</span>
            </div>
            <hr className="border-gray-200 dark:border-gray-700" />
            <div className="flex justify-between text-lg font-bold">
              <span>Total</span>
              <span className="text-lumina-600">{formatPrice(totalPrice)}</span>
            </div>
          </div>
          {error && (
            <div className="bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 text-sm p-3 rounded-lg mt-4">
              {error}
            </div>
          )}
          <button
            onClick={handlePagar}
            disabled={pagando}
            className="btn-primary w-full mt-6 flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {pagando ? (
              <>
                <Loader2 className="w-5 h-5 animate-spin" />
                Procesando...
              </>
            ) : (
              <>
                <CreditCard className="w-5 h-5" />
                Pagar con MercadoPago
              </>
            )}
          </button>
          <button
            onClick={async () => {
              if (!isAuthenticated) { navigate('/login'); return; }
              const demoOrdenId = 'ORD-DEMO-' + Date.now();
              const pedido = { ordenId: demoOrdenId, estado: 'reparto', timestamp: Date.now(), items: items.length, total: totalPrice };
              const pedidosKey = `lumina-pedidos-${user?.id || 'guest'}`;
              const pedidos = JSON.parse(localStorage.getItem(pedidosKey) || '[]');
              pedidos.push(pedido);
              localStorage.setItem(pedidosKey, JSON.stringify(pedidos));
              localStorage.setItem('lumina-last-orden', demoOrdenId);
              // Descontar stock de cada producto
              await Promise.all(items.map(item => bodegaService.descontarStock(item.id, item.cantidad)));
              clearCart();
              navigate('/pago/exitoso');
            }}
            className="w-full mt-3 text-sm py-2.5 px-4 rounded-lg font-medium border-2 border-yellow-400 bg-yellow-50 text-yellow-700 hover:bg-yellow-100 dark:bg-yellow-900/20 dark:text-yellow-400 dark:border-yellow-600 dark:hover:bg-yellow-900/40 transition-colors flex items-center justify-center gap-2"
          >
            <CreditCard className="w-4 h-4" />
            Simular Pago (Demo)
          </button>
          <button
            onClick={clearCart}
            className="btn-secondary w-full mt-3 text-sm"
          >
            Vaciar Carrito
          </button>
        </div>
      </div>
    </div>
  );
}

export default CarritoPage;
