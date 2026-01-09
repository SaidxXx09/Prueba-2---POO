package ec.edu.sistemalicencias;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.Statement;

public class SetupDatabase {
    public static void main(String[] args) {
        System.out.println("--- INICIANDO CONFIGURACIÓN COMPLETA (RAILWAY) ---");

        // 1. Tabla USUARIOS (Login)
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id SERIAL PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(50) NOT NULL, " +
                "rol VARCHAR(20) NOT NULL" +
                ");";

        // 2. Tabla CONDUCTORES
        String sqlConductores = "CREATE TABLE IF NOT EXISTS conductores (" +
                "id SERIAL PRIMARY KEY, " +
                "cedula VARCHAR(15) UNIQUE NOT NULL, " +
                "nombres VARCHAR(100) NOT NULL, " +
                "apellidos VARCHAR(100) NOT NULL, " +
                "fecha_nacimiento DATE NOT NULL, " +
                "telefono VARCHAR(20), " +
                "tipo_sangre VARCHAR(5), " +
                "email VARCHAR(100), " +
                "donador_organos BOOLEAN DEFAULT FALSE, " +
                "documentos_validos BOOLEAN DEFAULT FALSE" +
                ");";

        // 3. Tabla PRUEBAS PSICOMETRICAS 
        String sqlPruebas = "CREATE TABLE IF NOT EXISTS pruebas_psicometricas (" +
                "id SERIAL PRIMARY KEY, " +
                "conductor_id INT NOT NULL REFERENCES conductores(id), " +
                "reaccion INT, " +
                "coordinacion INT, " +
                "atencion INT, " +
                "percepcion INT, " +
                "psicologia INT, " +
                "promedio DECIMAL(5,2), " +
                "aprobado BOOLEAN, " +
                "observaciones TEXT, " +
                "fecha_prueba DATE DEFAULT CURRENT_DATE" +
                ");";

        // 4. Tabla LICENCIAS 
        String sqlLicencias = "CREATE TABLE IF NOT EXISTS licencias (" +
                "id SERIAL PRIMARY KEY, " +
                "conductor_id INT NOT NULL REFERENCES conductores(id), " +
                "tipo_licencia VARCHAR(5) NOT NULL, " +
                "fecha_emision DATE NOT NULL, " +
                "fecha_vencimiento DATE NOT NULL, " +
                "puntos INT DEFAULT 30, " +
                "estado VARCHAR(20) DEFAULT 'ACTIVA', " +
                "costo DECIMAL(10,2)" +
                ");";

        // 5. Insertar Usuarios por defecto (si no existen)
        String sqlAdmin = "INSERT INTO usuarios (username, password, rol) VALUES ('admin', '1234', 'ADMINISTRADOR') " +
                          "ON CONFLICT (username) DO NOTHING;";
        String sqlAnalista = "INSERT INTO usuarios (username, password, rol) VALUES ('analista', '1234', 'ANALISTA') " +
                             "ON CONFLICT (username) DO NOTHING;";

        try (Connection conn = DatabaseConfig.getInstance().obtenerConexion();
             Statement stmt = conn.createStatement()) {

            System.out.println("Conectando a Railway...");

            stmt.execute(sqlUsuarios);
            System.out.println(" Tabla 'usuarios' lista.");

            stmt.execute(sqlConductores);
            System.out.println(" Tabla 'conductores' lista.");

            stmt.execute(sqlPruebas);
            System.out.println(" Tabla 'pruebas_psicometricas' lista.");

            stmt.execute(sqlLicencias);
            System.out.println(" Tabla 'licencias' lista.");

            // Crear usuarios
            stmt.executeUpdate(sqlAdmin);
            stmt.executeUpdate(sqlAnalista);
            System.out.println(" Usuarios Admin y Analista verificados.");

            System.out.println("-------------------------------------------");
            System.out.println("¡BASE DE DATOS EN LA NUBE COMPLETAMENTE LISTA!");

        } catch (Exception e) {
            System.err.println(" Error durante la configuración:");
            e.printStackTrace();
        }
    }
}
