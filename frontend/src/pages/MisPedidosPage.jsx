import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Package, Truck, CheckCircle, Clock, ShoppingBag, ChevronRight } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

function MisPedidosPage() {
  const { user } = useAuth();
  const [pedidos, setPedidos] = useState([]);

  useEffect(() => {
    const pedidosKey = `lumina-pedidos-${user?.id || 'guest'}`;
    const stored = JSON.parse(localStorage.getItem(pedidosKey) || '[]');
    setPedidos(stored.sort((a, b) => b.timestamp - a.timestamp));
  }, [user]);

  const getEstadoConfig = (estado) => {
    switch (estado) {
      case 'preparando':
        return { label: 'Preparando', color: 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400', icon: Clock };
      case 'reparto':
        return { label: 'En Reparto', color: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400', icon: Truck };
      case 'finalizado':
        return { label: 'Entregado', color: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400', icon: CheckCircle };
      default:
        return { label: 'Pendiente', color: 'bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-400', icon: Package };
    }
  };

  const formatDate = (timestamp) => {
    return new Date(timestamp).toLocaleString('es-CL', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const formatPrice = (price) => {
    return new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(price);
  };

  if (pedidos.length === 0) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-20 text-center">
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
          <Package className="w-16 h-16 mx-auto mb-4 text-gray-300 dark:text-gray-600" />
          <h2 className="text-2xl font-bold mb-2">No tienes pedidos aun</h2>
          <p className="text-gray-500 mb-6">Cuando realices una compra, podras ver tus pedidos aqui</p>
          <Link to="/productos" className="btn-primary">
            Ver Productos
          </Link>
        </motion.div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <motion.div initial={{ opacity: 0, y: -10 }} animate={{ opacity: 1, y: 0 }}>
        <h1 className="text-3xl font-bold mb-8">Mis Pedidos</h1>
      </motion.div>

      <div className="space-y-4">
        {pedidos.map((pedido, index) => {
          const estadoConfig = getEstadoConfig(pedido.estado);
          const Icon = estadoConfig.icon;

          return (
            <motion.div
              key={pedido.ordenId}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.05 }}
            >
              <Link
                to={`/delivery/${pedido.ordenId}`}
                className="card p-5 flex items-center gap-4 hover:shadow-lg hover:-translate-y-0.5 transition-all duration-200 block"
              >
                <div className="w-12 h-12 rounded-full bg-lumina-100 dark:bg-lumina-900/30 flex items-center justify-center flex-shrink-0">
                  <Icon className="w-6 h-6 text-lumina-600" />
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-3 mb-1">
                    <p className="font-semibold truncate">{pedido.ordenId}</p>
                    <span className={`text-xs font-medium px-2.5 py-0.5 rounded-full ${estadoConfig.color}`}>
                      {estadoConfig.label}
                    </span>
                  </div>
                  <p className="text-sm text-gray-500">{formatDate(pedido.timestamp)}</p>
                  <p className="text-sm font-medium text-lumina-600 mt-0.5">
                    {pedido.items} {pedido.items === 1 ? 'producto' : 'productos'} - {formatPrice(pedido.total)}
                  </p>
                </div>
                <ChevronRight className="w-5 h-5 text-gray-400 flex-shrink-0" />
              </Link>
            </motion.div>
          );
        })}
      </div>
    </div>
  );
}

export default MisPedidosPage;
