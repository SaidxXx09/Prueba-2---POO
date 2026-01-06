package ec.edu.sistemalicencias.dao;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import ec.edu.sistemalicencias.model.entities.Conductor;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;
import ec.edu.sistemalicencias.model.interfaces.Persistible;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Conductor.
 * Implementa la interface Persistible para operaciones CRUD.
 * Aplica el patrón DAO para separar la lógica de acceso a datos.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class ConductorDAO implements Persistible<Conductor> {

    private final DatabaseConfig dbConfig;

    /**
     * Constructor que inicializa la configuración de base de datos
     */
    public ConductorDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /**
     * Guarda o actualiza un conductor en la base de datos
     * @param conductor Conductor a persistir
     * @return ID del conductor guardado
     * @throws BaseDatosException Si ocurre un error en la operación
     */
    @Override
    public Long guardar(Conductor conductor) throws BaseDatosException {
        if (conductor.getId() == null) {
            return insertar(conductor);
        } else {
            actualizar(conductor);
            return conductor.getId();
        }
    }

    /**
     * Inserta un nuevo conductor en la base de datos
     * @param conductor Conductor a insertar
     * @return ID generado
     * @throws BaseDatosException Si ocurre un error en la inserción
     */
    private Long insertar(Conductor conductor) throws BaseDatosException {
        String sql = "INSERT INTO conductores (cedula, nombres, apellidos, fecha_nacimiento, " +
                "direccion, telefono, email, tipo_sangre, documentos_validados, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, conductor.getCedula());
            stmt.setString(2, conductor.getNombres());
            stmt.setString(3, conductor.getApellidos());
            stmt.setDate(4, Date.valueOf(conductor.getFechaNacimiento()));
            stmt.setString(5, conductor.getDireccion());
            stmt.setString(6, conductor.getTelefono());
            stmt.setString(7, conductor.getEmail());
            stmt.setString(8, conductor.getTipoSangre());
            stmt.setBoolean(9, conductor.isDocumentosValidados());
            stmt.setString(10, conductor.getObservaciones());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new BaseDatosException("No se pudo insertar el conductor");
            }

            // Obtener el ID generado
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new BaseDatosException("No se pudo obtener el ID generado");
            }

        } catch (SQLException e) {
            throw new BaseDatosException("Error al insertar conductor: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    /**
     * Actualiza un conductor existente
     * @param conductor Conductor a actualizar
     * @throws BaseDatosException Si ocurre un error en la actualización
     */
    private void actualizar(Conductor conductor) throws BaseDatosException {
        String sql = "UPDATE conductores SET cedula = ?, nombres = ?, apellidos = ?, " +
                "fecha_nacimiento = ?, direccion = ?, telefono = ?, email = ?, " +
                "tipo_sangre = ?, documentos_validados = ?, observaciones = ? " +
                "WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, conductor.getCedula());
            stmt.setString(2, conductor.getNombres());
            stmt.setString(3, conductor.getApellidos());
            stmt.setDate(4, Date.valueOf(conductor.getFechaNacimiento()));
            stmt.setString(5, conductor.getDireccion());
            stmt.setString(6, conductor.getTelefono());
            stmt.setString(7, conductor.getEmail());
            stmt.setString(8, conductor.getTipoSangre());
            stmt.setBoolean(9, conductor.isDocumentosValidados());
            stmt.setString(10, conductor.getObservaciones());
            stmt.setLong(11, conductor.getId());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new BaseDatosException("No se encontró el conductor con ID: " + conductor.getId());
            }

        } catch (SQLException e) {
            throw new BaseDatosException("Error al actualizar conductor: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    /**
     * Busca un conductor por su ID
     * @param id ID del conductor
     * @return Conductor encontrado o null
     * @throws BaseDatosException Si ocurre un error en la búsqueda
     */
    @Override
    public Conductor buscarPorId(Long id) throws BaseDatosException {
        String sql = "SELECT * FROM conductores WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearResultSet(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new BaseDatosException("Error al buscar conductor por ID: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    /**
     * Busca un conductor por su cédula
     * @param cedula Número de cédula
     * @return Conductor encontrado o null
     * @throws BaseDatosException Si ocurre un error en la búsqueda
     */
    public Conductor buscarPorCedula(String cedula) throws BaseDatosException {
        String sql = "SELECT * FROM conductores WHERE cedula = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cedula);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearResultSet(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new BaseDatosException("Error al buscar conductor por cédula: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    /**
     * Obtiene todos los conductores
     * @return Lista de conductores
     * @throws BaseDatosException Si ocurre un error en la consulta
     */
    public List<Conductor> obtenerTodos() throws BaseDatosException {
        String sql = "SELECT * FROM conductores ORDER BY apellidos, nombres";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Conductor> conductores = new ArrayList<>();

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                conductores.add(mapearResultSet(rs));
            }

            return conductores;

        } catch (SQLException e) {
            throw new BaseDatosException("Error al obtener conductores: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    /**
     * Busca conductores por nombre (búsqueda parcial)
     * @param nombre Nombre o apellido a buscar
     * @return Lista de conductores que coinciden
     * @throws BaseDatosException Si ocurre un error en la búsqueda
     */
    public List<Conductor> buscarPorNombre(String nombre) throws BaseDatosException {
        String sql = "SELECT * FROM conductores WHERE nombres LIKE ? OR apellidos LIKE ? " +
                "ORDER BY apellidos, nombres";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Conductor> conductores = new ArrayList<>();

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);
            String patron = "%" + nombre + "%";
            stmt.setString(1, patron);
            stmt.setString(2, patron);

            rs = stmt.executeQuery();

            while (rs.next()) {
                conductores.add(mapearResultSet(rs));
            }

            return conductores;

        } catch (SQLException e) {
            throw new BaseDatosException("Error al buscar conductores por nombre: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    /**
     * Elimina un conductor
     * @param id ID del conductor a eliminar
     * @return true si se eliminó correctamente
     * @throws BaseDatosException Si ocurre un error en la eliminación
     */
    @Override
    public boolean eliminar(Long id) throws BaseDatosException {
        String sql = "DELETE FROM conductores WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            throw new BaseDatosException("Error al eliminar conductor: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    /**
     * Mapea un ResultSet a un objeto Conductor
     * @param rs ResultSet con los datos
     * @return Objeto Conductor
     * @throws SQLException Si ocurre un error al leer los datos
     */
    private Conductor mapearResultSet(ResultSet rs) throws SQLException {
        Conductor conductor = new Conductor();

        conductor.setId(rs.getLong("id"));
        conductor.setCedula(rs.getString("cedula"));
        conductor.setNombres(rs.getString("nombres"));
        conductor.setApellidos(rs.getString("apellidos"));

        Date fechaNac = rs.getDate("fecha_nacimiento");
        if (fechaNac != null) {
            conductor.setFechaNacimiento(fechaNac.toLocalDate());
        }

        conductor.setDireccion(rs.getString("direccion"));
        conductor.setTelefono(rs.getString("telefono"));
        conductor.setEmail(rs.getString("email"));

        String tipoSangreStr = rs.getString("tipo_sangre");
        conductor.setTipoSangre(tipoSangreStr);

        conductor.setDocumentosValidados(rs.getBoolean("documentos_validados"));
        conductor.setObservaciones(rs.getString("observaciones"));

        return conductor;
    }

    /**
     * Cierra los recursos de base de datos de forma segura
     * @param conn Conexión
     * @param stmt Statement
     * @param rs ResultSet
     */
    private void cerrarRecursos(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar recursos: " + e.getMessage());
        }
    }
}
