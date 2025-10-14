# Etapa 1: Compilar la app con Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final con solo el jar
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Crear directorio para configuración externa
RUN mkdir -p /app/config

# Copiar archivo de configuración
COPY src/main/resources/application.properties /app/config/application.properties

# Usar variable de entorno PORT de Render (por defecto 10000)
ENV PORT=10000

# Variables de entorno para configuración de correo
ENV SPRING_MAIL_HOST=${SPRING_MAIL_HOST}
ENV SPRING_MAIL_PORT=${SPRING_MAIL_PORT}
ENV SPRING_MAIL_USERNAME=${SPRING_MAIL_USERNAME}
ENV SPRING_MAIL_PASSWORD=${SPRING_MAIL_PASSWORD}
ENV SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=${SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH}
ENV SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=${SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE}

EXPOSE ${PORT}

# Comando para Render - usa configuración externa y puerto dinámico
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/app/config/application.properties", "--server.port=${PORT}"]
