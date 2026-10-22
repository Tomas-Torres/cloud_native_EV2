import { Routes, Route } from 'react-router-dom';
import Layout from './components/layout/Layout';
import HomePage from './pages/HomePage';
import ProductosPage from './pages/ProductosPage';
import CarritoPage from './pages/CarritoPage';
import LoginPage from './pages/LoginPage';
import RegistroPage from './pages/RegistroPage';
import PagoExitosoPage from './pages/PagoExitosoPage';
import PagoFallidoPage from './pages/PagoFallidoPage';
import DeliveryPage from './pages/DeliveryPage';
import MisPedidosPage from './pages/MisPedidosPage';
import AdminPage from './pages/AdminPage';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<HomePage />} />
        <Route path="productos" element={<ProductosPage />} />
        <Route path="carrito" element={<CarritoPage />} />
        <Route path="login" element={<LoginPage />} />
        <Route path="registro" element={<RegistroPage />} />
        <Route path="pago/exitoso" element={<PagoExitosoPage />} />
        <Route path="pago/fallido" element={<PagoFallidoPage />} />
        <Route path="mis-pedidos" element={<MisPedidosPage />} />
        <Route path="admin" element={<AdminPage />} />
        <Route path="delivery/:ordenId" element={<DeliveryPage />} />
      </Route>
    </Routes>
  );
}

export default App;
