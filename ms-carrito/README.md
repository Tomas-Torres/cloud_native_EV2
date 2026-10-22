# ms-carrito

Microservicio de **carrito de compras por usuario** de la Tienda Retail Lumina.

## Stack

- Java 17, Spring Boot 3.2
- Spring Data JPA / Hibernate
- MySQL 8.0 (produccion) / H2 (pruebas)
- JaCoCo (cobertura)

## Configuracion

| Parametro | Valor |
|-----------|-------|
| Puerto | 8084 |
| Base de datos | db_carrito (MySQL puerto 3310) |
| Esquema persistencia | JPA / Hibernate (`ddl-auto=update`) |

Variables de entorno (perfil `docker`):

```
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-carrito:3306/db_carrito
SPRING_DATASOURCE_USERNAME=lumina_user
SPRING_DATASOURCE_PASSWORD=lumina_pass
```

## Endpoints (API REST)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/api/carrito/{usuarioId}` | Obtiene el carrito de un usuario |
| POST | `/api/carrito/{usuarioId}/agregar` | Agrega un producto al carrito |
| DELETE | `/api/carrito/{usuarioId}/eliminar/{productoId}` | Elimina un producto del carrito |
| DELETE | `/api/carrito/{usuarioId}/vaciar` | Vacia el carrito |

Documentacion interactiva (Swagger): http://localhost:8084/swagger-ui/index.html

## Ejecucion

### Con Docker (recomendado, desde la raiz del proyecto)

```bash
docker-compose up -d ms-carrito
```

### Local (requiere Maven y MySQL)

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
