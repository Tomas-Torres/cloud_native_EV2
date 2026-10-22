# Autenticación en el Gateway: JWT propio + Azure AD

Para cumplir el diagrama de arquitectura del profe (Front Clientes con JWT
propio, Front Admin con JWT de Azure AD, ambos contra el mismo Gateway), se
agregó a `bff-gateway` la capacidad de validar los dos tipos de token.

## Qué se agregó

- `pom.xml`: dependencias `spring-boot-starter-security` y
  `spring-boot-starter-oauth2-resource-server`.
- `src/main/java/com/lumina/bff/security/MultiIssuerJwtDecoder.java`: mira
  el claim `iss` del token (sin validarlo) para decidir si es un token
  propio (HS256, mismo secreto que `ms-usuarios`) o de Azure AD (RS256,
  validado contra las claves públicas del tenant vía JWKS). Recién ahí lo
  valida de verdad con el decoder que corresponde.
- `src/main/java/com/lumina/bff/security/JwtRoleConverter.java`: unifica el
  rol de admin venga de donde venga (`rol=ADMIN` en el token propio, o
  `roles=["Admin"]` en el de Azure App Roles) en la misma autoridad
  `ROLE_ADMIN` de Spring Security.
- `src/main/java/com/lumina/bff/config/SecurityConfig.java`: exige JWT
  válido en todas las rutas salvo `/actuator/**` y
  `/api/usuarios/login|registro`; y exige rol ADMIN en las rutas de
  escritura de productos y en todo `/api/bodega/**` (las que usa el Front
  Admin).
- `application.yml` / `docker-compose.yml`: variables nuevas
  `AZURE_TENANT_ID` y `AZURE_CLIENT_ID`.

## Qué falta / a validar

1. **Completar `AZURE_TENANT_ID` y `AZURE_CLIENT_ID`** en el entorno donde
   corra el Gateway (docker-compose o Azure), con los mismos valores del
   App Registration que usa `frontend-admin` (ver
   `frontend-admin/.env.example`). HECHO
2. **Confirmar que `JWT_SECRET` sea idéntico** entre `bff-gateway` y
   `ms-usuarios` (hoy comparten el mismo valor por defecto, pero si se
   cambia en uno hay que cambiarlo en el otro).
3. **Configurar App Roles en Azure AD** (App Registration -> App roles) y
   asignarle el rol admin a las cuentas que deban entrar al panel. Sin
   esto, el token de Azure no trae el claim `roles` y nadie con cuenta de
   Azure va a poder pasar las rutas protegidas con `hasRole("ADMIN")`.
4. Esto es un **borrador para revisar en equipo**, no un reemplazo del
   trabajo de Jorge en el Gateway: falta que existan los controllers/rutas
   reales hacia cada microservicio (`/api/productos`, `/api/bodega`, etc.)
   — hoy `bff-gateway` no tiene ninguno todavía.
5. No se tocó `ms-usuarios` ni el resto de microservicios.
