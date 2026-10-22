import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Search, Filter, ShoppingCart, Loader2 } from 'lucide-react';
import { useCart } from '../context/CartContext';
import { productosService } from '../services/api';

function ProductosPage() {
  console.log('🔥 PRODUCTOS PAGE SE MONTÓ');
  const [productos, setProductos] = useState([]);
  const [search, setSearch] = useState('');
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const { addItem } = useCart();

  useEffect(() => {
    const cargar = async () => {
      try {
        const res = await productosService.listar();
        const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
        const mapped = data.map((p) => ({
          id: p.id,
          nombre: p.nombre,
          descripcion: p.descripcion,
          marca: p.marca?.nombre || p.categoria || '',
          precio: p.precio,
          imagen: p.imagenUrl,
          categoria: p.categoria,
        }));
        setProductos(mapped);
        setFilteredProducts(mapped);
      } catch (err) {
        console.error('Error cargando productos:', err);
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, []);

  useEffect(() => {
    const filtered = productos.filter(
      (p) =>
        p.nombre.toLowerCase().includes(search.toLowerCase()) ||
        (p.marca || '').toLowerCase().includes(search.toLowerCase())
    );
    setFilteredProducts(filtered);
  }, [search, productos]);

  const formatPrice = (price) => {
    return new Intl.NumberFormat('es-CL', {
      style: 'currency',
      currency: 'CLP',
    }).format(price);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <motion.div
        initial={{ opacity: 0, y: -10 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex flex-col sm:flex-row items-center gap-4 mb-8"
      >
        <h1 className="text-3xl font-bold">Productos</h1>
        <div className="flex-1 w-full sm:max-w-md ml-auto">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar productos o marcas..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="input-field pl-10"
            />
          </div>
        </div>
      </motion.div>

      {loading && (
        <div className="text-center py-20">
          <Loader2 className="w-8 h-8 mx-auto animate-spin text-lumina-600" />
          <p className="text-gray-500 mt-3">Cargando productos...</p>
        </div>
      )}

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredProducts.map((product, index) => (
          <motion.div
            key={product.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.05 }}
            className="card overflow-hidden group"
          >
            <div className="aspect-[4/3] overflow-hidden bg-gray-100 dark:bg-gray-700">
              <img
                src={product.imagen}
                alt={product.nombre}
                className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                onError={(e) => { e.target.src = `https://placehold.co/400x300/333/fff?text=${encodeURIComponent(product.nombre)}`; }}
              />
            </div>
            <div className="p-5">
              <span className="text-xs font-medium text-lumina-600 dark:text-lumina-400 uppercase tracking-wider">
                {product.marca}
              </span>
              <h3 className="font-semibold text-lg mt-1">{product.nombre}</h3>
              <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">{product.categoria}</p>
              <div className="flex items-center justify-between mt-4">
                <span className="text-xl font-bold text-lumina-700 dark:text-lumina-300">
                  {formatPrice(product.precio)}
                </span>
                <button
                  onClick={() => addItem(product)}
                  className="flex items-center gap-2 btn-primary text-sm py-2 px-4"
                >
                  <ShoppingCart className="w-4 h-4" />
                  Agregar
                </button>
              </div>
            </div>
          </motion.div>
        ))}
      </div>

      {filteredProducts.length === 0 && (
        <div className="text-center py-20 text-gray-500">
          <Filter className="w-12 h-12 mx-auto mb-4 opacity-50" />
          <p className="text-lg">No se encontraron productos</p>
        </div>
      )}
    </div>
  );
}

export default ProductosPage;
