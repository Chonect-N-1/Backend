# Multi-stage build optimizado
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copiar archivos de configuración primero (mejor cache)
COPY pom.xml ./
COPY src/main/resources/application.properties ./src/main/resources/

# Descargar dependencias (se cachea si no cambia pom.xml)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Construir aplicación
RUN mvn clean package -DskipTests -B

# Etapa de producción con JRE Alpine (más pequeño)
FROM eclipse-temurin:21-jre-alpine AS runtime

# Crear usuario no-root por seguridad
RUN addgroup -g 1001 -S appuser && \
    adduser -S appuser -u 1001 -G appuser

WORKDIR /app

# Crear directorios necesarios
RUN mkdir -p /app/config /app/logs && \
    chown -R appuser:appuser /app

# Copiar JAR desde etapa de build
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/src/main/resources/application.properties /app/config/application.properties

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT}/actuator/health || exit 1

# Variables de entorno con valores por defecto seguros
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Variables de entorno para JWT (se sobreescribirán por Render)
ENV SECURITY_JWT_EXPIRATION_TIME=3600000
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Cambiar propietario de archivos
RUN chown -R appuser:appuser /app

# Cambiar a usuario no-root
USER appuser

# Exponer puerto
EXPOSE ${PORT}

# Comando de inicio optimizado
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.config.location=file:/app/config/application.properties --server.port=${PORT}"]
