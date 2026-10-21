# ms-delivery

Microservicio de **seguimiento de entregas** con historial de estados de la Tienda Retail Lumina.

## Stack

- Java 17, Spring Boot 3.2
- Spring Data JPA / Hibernate
- MySQL 8.0 (produccion) / H2 (pruebas)
- JaCoCo (cobertura)

## Configuracion

| Parametro | Valor |
|-----------|-------|
| Puerto | 8085 |
| Base de datos | db_delivery (MySQL puerto 3311) |
| Esquema persistencia | JPA / Hibernate (`ddl-auto=update`) |

Variables de entorno (perfil `docker`):

```
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-delivery:3306/db_delivery
SPRING_DATASOURCE_USERNAME=lumina_user
SPRING_DATASOURCE_PASSWORD=lumina_pass
```

## Endpoints (API REST)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/api/delivery/{id}` | Obtiene un delivery por ID |
| GET | `/api/delivery/orden/{ordenId}` | Obtiene el delivery de una orden |
| POST | `/api/delivery` | Crea un delivery |
| PATCH | `/api/delivery/{id}/estado` | Actualiza el estado del delivery |

Estados posibles: `RECOLECCION`, `REPARTO`, `FINALIZADO` (con historial de cambios).

Documentacion interactiva (Swagger): http://localhost:8085/swagger-ui/index.html

## Ejecucion

### Con Docker (recomendado, desde la raiz del proyecto)

```bash
docker-compose up -d ms-delivery
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
