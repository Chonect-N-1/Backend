# 🏢 Backend - Sistema de Autenticación y Gestión de Usuarios

## 📌 Descripción General

Este proyecto implementa un **backend empresarial** desarrollado en **Spring Boot**, orientado a la gestión de autenticación y registro de usuarios con **seguridad basada en JWT (JSON Web Tokens)**.  

El sistema está diseñado bajo principios de **escalabilidad, seguridad y mantenibilidad**, asegurando que pueda integrarse fácilmente en aplicaciones modernas, ya sea como **API REST independiente** o como parte de una **arquitectura de microservicios**.

---

## ⚙️ Características Principales

- **Autenticación segura con JWT** (JSON Web Token).
- **Registro de usuarios** con asignación de roles por defecto.
- **Roles predefinidos**:
  - `CUSTOMER`
  - `ADMIN`
- **Validación de datos de entrada** mediante anotaciones de `Jakarta Validation`.
- **Manejo centralizado de excepciones** para respuestas claras y estructuradas.
- **Arquitectura modular**, siguiendo principios de **Domain-Driven Design (DDD)**.
- **Compatibilidad con CORS**, facilitando la comunicación con frontends en React, Angular u otros frameworks.

---

## 🏗️ Arquitectura del Proyecto

La estructura se organiza en paquetes separados por responsabilidades:

---

## 🌐 Rutas Locales (Desarrollo)

- **Base URL local**
  - `http://localhost:8080`

- **Swagger / Documentación**
  - UI: `http://localhost:8080/swagger-ui/index.html`
  - OpenAPI JSON: `http://localhost:8080/v3/api-docs`

- **Autenticación** (`/api/v1/auth`)
  - `POST /api/v1/auth/login`
    - Body JSON:

      ```json
      {
        "email": "tu_email@dominio.com",
        "password": "tu_password"
      }
      ```

    - Respuesta (ejemplo):

      ```json
      { "token": "<JWT>" }
      ```

  - `POST /api/v1/auth/register`
    - Body JSON (ejemplo):

      ```json
      {
        "username": "jdoe",
        "password": "Secret123*",
        "email": "jdoe@correo.com",
        "fullName": "John Doe",
        "birthDate": "1990-01-01"
      }
      ```

- **Usuarios** (`/api/v1/user`)
  - Protegido por JWT (añadir header `Authorization: Bearer <TOKEN>`)
  - Endpoints base (según tu `UserController` y controladores básicos):
    - `GET /api/v1/user/{id}`
    - `POST /api/v1/user`
    - `PUT /api/v1/user/{id}`
    - `DELETE /api/v1/user/{id}`

### Ejemplos con curl

- Login y guardar token en variable de entorno (PowerShell):

  ```powershell
  $response = Invoke-RestMethod -Method POST -Uri "http://localhost:8080/api/v1/auth/login" -ContentType "application/json" -Body '{
    "email": "tu_email@dominio.com",
    "password": "tu_password"
  }'
  $env:TOKEN = $response.token
  ```

- GET protegido (User por id):

  ```bash
  curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/user/1
  ```

- Crear usuario (requiere permisos si tu servicio lo aplica):

  ```bash
  curl -X POST http://localhost:8080/api/v1/user \
       -H "Authorization: Bearer $TOKEN" \
       -H "Content-Type: application/json" \
       -d '{
            "username": "nuevo",
            "password": "Secret123*",
            "email": "nuevo@correo.com",
            "fullName": "Nuevo Usuario",
            "birthDate": "2000-01-01"
          }'
  ```

---

## 🌍 Rutas en Producción

- **Base URL producción**
  - `https://chonect-n-1.onrender.com`

- **Swagger / Documentación**
  - UI: `https://chonect-n-1.onrender.com/swagger-ui/index.html`
  - OpenAPI JSON: `https://chonect-n-1.onrender.com/v3/api-docs`

---

## 🔐 Seguridad y CORS

- **JWT requerido** en todas las rutas excepto:
  - `/api/v1/auth/**`
  - `/swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**`

- **CORS permitido** (ver `SecurityConfig` y `corsConfigurationSource`):
  - `http://localhost:5173`
  - `https://chonect-n-1.onrender.com`

Si necesitas una ruta pública de salud, puedes exponer `GET /api/v1/health` y añadir `permitAll()` a esa ruta.

---

## ▶️ Ejecución Local Rápida

- Maven (local):

  ```bash
  mvn spring-boot:run
  ```

- Docker (local):

  ```bash
  docker build -t carrlox/chonect-n-1:1.0.1 .
  docker run --rm -p 8080:8080 --name chonect-n-1 carrlox/chonect-n-1:1.0.1
  ```
