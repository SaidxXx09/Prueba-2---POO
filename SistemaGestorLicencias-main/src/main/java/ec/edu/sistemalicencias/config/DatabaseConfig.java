package ec.edu.sistemalicencias.config;

import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase de configuración para la conexión a la base de datos MySQL.
 * Implementa el patrón Singleton para gestionar una única instancia de configuración.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class DatabaseConfig {

    // Instancia única (Singleton)
    private static DatabaseConfig instancia;

    // Parámetros de conexión (Encapsulamiento)
    private final String url;
    private final String usuario;
    private final String password;
    private final String driver;

    /**
     * Constructor privado para implementar Singleton
     * Carga los parámetros de conexión desde configuración
     */
    private DatabaseConfig() {
        // Configuración por defecto - puede ser sobreescrita mediante properties
        this.driver = "com.mysql.cj.jdbc.Driver";
        this.url = "jdbc:mysql://localhost:3306/sistema_licencias?useSSL=false&serverTimezone=UTC";
        this.usuario = "root";
        this.password = "root";

        try {
            // Cargar el driver JDBC
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver MySQL: " + e.getMessage());
        }
    }

    /**
     * Obtiene la instancia única de DatabaseConfig (Singleton)
     * @return Instancia de DatabaseConfig
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instancia == null) {
            instancia = new DatabaseConfig();
        }
        return instancia;
    }

    /**
     * Crea y retorna una conexión a la base de datos
     * @return Objeto Connection
     * @throws BaseDatosException Si no se puede establecer la conexión
     */
    public Connection obtenerConexion() throws BaseDatosException {
        try {
            Connection conexion = DriverManager.getConnection(url, usuario, password);
            conexion.setAutoCommit(true);
            return conexion;
        } catch (SQLException e) {
            throw new BaseDatosException(
                    "Error al conectar con la base de datos: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Cierra una conexión de forma segura
     * @param conexion Conexión a cerrar
     */
    public void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    /**
     * Verifica si la conexión a la base de datos es válida
     * @return true si la conexión es exitosa, false en caso contrario
     */
    public boolean verificarConexion() {
        try (Connection conn = obtenerConexion()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            System.err.println("Error al verificar conexión: " + e.getMessage());
            return false;
        }
    }

    // Getters

    public String getUrl() {
        return url;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getDriver() {
        return driver;
    }
}
