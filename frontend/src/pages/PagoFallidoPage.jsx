import { motion } from 'framer-motion';
import { XCircle, RefreshCw } from 'lucide-react';
import { Link } from 'react-router-dom';

function PagoFallidoPage() {
  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4">
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        className="card p-10 text-center max-w-md"
      >
        <motion.div
          initial={{ scale: 0 }}
          animate={{ scale: 1 }}
          transition={{ delay: 0.2, type: 'spring', stiffness: 200 }}
        >
          <XCircle className="w-20 h-20 mx-auto text-red-500 mb-4" />
        </motion.div>
        <h1 className="text-2xl font-bold mb-2">Pago Fallido</h1>
        <p className="text-gray-500 dark:text-gray-400 mb-6">
          Hubo un problema al procesar tu pago. Por favor intenta nuevamente o usa otro método de pago.
        </p>
        <div className="flex flex-col sm:flex-row gap-3 justify-center">
          <Link to="/carrito" className="btn-primary flex items-center justify-center gap-2">
            <RefreshCw className="w-4 h-4" />
            Reintentar Pago
          </Link>
          <Link to="/" className="btn-secondary">
            Volver al Inicio
          </Link>
        </div>
      </motion.div>
    </div>
  );
}

export default PagoFallidoPage;
