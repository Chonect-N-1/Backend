package com.snapshot.chonect.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class DatabaseMigrationConfig {

    @Bean
    CommandLineRunner addVersionColumns(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                log.info("Verificando y agregando columnas 'version' si no existen...");

                // Agregar columna version a element
                executeAlterTable(jdbcTemplate, "element");

                // Agregar columna version a page
                executeAlterTable(jdbcTemplate, "page");

                // Agregar columna version a project
                executeAlterTable(jdbcTemplate, "project");

                // Agregar columna version a canvas
                executeAlterTable(jdbcTemplate, "canvas");

                // Agregar columna version a connection
                executeAlterTable(jdbcTemplate, "connection");

                log.info("✅ Migración de columnas 'version' completada exitosamente");
            } catch (Exception e) {
                log.error("Error durante la migración de columnas 'version': {}", e.getMessage());
                // No lanzamos la excepción para que la aplicación pueda iniciar
            }
        };
    }

    private void executeAlterTable(JdbcTemplate jdbcTemplate, String tableName) {
        try {
            // Verificar si la columna ya existe
            String checkColumnSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_SCHEMA = DATABASE() " +
                    "AND TABLE_NAME = ? " +
                    "AND COLUMN_NAME = 'version'";

            Integer count = jdbcTemplate.queryForObject(checkColumnSql, Integer.class, tableName);

            if (count == null || count == 0) {
                // La columna no existe, agregarla
                String alterTableSql = "ALTER TABLE " + tableName + " ADD COLUMN version BIGINT DEFAULT 0";
                jdbcTemplate.execute(alterTableSql);
                log.info("✅ Columna 'version' agregada a la tabla '{}'", tableName);
            } else {
                log.info("ℹ️ La columna 'version' ya existe en la tabla '{}'", tableName);
            }

            // SIEMPRE actualizar registros con version NULL a 0
            // Esto es necesario porque registros antiguos pueden tener NULL
            String updateSql = "UPDATE " + tableName + " SET version = 0 WHERE version IS NULL";
            int updated = jdbcTemplate.update(updateSql);
            if (updated > 0) {
                log.info("✅ {} registros con version NULL actualizados a 0 en la tabla '{}'", updated, tableName);
            }
        } catch (Exception e) {
            log.warn("⚠️ Error al procesar tabla '{}': {}", tableName, e.getMessage());
        }
    }
}
