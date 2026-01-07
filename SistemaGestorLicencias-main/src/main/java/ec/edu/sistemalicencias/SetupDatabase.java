package ec.edu.sistemalicencias;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.Statement;

public class SetupDatabase {
    public static void main(String[] args) {
        System.out.println("--- INICIANDO CONFIGURACIÓN DE BASE DE DATOS EN NUBE ---");

        // 1. SQL para crear la tabla
        String sqlTabla = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id SERIAL PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(50) NOT NULL, " +
                "rol VARCHAR(20) NOT NULL" +
                ");";

        // 2. SQL para insertar usuarios (usamos ON CONFLICT para que no de error si ya existen)
        String sqlAdmin = "INSERT INTO usuarios (username, password, rol) VALUES ('admin', '1234', 'ADMINISTRADOR') " +
                "ON CONFLICT (username) DO NOTHING;";

        String sqlAnalista = "INSERT INTO usuarios (username, password, rol) VALUES ('analista', '1234', 'ANALISTA') " +
                "ON CONFLICT (username) DO NOTHING;";

        try (Connection conn = DatabaseConfig.getInstance().obtenerConexion();
             Statement stmt = conn.createStatement()) {

            System.out.println("1. Conexión exitosa a Railway.");

            // Ejecutar creación de tabla
            stmt.execute(sqlTabla);
            System.out.println("2. Tabla 'usuarios' verificada.");

            // Ejecutar inserciones
            int filasAdmin = stmt.executeUpdate(sqlAdmin);
            int filasAnalista = stmt.executeUpdate(sqlAnalista);

            System.out.println("3. Usuarios insertados/verificados.");
            System.out.println("-------------------------------------------------");
            System.out.println("¡LISTO! YA PUEDES EJECUTAR TU LOGINVIEW.");

        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO:");
            e.printStackTrace();
        }
    }
}