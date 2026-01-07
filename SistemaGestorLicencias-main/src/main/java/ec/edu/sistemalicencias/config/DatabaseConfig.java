package ec.edu.sistemalicencias.config;

import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static DatabaseConfig instancia;
    private final String url;
    private final String usuario;
    private final String password;

    private DatabaseConfig() {
        // --- DATOS DE RAILWAY ---
        String host = "gondola.proxy.rlwy.net";
        String puerto = "56426";
        String database = "railway";

        this.usuario = "postgres";
        this.password = "qrfOYTeHrUOwjdEJwqVUSWZxLbTdmhsy";

        // URL formateada para PostgreSQL
        this.url = "jdbc:postgresql://" + host + ":" + puerto + "/" + database + "?sslmode=require";

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver de PostgreSQL.");
        }
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instancia == null) {
            instancia = new DatabaseConfig();
        }
        return instancia;
    }

    public Connection obtenerConexion() throws BaseDatosException {
        try {
            return DriverManager.getConnection(url, usuario, password);
        } catch (SQLException e) {
            throw new BaseDatosException("Error conectando a Railway: " + e.getMessage(), e);
        }
    }

    // --- ESTE ES EL MÉTODO QUE FALTABA ---
    public boolean verificarConexion() {
        try (Connection conn = obtenerConexion()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            System.err.println("Fallo al verificar conexión: " + e.getMessage());
            return false;
        }
    }
}
