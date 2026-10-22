# ms-pagos

Microservicio de **procesamiento de pagos** integrado con MercadoPago para la Tienda Retail Lumina.

## Stack

- Java 17, Spring Boot 3.2
- Spring Data JPA / Hibernate
- MercadoPago SDK Java
- MySQL 8.0 (produccion) / H2 (pruebas)
- JaCoCo (cobertura)

## Configuracion

| Parametro | Valor |
|-----------|-------|
| Puerto | 8086 |
| Base de datos | db_pagos (MySQL puerto 3312) |
| Esquema persistencia | JPA / Hibernate (`ddl-auto=update`) |

Variables de entorno (perfil `docker`):

```
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-pagos:3306/db_pagos
SPRING_DATASOURCE_USERNAME=lumina_user
SPRING_DATASOURCE_PASSWORD=lumina_pass
MERCADOPAGO_ACCESS_TOKEN=<tu_access_token>
MERCADOPAGO_WEBHOOK_SECRET=<tu_webhook_secret>
```

> Las credenciales de MercadoPago se inyectan por variables de entorno y **no** se versionan.

## Endpoints (API REST)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | `/api/pagos/crear-preferencia` | Crea una preferencia de pago en MercadoPago |
| POST | `/api/pagos/notifications` | Webhook de notificaciones de MercadoPago |
| GET | `/api/pagos/{id}` | Obtiene un pago por ID |
| GET | `/api/pagos/orden/{ordenId}` | Lista los pagos de una orden |

Documentacion interactiva (Swagger): http://localhost:8086/swagger-ui/index.html

## Ejecucion

### Con Docker (recomendado, desde la raiz del proyecto)

```bash
docker-compose up -d ms-pagos
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
