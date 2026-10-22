# ms-bodega

Microservicio de **gestion de inventario, stock y alertas de stock critico** de la Tienda Retail Lumina.

## Stack

- Java 17, Spring Boot 3.2
- Spring Data JPA / Hibernate
- MySQL 8.0 (produccion) / H2 (pruebas)
- JaCoCo (cobertura)

## Configuracion

| Parametro | Valor |
|-----------|-------|
| Puerto | 8083 |
| Base de datos | db_bodega (MySQL puerto 3309) |
| Esquema persistencia | JPA / Hibernate (`ddl-auto=update`) |

Variables de entorno (perfil `docker`):

```
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-bodega:3306/db_bodega
SPRING_DATASOURCE_USERNAME=lumina_user
SPRING_DATASOURCE_PASSWORD=lumina_pass
```

## Endpoints (API REST)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/api/bodega/inventario` | Lista todo el inventario |
| GET | `/api/bodega/inventario/{productoId}` | Consulta el stock de un producto |
| POST | `/api/bodega/inventario` | Crea un registro de inventario |
| PATCH | `/api/bodega/inventario/{productoId}/agregar` | Agrega stock |
| PATCH | `/api/bodega/inventario/{productoId}/descontar` | Descuenta stock |
| GET | `/api/bodega/alertas` | Lista alertas de stock critico |

Documentacion interactiva (Swagger): http://localhost:8083/swagger-ui/index.html

## Ejecucion

### Con Docker (recomendado, desde la raiz del proyecto)

```bash
docker-compose up -d ms-bodega
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
