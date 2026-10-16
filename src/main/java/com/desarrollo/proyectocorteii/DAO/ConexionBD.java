package com.desarrollo.proyectocorteii.DAO;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {
    private static final String ARCHIVO_CONFIG = "config.properties";

    private ConexionBD() {}

    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(ARCHIVO_CONFIG)) {
            props.load(in);
        } catch (IOException e) {
            // Se lanza como SQLException para que el controlador lo trate igual que un fallo de conexión (E1)
            throw new SQLException("No se pudo leer el archivo de configuración", "08001", e);
        }
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password"));
    }
}