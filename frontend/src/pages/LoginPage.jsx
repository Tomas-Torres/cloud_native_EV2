import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Mail, Lock, Eye, EyeOff } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/api';
import { useMsal } from '@azure/msal-react';
import { adminLoginRequest } from '../config/authConfig';

function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const { login, loading, setLoading } = useAuth();
  const navigate = useNavigate();
  const { instance } = useMsal();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authService.login({ email, password });
      let data = response.data;
      if (typeof data === 'string') {
        try { data = JSON.parse(data); } catch (e) { /* keep as-is */ }
      }

      if (data?.token && data?.usuario) {
        login(data.usuario, data.token);
        navigate('/');
      } else {
        setError('Respuesta inesperada del servidor.');
      }
    } catch (err) {
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
        setError(msg || 'Credenciales incorrectas. Intenta de nuevo.');
      }
    } finally {
      setLoading(false);
    }
  };

  const decodeJwt = (token) => {
    try {
      const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      return JSON.parse(decodeURIComponent(atob(payload).split('').map(
        (c) => '%' + c.charCodeAt(0).toString(16).padStart(2, '0')
      ).join('')));
    } catch {
      return null;
    }
  };

  const handleAzureLogin = async () => {
    setError('');
    setLoading(true);
    try {
      const loginResult = await instance.loginPopup({ scopes: ['openid', 'profile'] });
      let tokenResult;
      try {
        tokenResult = await instance.acquireTokenSilent({ ...adminLoginRequest, account: loginResult.account });
      } catch {
        tokenResult = await instance.acquireTokenPopup(adminLoginRequest);
      }

      const claims = decodeJwt(tokenResult.accessToken);
      const roles = claims?.roles || [];
      const rol = roles.some((r) => r.toUpperCase() === 'ADMIN') ? 'ADMIN' : 'USER';

      const userData = {
        id: claims?.oid,
        nombre: claims?.name || loginResult.account.name,
        email: claims?.preferred_username || loginResult.account.username,
        rol,
      };

      login(userData, tokenResult.accessToken);
      navigate(rol === 'ADMIN' ? '/admin' : '/');
    } catch (err) {
      console.error('Error en login con Azure:', err);
      setError('No se pudo iniciar sesión con Microsoft. Intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="card p-8 w-full max-w-md"
      >
        <h1 className="text-2xl font-bold text-center mb-2">Iniciar Sesión</h1>
        <p className="text-sm text-gray-500 text-center mb-8">
          Ingresa a tu cuenta Lumina
        </p>

        {error && (
          <div className="bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 text-sm p-3 rounded-lg mb-4">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-sm font-medium mb-1.5">Email</label>
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="tu@email.com"
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
                type={showPassword ? 'text' : 'password'}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                required
                className="input-field pl-10 pr-10"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
              </button>
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="btn-primary w-full disabled:opacity-50"
          >
            {loading ? 'Ingresando...' : 'Ingresar'}
          </button>
        </form>

        <div className="relative my-6">
          <div className="absolute inset-0 flex items-center">
            <div className="w-full border-t border-gray-200 dark:border-gray-700" />
          </div>
          <div className="relative flex justify-center text-xs uppercase">
            <span className="px-2 bg-white dark:bg-gray-800 text-gray-400">o</span>
          </div>
        </div>

        <button
          type="button"
          onClick={handleAzureLogin}
          disabled={loading}
          className="btn-secondary w-full disabled:opacity-50"
        >
          Ingresar con Microsoft Azure
        </button>

        <p className="text-sm text-center mt-6 text-gray-500">
          ¿No tienes cuenta?{' '}
          <Link to="/registro" className="text-lumina-600 font-medium hover:underline">
            Regístrate aquí
          </Link>
        </p>
      </motion.div>
    </div>
  );
}

export default LoginPage;