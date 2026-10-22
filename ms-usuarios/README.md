# ms-usuarios

Microservicio de **gestion de usuarios** de la Tienda Retail Lumina: registro, autenticacion con JWT y consulta de perfiles.

## Stack

- Java 17, Spring Boot 3.2
- Spring Data JPA / Hibernate
- Spring Security + JWT (jjwt)
- MySQL 8.0 (produccion) / H2 (pruebas)
- JaCoCo (cobertura)

## Configuracion

| Parametro | Valor |
|-----------|-------|
| Puerto | 8081 |
| Base de datos | db_usuarios (MySQL puerto 3307) |
| Esquema persistencia | JPA / Hibernate (`ddl-auto=update`) |

Variables de entorno (perfil `docker`):

```
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-usuarios:3306/db_usuarios
SPRING_DATASOURCE_USERNAME=lumina_user
SPRING_DATASOURCE_PASSWORD=lumina_pass
```

## Endpoints (API REST)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | `/api/usuarios/registro` | Registra un nuevo usuario |
| POST | `/api/usuarios/login` | Autentica y devuelve un token JWT |
| GET | `/api/usuarios/{id}` | Obtiene el perfil de un usuario |

Documentacion interactiva (Swagger): http://localhost:8081/swagger-ui/index.html

## Ejecucion

### Con Docker (recomendado, desde la raiz del proyecto)

```bash
docker-compose up -d ms-usuarios
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
