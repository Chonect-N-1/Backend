package com.snapshot.chonect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MySQL9ConnectionTest {

    @Test
    public void testMySQL9Connection() {
        String url = "jdbc:mysql://tramway.proxy.rlwy.net:14151/railway";
        String username = "root";
        String password = "hXJsrTioWMTdGiMUMzWStcjmpokfzpxN";
        
        System.out.println("=== TEST DE CONECTIVIDAD MYSQL 9.5.0 ===");
        System.out.println("URL: " + url);
        System.out.println("Username: " + username);
        System.out.println("Driver: " + com.mysql.cj.jdbc.Driver.class.getName());
        
        try {
            // Configuraciones específicas para MySQL 9.x
            Properties properties = new Properties();
            properties.setProperty("user", username);
            properties.setProperty("password", password);
            properties.setProperty("useSSL", "false");
            properties.setProperty("allowPublicKeyRetrieval", "true");
            properties.setProperty("serverTimezone", "UTC");
            properties.setProperty("connectTimeout", "30000");
            properties.setProperty("socketTimeout", "30000");
            properties.setProperty("useUnicode", "true");
            properties.setProperty("characterEncoding", "utf8");
            properties.setProperty("zeroDateTimeBehavior", "CONVERT_TO_NULL");
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("failOverReadOnly", "false");
            properties.setProperty("maxReconnects", "3");
            properties.setProperty("initialTimeout", "2");
            
            System.out.println("Configuraciones aplicadas:");
            System.out.println("- SSL: false");
            System.out.println("- Allow Public Key Retrieval: true");
            System.out.println("- Server Timezone: UTC");
            System.out.println("- Connect Timeout: 30s");
            System.out.println("- Socket Timeout: 30s");
            
            Connection connection = DriverManager.getConnection(url, properties);
            System.out.println("✅ CONEXIÓN EXITOSA A MYSQL 9.5.0");
            
            // Obtener información de la versión
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery("SELECT VERSION()");
            if (resultSet.next()) {
                String version = resultSet.getString(1);
                System.out.println("✅ Versión MySQL: " + version);
            }
            
            // Probar consulta simple
            var testQuery = statement.executeQuery("SELECT 1 as test_value");
            if (testQuery.next()) {
                System.out.println("✅ Consulta de prueba exitosa. Resultado: " + testQuery.getInt("test_value"));
            }
            
            // Probar conexión a tabla de sistema
            var tablesQuery = statement.executeQuery("SHOW TABLES");
            int tableCount = 0;
            while (tablesQuery.next()) {
                tableCount++;
            }
            System.out.println("✅ Conexión a base de datos exitosa. Tablas encontradas: " + tableCount);
            
            connection.close();
            System.out.println("✅ Conexión cerrada correctamente");
            
        } catch (SQLException e) {
            System.out.println("❌ ERROR DE CONEXIÓN A MYSQL 9.5.0");
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Message: " + e.getMessage());
            
            // Diagnóstico específico para MySQL 9.x
            if (e.getMessage().contains("Communications link failure")) {
                System.out.println("🔍 DIAGNÓSTICO: Posible causa MySQL 9.5.0:");
                System.out.println("  1. Base de datos aún inicializándose");
                System.out.println("  2. Proxy de Railway cambió");
                System.out.println("  3. Configuraciones MySQL 9.x requeridas");
            } else if (e.getMessage().contains("Access denied")) {
                System.out.println("🔍 DIAGNÓSTICO: Credenciales incorrectas o usuario sin permisos");
            } else if (e.getMessage().contains("Unknown database")) {
                System.out.println("🔍 DIAGNÓSTICO: Base de datos 'railway' no existe");
            } else if (e.getMessage().contains("Connection refused")) {
                System.out.println("🔍 DIAGNÓSTICO: Puerto cerrado o servicio no disponible");
            }
            
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ ERROR INESPERADO:");
            e.printStackTrace();
        }
    }
}
