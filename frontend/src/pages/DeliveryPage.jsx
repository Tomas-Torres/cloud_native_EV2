import { useState, useEffect, useMemo } from 'react';
import { useParams, Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Package, Truck, CheckCircle, MapPin, ShoppingBag, Clock } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

function generarDelivery(ordenId) {
  const ahora = new Date();
  const hace1h = new Date(ahora.getTime() - 60 * 60 * 1000);
  const fmt = (d) => d.toLocaleString('es-CL', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });

  return {
    id: Math.floor(Math.random() * 1000) + 1,
    ordenId,
    estado: 'reparto',
    direccion: 'Av. Providencia 1234, Santiago',
    fechaEstimada: new Date(ahora.getTime() + 2 * 24 * 60 * 60 * 1000).toLocaleDateString('es-CL'),
    pasos: [
      { estado: 'preparando', label: 'Pedido Confirmado', completado: true, fecha: fmt(hace1h), descripcion: 'Tu pedido fue recibido y confirmado' },
      { estado: 'recoleccion', label: 'En Bodega', completado: true, fecha: fmt(new Date(ahora.getTime() - 30 * 60 * 1000)), descripcion: 'Productos recolectados y empaquetados' },
      { estado: 'reparto', label: 'En Reparto', completado: true, fecha: fmt(ahora), descripcion: 'Tu pedido va en camino' },
      { estado: 'finalizado', label: 'Entregado', completado: false, fecha: null, descripcion: 'Esperando entrega en destino' },
    ],
  };
}

function getDeliveryKey(ordenId) {
  return `lumina-delivery-${ordenId}`;
}

function DeliveryPage() {
  const { user } = useAuth();
  const { ordenId } = useParams();
  const [delivery, setDelivery] = useState(() => {
    const saved = localStorage.getItem(getDeliveryKey(ordenId));
    if (saved) return JSON.parse(saved);
    const nuevo = generarDelivery(ordenId);
    localStorage.setItem(getDeliveryKey(ordenId), JSON.stringify(nuevo));
    return nuevo;
  });
  const [simulando, setSimulando] = useState(false);

  const simularEntrega = () => {
    setSimulando(true);
    setTimeout(() => {
      setDelivery((prev) => {
        const updated = {
          ...prev,
          estado: 'finalizado',
          pasos: prev.pasos.map((p) =>
            p.estado === 'finalizado'
              ? { ...p, completado: true, fecha: new Date().toLocaleString('es-CL', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' }), descripcion: 'Pedido entregado exitosamente' }
              : p
          ),
        };
        localStorage.setItem(getDeliveryKey(ordenId), JSON.stringify(updated));
        // Actualizar estado en la lista de pedidos
        const pedidosKey = `lumina-pedidos-${user?.id || 'guest'}`;
        const pedidos = JSON.parse(localStorage.getItem(pedidosKey) || '[]');
        const idx = pedidos.findIndex((p) => p.ordenId === ordenId);
        if (idx !== -1) {
          pedidos[idx].estado = 'finalizado';
          localStorage.setItem(pedidosKey, JSON.stringify(pedidos));
        }
        return updated;
      });
      setSimulando(false);
    }, 2000);
  };

  const getIcon = (estado) => {
    switch (estado) {
      case 'preparando': return ShoppingBag;
      case 'recoleccion': return Package;
      case 'reparto': return Truck;
      case 'finalizado': return CheckCircle;
      default: return Package;
    }
  };

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }}>
        <h1 className="text-3xl font-bold mb-2">Seguimiento de Envio</h1>
        <p className="text-gray-500 dark:text-gray-400 mb-8">
          Orden: <span className="font-mono font-medium">{ordenId}</span>
        </p>

        {/* Info cards */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-8">
          <div className="card p-5 flex items-center gap-3">
            <MapPin className="w-5 h-5 text-lumina-600 flex-shrink-0" />
            <div>
              <p className="text-sm text-gray-500">Direccion de entrega</p>
              <p className="font-medium">{delivery.direccion}</p>
            </div>
          </div>
          <div className="card p-5 flex items-center gap-3">
            <Clock className="w-5 h-5 text-lumina-600 flex-shrink-0" />
            <div>
              <p className="text-sm text-gray-500">Fecha estimada</p>
              <p className="font-medium">{delivery.fechaEstimada}</p>
            </div>
          </div>
        </div>

        {/* Timeline */}
        <div className="space-y-0">
          {delivery.pasos.map((paso, index) => {
            const Icon = getIcon(paso.estado);
            const isLast = index === delivery.pasos.length - 1;

            return (
              <motion.div
                key={paso.estado}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: index * 0.15 }}
                className="flex gap-4"
              >
                <div className="flex flex-col items-center">
                  <div
                    className={`w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0 ${
                      paso.completado
                        ? 'bg-lumina-600 text-white'
                        : 'bg-gray-200 dark:bg-gray-700 text-gray-400'
                    }`}
                  >
                    <Icon className="w-5 h-5" />
                  </div>
                  {!isLast && (
                    <div
                      className={`w-0.5 h-16 ${
                        paso.completado
                          ? 'bg-lumina-600'
                          : 'bg-gray-200 dark:bg-gray-700'
                      }`}
                    />
                  )}
                </div>
                <div className="pt-1.5 pb-8">
                  <p className={`font-semibold ${paso.completado ? 'text-lumina-600' : 'text-gray-400'}`}>
                    {paso.label}
                  </p>
                  <p className="text-sm text-gray-500 mt-0.5">{paso.descripcion}</p>
                  {paso.fecha && (
                    <p className="text-xs text-gray-400 mt-1">{paso.fecha}</p>
                  )}
                  {!paso.completado && (
                    <p className="text-xs text-yellow-500 font-medium mt-1">Pendiente</p>
                  )}
                </div>
              </motion.div>
            );
          })}
        </div>

        {/* Acciones */}
        <div className="mt-6 flex flex-col sm:flex-row gap-3">
          {delivery.estado !== 'finalizado' && (
            <button
              onClick={simularEntrega}
              disabled={simulando}
              className="btn-primary flex items-center justify-center gap-2 disabled:opacity-50"
            >
              {simulando ? (
                <>
                  <Truck className="w-4 h-4 animate-bounce" />
                  Simulando entrega...
                </>
              ) : (
                <>
                  <Truck className="w-4 h-4" />
                  Simular Entrega (Demo)
                </>
              )}
            </button>
          )}
          <Link to="/productos" className="btn-secondary flex items-center justify-center gap-2">
            <ShoppingBag className="w-4 h-4" />
            Seguir Comprando
          </Link>
        </div>
      </motion.div>
    </div>
  );
}

export default DeliveryPage;
