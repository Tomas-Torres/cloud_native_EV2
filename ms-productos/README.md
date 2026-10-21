# ms-productos

Microservicio de **catalogo de productos y marcas** de la Tienda Retail Lumina.

## Stack

- Java 17, Spring Boot 3.2
- Spring Data JPA / Hibernate
- MySQL 8.0 (produccion) / H2 (pruebas)
- JaCoCo (cobertura)

## Configuracion

| Parametro | Valor |
|-----------|-------|
| Puerto | 8082 |
| Base de datos | db_productos (MySQL puerto 3308) |
| Esquema persistencia | JPA / Hibernate (`ddl-auto=update`) |

Variables de entorno (perfil `docker`):

```
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-productos:3306/db_productos
SPRING_DATASOURCE_USERNAME=lumina_user
SPRING_DATASOURCE_PASSWORD=lumina_pass
```

## Endpoints (API REST)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/api/productos` | Lista los productos activos |
| GET | `/api/productos/{id}` | Obtiene un producto por ID |
| GET | `/api/productos/buscar?q=` | Busca productos por nombre |
| GET | `/api/productos/marcas` | Lista las marcas |
| POST | `/api/productos` | Crea un producto |
| PUT | `/api/productos/{id}` | Actualiza un producto |
| DELETE | `/api/productos/{id}` | Elimina (logico) un producto |

Documentacion interactiva (Swagger): http://localhost:8082/swagger-ui/index.html

## Ejecucion

### Con Docker (recomendado, desde la raiz del proyecto)

```bash
docker-compose up -d ms-productos
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
