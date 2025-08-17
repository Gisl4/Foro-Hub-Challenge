# 🧠 ForoHub API

[![Java](https://img.shields.io/badge/Java-17-007396.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F.svg)](https://spring.io/projects/spring-boot)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-6BA539.svg)](#-documentacion-openapi)
[![MySQL](https://img.shields.io/badge/MySQL-8.x-005C84.svg)](https://www.mysql.com/)
[![License: Proprietary](https://img.shields.io/badge/License-Proprietary-blue.svg)](#-licencia)

API RESTful construida con **Spring Boot 3.3** para gestionar **tópicos** y **respuestas** de un foro técnico.
Incluye **autenticación JWT**, control de acceso con **Spring Security**, persistencia con **JPA/Hibernate** y migraciones **Flyway**.
Documentación interactiva con **Swagger UI**.

---

## 📚 Tabla de contenidos
- [Características](#-características)
- [Arquitectura y seguridad](#-arquitectura-y-seguridad)
- [Documentación OpenAPI](#-documentación-openapi)
- [Endpoints](#-endpoints)
- [Modelos y payloads](#-modelos-y-payloads)
- [Arranque rápido](#-arranque-rápido)
- [Base de datos](#-base-de-datos)
- [Pruebas recomendadas](#-pruebas-recomendadas)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Roadmap](#-roadmap)
- [Licencia](#-licencia)
- [Autoría](#-autoría)

---

## ✅ Características
- 🔐 **JWT** con expiración de 2 horas (Issuer: `ForoHub`) y cabecera `Authorization: Bearer <token>`.
- 🧰 **Spring Security** con filtro personalizado para validar token y usuario.
- 📘 **Swagger / OpenAPI 3** habilitado.
- 🗃️ **JPA + Hibernate** y migraciones **Flyway** versionadas.
- 📦 Perfiles de configuración **DEV** y **PROD** (variables de entorno).

---

## 🛡️ Arquitectura y seguridad

**Flujo de autenticación**
1. `POST /login` con `login` y `password`.
2. Si las credenciales son válidas, se genera y retorna un **JWT**.
3. Con el token, invoca recursos protegidos con `Authorization: Bearer <token>`.

**Códigos de estado habituales**
- `401 Unauthorized` → token ausente, inválido o expirado.
- `403 Forbidden` → autenticado pero sin permisos suficientes.
- `409 Conflict` → evitar duplicados al crear un tópico con mismo título y mensaje.
- `404 Not Found` → recurso inexistente (p. ej. tópico no encontrado).

---

## 📘 Documentación OpenAPI
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **Esquema**: `http://localhost:8080/v3/api-docs`
- Seguridad definida con esquema **Bearer JWT** (Authorize una sola vez).

---

## 🧩 Endpoints

### Autenticación
| Endpoint   | Método | Público | Descripción                           |
|------------|:------:|:-------:|---------------------------------------|
| `/login`   | POST   |   ✅    | Autentica al usuario y devuelve JWT   |

### Tópicos
| Endpoint                       | Método | Seguridad  | Descripción |
|--------------------------------|:------:|------------|-------------|
| `/topicos`                     | POST   | Protegido  | Crear nuevo tópico |
| `/topicos`                     | GET    | Protegido  | Listar tópicos (paginado) |
| `/topicos/filtrar`            | GET    | Protegido  | Filtrar por `curso` y `año` |
| `/topicos/{id}`              | GET    | Protegido  | Detalle de un tópico |
| `/topicos/{id}`              | PUT    | Protegido  | Actualizar tópico |
| `/topicos/{id}`              | DELETE | Protegido  | Eliminar tópico |
| `/topicos/{id}/respuestas`   | GET    | Protegido  | Listar respuestas del tópico |

### Respuestas
| Endpoint        | Método | Seguridad  | Descripción               |
|-----------------|:------:|------------|---------------------------|
| `/respuestas`   | POST   | Protegido  | Registrar una respuesta   |

> **Nota:** Rutas públicas: `/login`, `/swagger-ui/**`, `/v3/api-docs/**` (según el filtro de seguridad).

---

## 🧱 Modelos y payloads

### Login (request) → Token (response)
**POST** `/login`
```json
{
  "login": "tu_usuario",
  "password": "tu_password"
}
```
**200 OK**
```json
{
  "jwTtoken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Crear tópico
**POST** `/topicos`
```json
{
  "titulo": "Error al compilar proyecto",
  "mensaje": "Me falla el build en Maven con X error",
  "status": "ABIERTO",
  "autor": 1,
  "curso": 1
}
```

### Actualizar tópico
**PUT** `/topicos/{id}`
```json
{
  "titulo": "Build falla en CI",
  "mensaje": "Detalle del error actualizado",
  "status": "EN_PROCESO"
}
```

### Filtrar por curso y año
**GET** `/topicos/filtrar?curso=Spring%20Boot&a%C3%B1o=2025`

### Registrar respuesta
**POST** `/respuestas`
```json
{
  "topicoId": 1,
  "mensaje": "¿Probaste limpiar el caché de Maven?"
}
```

### Listar tópicos (paginado)
**GET** `/topicos?page=0&size=10&sort=fechaCreacion,desc`

---

## 🚀 Arranque rápido

### Requisitos
- Java 17
- Maven 3.9+
- MySQL 8.x

### Configuración (DEV)
Crea/ajusta `src/main/resources/application.properties`:
```properties
spring.application.name=ForoHub
spring.datasource.url=jdbc:mysql://localhost:3306/forohub_db
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.error.include-stacktrace=never
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true

# JWT
api.security.secret=${JWT_SECRET:123456}
```

> También puedes exportar `JWT_SECRET` como variable de entorno.

### Correr
```bash
mvn clean spring-boot:run
# Abre: http://localhost:8080/swagger-ui/index.html
```

### Producción (ejemplo)
`src/main/resources/application-PROD.properties` usa variables:
```
spring.datasource.url=${JDBC_DATABASE_URL}
spring.datasource.username=${JDBC_DATABASE_USERNAME}
spring.datasource.password=${JDBC_DATABASE_PASSWORD}
```

---

## 🗄️ Base de datos
- Tablas: `topicos`, `usuarios`, `curso`, `respuestas`.
- Migraciones Flyway: `src/main/resources/db/migration/`
  - `V1__create_table_topicos.sql`
  - `V2__create_table_usuarios.sql`
  - `V3__create_table_curso.sql`
  - `V4__create-table_respuestas.sql`

> **Usuarios:** las contraseñas deben almacenarse con **BCrypt** (el proyecto usa `BCryptPasswordEncoder`).

---

## 🧪 Pruebas recomendadas
- ✅ Login válido devuelve token.
- 🚫 Acceso a `/topicos` sin token → `401`.
- ⏳ Token expirado (2h) → `401`.
- 🧾 Crear tópico duplicado (mismo título + mensaje) → `409`.
- 🔐 Acceso a `/topicos/{id}` inexistente → `404`.
- 🧪 Probar todo desde **Swagger UI** y **cURL**.

---

## 🗂️ Estructura del proyecto
```
src/
 ├─ main/
 │   ├─ java/com/aluraCursos/ForoHub/
 │   │   ├─ controller/      # Autenticación, Topicos, Respuestas
 │   │   ├─ domain/
 │   │   │   ├─ curso/       # Entidad Curso
 │   │   │   ├─ topico/      # Entidad Topico
 │   │   │   ├─ usuario/     # Entidad Usuario (UserDetails)
 │   │   │   └─ dto/         # Records DTO
 │   │   ├─ infra/
 │   │   │   ├─ errores/     # Manejo de errores
 │   │   │   ├─ security/    # JWT, Filtros, Config
 │   │   │   └─ springdoc/   # Configuración OpenAPI
 │   │   └─ repository/      # Repositorios JPA
 │   └─ resources/
 │       ├─ application.properties
 │       └─ db/migration/    # Scripts Flyway
 └─ test/                    # Pruebas
```

---

## 🗺️ Roadmap
-  Refresh tokens.
-  Administración de usuarios/roles vía endpoints.
-  Tests de integración (RestAssured / Testcontainers).
-  Perfil Docker (compose: app + MySQL).

---

## 📄 Licencia
Este proyecto es **propietario (todos los derechos reservados)**. **No se conceden permisos** para usar, copiar, modificar, distribuir ni sublicenciar el código, salvo autorización previa y por escrito de la autora.

Para solicitar permiso de uso, abre un issue o contacta a la autora.

## ✍️ Autoría
Realizado por:  **Gisell López**.
