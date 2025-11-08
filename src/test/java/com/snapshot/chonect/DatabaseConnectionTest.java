package com.snapshot.chonect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

public class DatabaseConnectionTest {

    @Test
    public void testMySQLConnection() {
        String url = "jdbc:mysql://tramway.proxy.rlwy.net:14151/railway";
        String username = "root";
        String password = "hXJsrTioWMTdGiMUMzWStcjmpokfzpxN";
        
        System.out.println("=== TEST DE CONECTIVIDAD A BASE DE DATOS ===");
        System.out.println("URL: " + url);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password.substring(0, 3) + "***");
        
        try {
            Properties properties = new Properties();
            properties.setProperty("useSSL", "false");
            properties.setProperty("allowPublicKeyRetrieval", "true");
            properties.setProperty("serverTimezone", "UTC");
            
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ CONEXIÓN EXITOSA A LA BASE DE DATOS");
            System.out.println("✅ Base de datos disponible y credenciales correctas");
            
            // Probar ejecutar una consulta simple
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery("SELECT 1");
            if (resultSet.next()) {
                System.out.println("✅ Consulta de prueba ejecutada correctamente. Resultado: " + resultSet.getInt(1));
            }
            
            connection.close();
            
        } catch (SQLException e) {
            System.out.println("❌ ERROR DE CONEXIÓN A LA BASE DE DATOS");
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Message: " + e.getMessage());
            
            // Análisis del error
            if (e.getMessage().contains("Access denied")) {
                System.out.println("🔍 DIAGNÓSTICO: Credenciales incorrectas o usuario sin permisos");
            } else if (e.getMessage().contains("Unknown database")) {
                System.out.println("🔍 DIAGNÓSTICO: Base de datos 'railway' no existe");
            } else if (e.getMessage().contains("Communications link failure")) {
                System.out.println("🔍 DIAGNÓSTICO: Problema de conectividad de red o base de datos pausada");
            } else if (e.getMessage().contains("Connection refused")) {
                System.out.println("🔍 DIAGNÓSTICO: Puerto cerrado o servicio no disponible");
            }
        }
    }
}
