import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { CheckCircle, Package, AlertTriangle, Truck } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';

function PagoExitosoPage() {
  const navigate = useNavigate();
  const [countdown, setCountdown] = useState(10);
  const ordenId = localStorage.getItem('lumina-last-orden') || 'ORD-DEMO';

  useEffect(() => {
    const timer = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          navigate(`/delivery/${ordenId}`);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
    return () => clearInterval(timer);
  }, [navigate]);

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4">
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        className="card p-10 text-center max-w-md"
      >
        <div className="bg-yellow-50 dark:bg-yellow-900/20 border border-yellow-200 dark:border-yellow-800 rounded-lg p-3 mb-6 flex items-center gap-2 justify-center">
          <AlertTriangle className="w-4 h-4 text-yellow-600" />
          <span className="text-sm font-medium text-yellow-700 dark:text-yellow-400">
            Modo Simulacion - No se realizo un cobro real
          </span>
        </div>

        <motion.div
          initial={{ scale: 0 }}
          animate={{ scale: 1 }}
          transition={{ delay: 0.2, type: 'spring', stiffness: 200 }}
        >
          <CheckCircle className="w-20 h-20 mx-auto text-green-500 mb-4" />
        </motion.div>
        <h1 className="text-2xl font-bold mb-2">Pago Exitoso!</h1>
        <p className="text-gray-500 dark:text-gray-400 mb-2">
          Tu pago ha sido procesado correctamente (simulacion).
        </p>
        <p className="text-sm text-gray-400 mb-6">
          Redirigiendo en <span className="font-bold text-lumina-600">{countdown}</span> segundos...
        </p>
        <Link
          to={`/delivery/${ordenId}`}
          className="btn-primary w-full flex items-center justify-center gap-2 mb-3"
        >
          <Truck className="w-4 h-4" />
          Seguir mi Pedido
        </Link>
        <div className="flex flex-col sm:flex-row gap-3 justify-center">
          <Link to="/" className="btn-secondary flex items-center justify-center gap-2">
            Volver al Inicio
          </Link>
          <Link to="/productos" className="btn-secondary flex items-center justify-center gap-2">
            <Package className="w-4 h-4" />
            Seguir Comprando
          </Link>
        </div>
      </motion.div>
    </div>
  );
}

export default PagoExitosoPage;
