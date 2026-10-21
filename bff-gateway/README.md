# bff-gateway

**Backend For Frontend / API Gateway** de la Tienda Retail Lumina. Es el unico punto
de entrada del frontend (puerto 8080) y orquesta/redirige las peticiones hacia los
microservicios internos mediante `WebClient`.

## Stack

- Java 17, Spring Boot 3.2
- Spring WebFlux (`WebClient`) + Spring Web (MVC)
- Resilience4j (Circuit Breaker reactivo)
- springdoc-openapi (Swagger UI)
- JaCoCo (cobertura)

## Circuit Breaker (Resilience4j)

Todas las llamadas del gateway hacia los microservicios estan protegidas por un
**circuit breaker** (uno por microservicio), centralizado en
`CircuitBreakerHelper`. Esto evita que un microservicio caido sature al BFF y
permite degradar el servicio de forma controlada.

Comportamiento:

- Si un microservicio **no responde** (conexion rechazada / timeout), el BFF
  devuelve `503 Service Unavailable` con un mensaje JSON de fallback.
- Tras varios fallos consecutivos, el circuito pasa a estado **OPEN** y las
  siguientes peticiones fallan rapido (sin esperar al microservicio) hasta que
  el circuito vuelve a **HALF_OPEN** y se recupera automaticamente.
- Los **errores HTTP del microservicio** (4xx/5xx) NO abren el circuito: indican
  que el servicio esta vivo y se propagan tal cual.

Parametros (en `application.yml`): ventana de 10 llamadas, umbral de fallo 50%,
10s en estado abierto, transicion automatica a half-open.

Estado de los circuitos (Actuator):

```
GET http://localhost:8080/actuator/health
GET http://localhost:8080/actuator/circuitbreakers
```

## Configuracion

| Parametro | Valor |
|-----------|-------|
| Puerto | 8080 |
| Persistencia | No aplica (el gateway no tiene base de datos) |

URLs de los microservicios (perfil `docker`):

```
MS_USUARIOS_URL=http://ms-usuarios:8081
MS_PRODUCTOS_URL=http://ms-productos:8082
MS_BODEGA_URL=http://ms-bodega:8083
MS_CARRITO_URL=http://ms-carrito:8084
MS_DELIVERY_URL=http://ms-delivery:8085
MS_PAGOS_URL=http://ms-pagos:8086
```

## Rutas expuestas (proxy)

Todas las rutas se exponen bajo `/api/*` y se redirigen al microservicio correspondiente:

| Prefijo | Microservicio destino |
|---------|----------------------|
| `/api/usuarios/**` | ms-usuarios |
| `/api/productos/**` | ms-productos |
| `/api/bodega/**` | ms-bodega |
| `/api/carrito/**` | ms-carrito |
| `/api/delivery/**` | ms-delivery |
| `/api/pagos/**` | ms-pagos |

Documentacion interactiva (Swagger): http://localhost:8080/swagger-ui/index.html

## Ejecucion

### Con Docker (recomendado, desde la raiz del proyecto)

```bash
docker-compose up -d bff-gateway
```

### Local (requiere Maven y los microservicios corriendo)

```bash
mvn spring-boot:run
```

## Pruebas y Cobertura

```bash
mvn test
```

El reporte de cobertura JaCoCo se genera en:

```
target/site/jacoco/index.html
```
