#!/bin/bash

# Script para construir y desplegar la aplicación Chonect con Docker

echo "🚀 Iniciando despliegue de Chonect Backend con Docker..."

# Verificar si Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker no está instalado. Por favor instala Docker primero."
    exit 1
fi

# Verificar si Docker Compose está instalado
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo "❌ Docker Compose no está instalado. Por favor instala Docker Compose primero."
    exit 1
fi

echo "📦 Construyendo la aplicación..."
# Construir la aplicación
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Error al construir la aplicación con Maven"
    exit 1
fi

echo "🐳 Construyendo imagen de Docker..."
# Construir la imagen
docker build -t carrlox/chonect-backend:latest .

if [ $? -ne 0 ]; then
    echo "❌ Error al construir la imagen de Docker"
    exit 1
fi

echo "🚀 Desplegando con Docker Compose..."
# Desplegar con Docker Compose
docker-compose up -d

if [ $? -ne 0 ]; then
    echo "❌ Error al desplegar con Docker Compose"
    exit 1
fi

echo "✅ ¡Despliegue completado exitosamente!"
echo ""
echo "🌐 La aplicación está disponible en: http://localhost:8082"
echo "📚 Swagger UI disponible en: http://localhost:8082/swagger-ui.html"
echo "🔍 Estado de salud: http://localhost:8082/actuator/health"
echo ""
echo "📋 Comandos útiles:"
echo "   docker-compose logs -f    # Ver logs en tiempo real"
echo "   docker-compose down       # Detener la aplicación"
echo "   docker-compose restart    # Reiniciar la aplicación"
