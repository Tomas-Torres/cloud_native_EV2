import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Mail, Lock, User, MapPin, CreditCard } from 'lucide-react';
import { authService } from '../services/api';

function RegistroPage() {
  const [form, setForm] = useState({
    nombre: '',
    email: '',
    password: '',
    confirmPassword: '',
    run: '',
    direccion: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const formatRun = (value) => {
    const clean = value.replace(/[^0-9kK]/g, '').toLowerCase();
    return clean.slice(0, 9);
  };

  const handleRunChange = (e) => {
    setForm({ ...form, run: formatRun(e.target.value) });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (form.run.length < 8 || form.run.length > 9) {
      setError('El RUT debe tener entre 8 y 9 caracteres (ej: 21575345k)');
      return;
    }

    if (form.password.length < 8 || form.password.length > 20) {
      setError('La contrasena debe tener entre 8 y 20 caracteres');
      return;
    }

    if (form.password !== form.confirmPassword) {
      setError('Las contrasenas no coinciden');
      return;
    }

    setLoading(true);
    setSuccess('');
    try {
      const { confirmPassword, ...registroData } = form;
      const response = await authService.registro(registroData);
      console.log('Registro response:', response.status, response.data);
      setSuccess('Cuenta creada exitosamente. Redirigiendo al login...');
      setLoading(false);
      setTimeout(() => navigate('/login'), 1500);
      return;
    } catch (err) {
      console.error('Registro error:', err.response?.status, err.response?.data);
      let msg = '';
      const errData = err.response?.data;
      if (typeof errData === 'string') {
        try { msg = JSON.parse(errData)?.message || errData; } catch { msg = errData; }
      } else {
        msg = errData?.message;
      }

      if (err.code === 'ERR_NETWORK') {
        setError('No se pudo conectar al servidor. Verifica que el backend esté corriendo.');
      } else {
        setError(msg || 'Error al crear la cuenta. Intenta de nuevo.');
      }
    }
    setLoading(false);
  };

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4 py-8">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="card p-8 w-full max-w-md"
      >
        <h1 className="text-2xl font-bold text-center mb-2">Crear Cuenta</h1>
        <p className="text-sm text-gray-500 text-center mb-8">
          Únete a Lumina y empieza a comprar
        </p>

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

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1.5">Nombre completo</label>
            <div className="relative">
              <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="text"
                name="nombre"
                value={form.nombre}
                onChange={handleChange}
                placeholder="Juan Pérez"
                required
                className="input-field pl-10"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium mb-1.5">RUN</label>
            <div className="relative">
              <CreditCard className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="text"
                name="run"
                value={form.run}
                onChange={handleRunChange}
                placeholder="21575345k"
                required
                maxLength={9}
                className="input-field pl-10"
              />
            </div>
            <p className="text-xs text-gray-400 mt-1">Solo numeros y digito verificador, sin puntos ni guion</p>
          </div>

          <div>
            <label className="block text-sm font-medium mb-1.5">Email</label>
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                placeholder="tu@email.com"
                required
                className="input-field pl-10"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium mb-1.5">Dirección</label>
            <div className="relative">
              <MapPin className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="text"
                name="direccion"
                value={form.direccion}
                onChange={handleChange}
                placeholder="Av. Ejemplo 123, Santiago"
                required
                className="input-field pl-10"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium mb-1.5">Contraseña</label>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="password"
                name="password"
                value={form.password}
                onChange={handleChange}
                placeholder="••••••••"
                required
                minLength={8}
                maxLength={20}
                className="input-field pl-10"
              />
            </div>
            <p className="text-xs text-gray-400 mt-1">Minimo 8 caracteres, maximo 20</p>
          </div>

          <div>
            <label className="block text-sm font-medium mb-1.5">Confirmar Contraseña</label>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="password"
                name="confirmPassword"
                value={form.confirmPassword}
                onChange={handleChange}
                placeholder="••••••••"
                required
                className="input-field pl-10"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="btn-primary w-full disabled:opacity-50"
          >
            {loading ? 'Creando cuenta...' : 'Crear Cuenta'}
          </button>
        </form>

        <p className="text-sm text-center mt-6 text-gray-500">
          ¿Ya tienes cuenta?{' '}
          <Link to="/login" className="text-lumina-600 font-medium hover:underline">
            Inicia sesión
          </Link>
        </p>
      </motion.div>
    </div>
  );
}

export default RegistroPage;
