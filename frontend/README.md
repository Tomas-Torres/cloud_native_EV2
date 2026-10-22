# Frontend - Tienda Retail Lumina

Aplicacion web SPA (Single Page Application) de la tienda retail Lumina, construida con React + Vite.

## Stack Tecnologico

| Tecnologia | Uso |
|-----------|-----|
| React 18 | Libreria de UI |
| Vite 5 | Build tool / dev server |
| Tailwind CSS 3 | Estilos |
| React Router DOM 6 | Enrutamiento SPA |
| Axios | Cliente HTTP hacia el BFF Gateway |
| Lucide React | Iconos |
| Framer Motion | Animaciones |
| Cypress | Pruebas E2E |

## Estructura

```
frontend/
├── public/             # Recursos estaticos (imagenes de productos, etc.)
├── src/
│   ├── components/     # Componentes reutilizables
│   ├── context/        # Estado global (auth, carrito)
│   ├── pages/          # Vistas (catalogo, carrito, login, admin, etc.)
│   ├── services/       # api.js - cliente Axios y servicios por dominio
│   ├── App.jsx         # Rutas principales
│   └── main.jsx        # Punto de entrada
├── cypress/            # Pruebas end-to-end
├── package.json
└── vite.config.js
```

## Requisitos Previos

- **Node.js** v18 o superior
- El **backend** corriendo (ver README principal): el frontend consume el BFF Gateway en `http://localhost:8080/api`.

## Instalacion

```bash
cd frontend
npm install
```

## Ejecucion

```bash
npm run dev
```

La aplicacion queda disponible en **http://localhost:5173**.

## Scripts Disponibles

| Script | Descripcion |
|--------|-------------|
| `npm run dev` | Levanta el servidor de desarrollo (Vite) |
| `npm run build` | Genera el build de produccion en `dist/` |
| `npm run preview` | Sirve el build de produccion localmente |
| `npm run cypress:open` | Abre Cypress en modo interactivo |
| `npm run cypress:run` | Ejecuta las pruebas E2E en consola |

## Pruebas E2E (Cypress)

Con el frontend y el backend corriendo:

```bash
npm run cypress:run
```

Las pruebas cubren los flujos de productos, carrito, pagos, delivery, bodega,
usuarios y el flujo completo de compra (`cypress/e2e/`).

## Configuracion del API

La URL base del backend se define en `src/services/api.js`:

```js
const api = axios.create({ baseURL: 'http://localhost:8080/api' });
```

## Credenciales de Admin

| Campo | Valor |
|-------|-------|
| Email | admin@lumina.cl |
| Password | admin123 |
