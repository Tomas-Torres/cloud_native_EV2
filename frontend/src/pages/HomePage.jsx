import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { ShoppingBag, Truck, CreditCard, Shield } from 'lucide-react';

const features = [
  {
    icon: ShoppingBag,
    title: 'Catálogo Completo',
    description: 'Miles de productos de las mejores marcas a tu alcance.',
  },
  {
    icon: Truck,
    title: 'Delivery en Tiempo Real',
    description: 'Sigue tu pedido en cada etapa hasta que llegue a tu puerta.',
  },
  {
    icon: CreditCard,
    title: 'Pago Seguro',
    description: 'Integración directa con MercadoPago para transacciones seguras.',
  },
  {
    icon: Shield,
    title: 'Compra Protegida',
    description: 'Tu información siempre protegida con la mejor seguridad.',
  },
];

function HomePage() {
  return (
    <div>
      {/* Hero */}
      <section className="relative overflow-hidden bg-gradient-to-br from-lumina-600 to-lumina-900 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24 md:py-32">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
            className="max-w-2xl"
          >
            <h1 className="text-4xl md:text-6xl font-bold leading-tight">
              Bienvenido a{' '}
              <span className="text-lumina-200">Lumina</span>
            </h1>
            <p className="mt-6 text-lg md:text-xl text-lumina-100 leading-relaxed">
              Tu tienda retail favorita, ahora más rápida y confiable gracias a nuestra
              arquitectura de microservicios.
            </p>
            <div className="mt-10 flex flex-wrap gap-4">
              <Link to="/productos" className="bg-white text-lumina-700 font-semibold py-3 px-8 rounded-lg hover:bg-lumina-50 transition-all shadow-lg hover:shadow-xl active:scale-95">
                Ver Productos
              </Link>
              <Link to="/registro" className="border-2 border-white/30 text-white font-semibold py-3 px-8 rounded-lg hover:bg-white/10 transition-all">
                Crear Cuenta
              </Link>
            </div>
          </motion.div>
        </div>
        <div className="absolute top-0 right-0 w-1/2 h-full bg-gradient-to-l from-lumina-500/20 to-transparent pointer-events-none" />
      </section>

      {/* Features */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
        <motion.h2
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          viewport={{ once: true }}
          className="text-3xl font-bold text-center mb-12"
        >
          ¿Por qué elegirnos?
        </motion.h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {features.map((feature, index) => (
            <motion.div
              key={feature.title}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ delay: index * 0.1 }}
              className="card p-6 text-center hover:shadow-lg hover:-translate-y-1 transition-all duration-300"
            >
              <div className="inline-flex items-center justify-center w-14 h-14 rounded-xl bg-lumina-100 dark:bg-lumina-900/30 mb-4">
                <feature.icon className="w-7 h-7 text-lumina-600" />
              </div>
              <h3 className="font-semibold text-lg mb-2">{feature.title}</h3>
              <p className="text-sm text-gray-500 dark:text-gray-400">{feature.description}</p>
            </motion.div>
          ))}
        </div>
      </section>
    </div>
  );
}

export default HomePage;
