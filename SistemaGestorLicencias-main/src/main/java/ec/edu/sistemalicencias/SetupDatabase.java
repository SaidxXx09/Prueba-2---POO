package ec.edu.sistemalicencias;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Script de inicialización de la Base de Datos en la Nube (Railway).
 * Crea todas las tablas con la estructura definitiva y usuarios por defecto.
 */
public class SetupDatabase {

    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println("   INICIANDO CONFIGURACIÓN DE BASE DE DATOS  ");
        System.out.println("            (PostgreSQL / Railway)           ");
        System.out.println("=============================================");

        // 1. Tabla USUARIOS
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
                "direccion VARCHAR(200), " +
                "telefono VARCHAR(20), " +
                "email VARCHAR(100), " +
                "tipo_sangre VARCHAR(5), " +
                "donador_organos BOOLEAN DEFAULT FALSE, " +
                "documentos_validos BOOLEAN DEFAULT FALSE, " +
                "observaciones TEXT" +
                ");";

        // 3. Tabla PRUEBAS PSICOMÉTRICAS
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

        // 5. Definición de Usuarios por Defecto
        String sqlAdmin = "INSERT INTO usuarios (username, password, rol) VALUES ('admin', '1234', 'ADMINISTRADOR') " +
                "ON CONFLICT (username) DO NOTHING;";

        String sqlAnalista = "INSERT INTO usuarios (username, password, rol) VALUES ('analista', '1234', 'ANALISTA') " +
                "ON CONFLICT (username) DO NOTHING;";

        String sqlAnalista1 = "INSERT INTO usuarios (username, password, rol) VALUES ('Analista_Ariel', 'nacionalmivida', 'ANALISTA') " +
                "ON CONFLICT (username) DO NOTHING;";

        String sqlAnalista2 = "INSERT INTO usuarios (username, password, rol) VALUES ('Analista_Alejandro', 'Zoesita', 'ANALISTA') " +
                "ON CONFLICT (username) DO NOTHING;";

        try (Connection conn = DatabaseConfig.getInstance().obtenerConexion();
             Statement stmt = conn.createStatement()) {

            System.out.println("Conectando a la nube...");

            // Ejecución secuencial de creación de tablas
            stmt.execute(sqlUsuarios);
            System.out.println("Tabla 'usuarios' verificada.");

            stmt.execute(sqlConductores);
            System.out.println("Tabla 'conductores' verificada.");

            stmt.execute(sqlPruebas);
            System.out.println("Tabla 'pruebas_psicometricas' verificada.");

            stmt.execute(sqlLicencias);
            System.out.println("Tabla 'licencias' verificada.");


            stmt.executeUpdate(sqlAdmin);
            stmt.executeUpdate(sqlAnalista);
            stmt.executeUpdate(sqlAnalista1);
            stmt.executeUpdate(sqlAnalista2);

            System.out.println("Usuarios del sistema (incluyendo Ariel y Alejandro) verificados.");

            System.out.println("=============================================");
            System.out.println("       ¡SISTEMA CONFIGURADO CON ÉXITO!       ");
            System.out.println("=============================================");

        } catch (Exception e) {
            System.err.println("Error crítico en la configuración:");
            e.printStackTrace();
        }
    }
}
