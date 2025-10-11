# 🚀 Despliegue con Docker - Chonect Backend

Este proyecto incluye configuración completa de Docker para facilitar el despliegue de la aplicación Spring Boot conectada a la base de datos MySQL en Railway. Está optimizado tanto para desarrollo local como para despliegue en plataformas cloud como **Render**.

## 📋 Prerrequisitos

### Para desarrollo local:
- Docker instalado
- Docker Compose instalado
- Conexión a internet (para descargar imágenes base)

### Para Render:
- Cuenta en [Render](https://render.com)
- Base de datos MySQL en Railway
- Archivo `.env` con las credenciales de la base de datos

## 🏗️ Arquitectura

- **Aplicación**: Spring Boot (Java 17)
- **Base de datos**: MySQL en Railway (externa)
- **Puerto**: Dinámico (10000 por defecto en Render)
- **Configuración**: Variables de entorno para producción

## 🚀 Despliegue Rápido

### Opción 1: Render (Producción)

1. **Crear servicio Web en Render**:
   - Conecta tu repositorio de GitHub
   - Selecciona "Web Service"
   - Usa la configuración de `render.yaml`

2. **Configurar variables de entorno en Render**:
   ```bash
   SPRING_PROFILES_ACTIVE=prod
   SPRING_DATASOURCE_URL=jdbc:mysql://crossover.proxy.rlwy.net:16755/railway
   SPRING_DATASOURCE_USERNAME=root
   SPRING_DATASOURCE_PASSWORD=UHHTHmYdBNRdADMpLpcrDSMVhDBwJfCE
   SPRING_JPA_HIBERNATE_DDL_AUTO=update
   SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQLDialect
   ```

3. **Deploy automático**: Render construirá y desplegará automáticamente

### Opción 2: Desarrollo local con Docker

```bash
# 1. Construir la aplicación
mvn clean package -DskipTests

# 2. Construir la imagen de Docker
docker build -t chonect-backend:latest .

# 3. Desplegar con Docker Compose
docker-compose up -d
```

### Opción 3: Script automático (Linux/Mac)

```bash
./docker-deploy.sh
```

## 🌐 Acceso a la aplicación

Una vez desplegada, la aplicación estará disponible en:

- **Aplicación principal**: http://localhost:8082
- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **GraphQL Playground**: http://localhost:8082/graphiql
- **Estado de salud**: http://localhost:8082/actuator/health

## 📋 Comandos útiles

```bash
# Ver logs en tiempo real
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f chonect-backend

# Reiniciar la aplicación
docker-compose restart

# Detener la aplicación
docker-compose down

# Ver estado de los contenedores
docker-compose ps

# Ver imágenes de Docker
docker images

# Limpiar contenedores detenidos
docker-compose down --remove-orphans
```

## 🔧 Configuración

### Variables de entorno

El archivo `docker-compose.yml` incluye las siguientes variables de entorno para la configuración de producción:

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=prod
  - SPRING_DATASOURCE_URL=jdbc:mysql://crossover.proxy.rlwy.net:16755/railway
  - SPRING_DATASOURCE_USERNAME=root
  - SPRING_DATASOURCE_PASSWORD=UHHTHmYdBNRdADMpLpcrDSMVhDBwJfCE
  - SPRING_JPA_HIBERNATE_DDL_AUTO=update
  - SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQLDialect
```

### Configuración de la aplicación

- **Puerto**: 8082 (configurable en `application.properties`)
- **Base de datos**: MySQL en Railway
- **JPA**: Hibernate con DDL automático en modo `update`
- **Health Check**: Endpoint `/actuator/health` cada 30 segundos

## 🔍 Solución de problemas

### Problema: Puerto ya en uso
```bash
# Ver qué proceso usa el puerto 8082
netstat -ano | findstr :8082

# O alternativamente, cambiar el puerto en docker-compose.yml
# Cambiar "8082:8082" por "8083:8082"
```

### Problema: Error de conexión a la base de datos
1. Verificar que las credenciales de Railway sean correctas
2. Verificar que la base de datos esté accesible desde tu IP
3. Revisar los logs: `docker-compose logs -f`

### Problema: La aplicación no inicia
```bash
# Ver logs detallados
docker-compose logs -f chonect-backend

# Revisar estado del contenedor
docker-compose ps
```

## 📁 Archivos incluidos

- `Dockerfile`: Configuración multi-etapa para optimizar la imagen
- `docker-compose.yml`: Orquestación de servicios
- `.dockerignore`: Archivos excluidos del contexto de construcción
- `docker-deploy.sh`: Script automático de despliegue (Linux/Mac)
- `DOCKER_README.md`: Esta documentación

## 🔒 Seguridad

⚠️ **Importante**: Las credenciales de la base de datos están expuestas en el archivo `docker-compose.yml`. Para producción, considera:

- Usar Docker Secrets
- Variables de entorno externas
- Archivo `.env` (no incluido en control de versiones)

## 📊 Monitoreo

La aplicación incluye Spring Boot Actuator para monitoreo:

- `/actuator/health`: Estado de salud
- `/actuator/info`: Información de la aplicación
- `/actuator/metrics`: Métricas

## 🛑 Detención

Para detener completamente la aplicación:

```bash
docker-compose down
```

Para detener y eliminar también la imagen:

```bash
docker-compose down --rmi all
```

## 🚀 Despliegue en Render (Guía detallada)

### Paso 1: Preparar el repositorio

1. **Sube tu código a GitHub**:
   ```bash
   git add .
   git commit -m "Add Docker configuration for Render deployment"
   git push origin main
   ```

2. **Verifica que estos archivos estén en tu repositorio**:
   - `Dockerfile` ✅
   - `render.yaml` ✅
   - `.env.example` ✅
   - `src/main/resources/application.properties` ✅

### Paso 2: Crear servicio en Render

1. Ve a [Render Dashboard](https://dashboard.render.com)
2. Haz clic en "New +" → "Web Service"
3. Conecta tu repositorio de GitHub
4. Configura el servicio:
   - **Name**: `chonect-backend`
   - **Environment**: `Java`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/*.jar --spring.config.location=file:src/main/resources/application.properties`

### Paso 3: Configurar variables de entorno en Render

Ve a tu servicio en Render y agrega estas variables de entorno:

| Variable | Valor | Descripción |
|----------|-------|-------------|
| `SPRING_PROFILES_ACTIVE` | `prod` | Perfil de producción |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://crossover.proxy.rlwy.net:16755/railway` | URL de Railway |
| `SPRING_DATASOURCE_USERNAME` | `root` | Usuario de la BD |
| `SPRING_DATASOURCE_PASSWORD` | `UHHTHmYdBNRdADMpLpcrDSMVhDBwJfCE` | Contraseña de la BD |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Actualización automática de esquema |
| `SPRING_JPA_DATABASE_PLATFORM` | `org.hibernate.dialect.MySQLDialect` | Dialecto MySQL |

### Paso 4: Desplegar

1. Haz clic en "Create Web Service"
2. Render construirá automáticamente la imagen Docker
3. Una vez completado, tu aplicación estará disponible en la URL proporcionada por Render

### Paso 5: Verificar el despliegue

- **URL de la aplicación**: `https://tu-app.onrender.com`
- **Health Check**: `https://tu-app.onrender.com/actuator/health`
- **Swagger UI**: `https://tu-app.onrender.com/swagger-ui.html`

### Características de Render:

✅ **Despliegue automático**: Se activa con cada push a main
✅ **Base de datos externa**: Compatible con Railway MySQL
✅ **Escalado automático**: De 0 a 1 instancia
✅ **Logs en tiempo real**: Disponibles en el dashboard
✅ **SSL automático**: Certificados incluidos
✅ **Dominio personalizado**: Opcional

### Comandos útiles en Render:

```bash
# Ver logs en Render Dashboard
# Reinicio automático en cada deploy
# Monitoreo incluido en el dashboard
```

### Solución de problemas en Render:

1. **Error de conexión a BD**:
   - Verifica las credenciales en Railway
   - Asegúrate de que la BD acepte conexiones externas

2. **Build fallido**:
   - Revisa los logs de construcción en Render
   - Verifica que Maven pueda descargar dependencias

3. **Aplicación no responde**:
   - Revisa los logs en el dashboard de Render
   - Verifica el health check endpoint

---

🎉 ¡Felicitaciones! Tu aplicación Chonect Backend está ahora desplegada con Docker y conectada a la base de datos MySQL en Railway.
