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

