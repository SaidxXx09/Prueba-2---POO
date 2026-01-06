package ec.edu.sistemalicencias.dao;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import ec.edu.sistemalicencias.model.entities.Licencia;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;
import ec.edu.sistemalicencias.model.interfaces.Persistible;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Licencia.
 * Gestiona la persistencia de las licencias de conducir.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class LicenciaDAO implements Persistible<Licencia> {

    private final DatabaseConfig dbConfig;

    /**
     * Constructor
     */
    public LicenciaDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /**
     * Guarda o actualiza una licencia
     * @param licencia Licencia a persistir
     * @return ID de la licencia guardada
     * @throws BaseDatosException Si ocurre un error
     */
    @Override
    public Long guardar(Licencia licencia) throws BaseDatosException {
        if (licencia.getId() == null) {
            return insertar(licencia);
        } else {
            actualizar(licencia);
            return licencia.getId();
        }
    }

    /**
     * Inserta una nueva licencia
     * @param licencia Licencia a insertar
     * @return ID generado
     * @throws BaseDatosException Si ocurre un error
     */
    private Long insertar(Licencia licencia) throws BaseDatosException {
        String sql = "INSERT INTO licencias (numero_licencia, conductor_id, tipo_licencia, " +
                "fecha_emision, fecha_vencimiento, activa, prueba_psicometrica_id, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, licencia.getNumeroLicencia());
            stmt.setLong(2, licencia.getConductorId());
            stmt.setString(3, licencia.getTipoLicencia());
            stmt.setDate(4, Date.valueOf(licencia.getFechaEmision()));
            stmt.setDate(5, Date.valueOf(licencia.getFechaVencimiento()));
            stmt.setBoolean(6, licencia.isActiva());

            if (licencia.getPruebaPsicometricaId() != null) {
                stmt.setLong(7, licencia.getPruebaPsicometricaId());
            } else {
                stmt.setNull(7, Types.BIGINT);
            }

            stmt.setString(8, licencia.getObservaciones());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new BaseDatosException("No se pudo insertar la licencia");
            }

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new BaseDatosException("No se pudo obtener el ID generado");
            }

        } catch (SQLException e) {
            throw new BaseDatosException("Error al insertar licencia: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    /**
     * Actualiza una licencia existente
     * @param licencia Licencia a actualizar
     * @throws BaseDatosException Si ocurre un error
     */
    private void actualizar(Licencia licencia) throws BaseDatosException {
        String sql = "UPDATE licencias SET numero_licencia = ?, conductor_id = ?, " +
                "tipo_licencia = ?, fecha_emision = ?, fecha_vencimiento = ?, " +
                "activa = ?, prueba_psicometrica_id = ?, observaciones = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, licencia.getNumeroLicencia());
            stmt.setLong(2, licencia.getConductorId());
            stmt.setString(3, licencia.getTipoLicencia());
            stmt.setDate(4, Date.valueOf(licencia.getFechaEmision()));
            stmt.setDate(5, Date.valueOf(licencia.getFechaVencimiento()));
            stmt.setBoolean(6, licencia.isActiva());

            if (licencia.getPruebaPsicometricaId() != null) {
                stmt.setLong(7, licencia.getPruebaPsicometricaId());
            } else {
                stmt.setNull(7, Types.BIGINT);
            }

            stmt.setString(8, licencia.getObservaciones());
            stmt.setLong(9, licencia.getId());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new BaseDatosException("No se encontró la licencia con ID: " + licencia.getId());
            }

        } catch (SQLException e) {
            throw new BaseDatosException("Error al actualizar licencia: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    /**
     * Busca una licencia por ID
     * @param id ID de la licencia
     * @return Licencia encontrada o null
     * @throws BaseDatosException Si ocurre un error
     */
    @Override
    public Licencia buscarPorId(Long id) throws BaseDatosException {
        String sql = "SELECT * FROM licencias WHERE id = ?";

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
            throw new BaseDatosException("Error al buscar licencia por ID: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    /**
     * Busca una licencia por número
     * @param numeroLicencia Número de licencia
     * @return Licencia encontrada o null
     * @throws BaseDatosException Si ocurre un error
     */
    public Licencia buscarPorNumero(String numeroLicencia) throws BaseDatosException {
        String sql = "SELECT * FROM licencias WHERE numero_licencia = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, numeroLicencia);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearResultSet(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new BaseDatosException("Error al buscar licencia por número: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    /**
     * Busca licencias por conductor
     * @param conductorId ID del conductor
     * @return Lista de licencias del conductor
     * @throws BaseDatosException Si ocurre un error
     */
    public List<Licencia> buscarPorConductor(Long conductorId) throws BaseDatosException {
        String sql = "SELECT * FROM licencias WHERE conductor_id = ? ORDER BY fecha_emision DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Licencia> licencias = new ArrayList<>();

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, conductorId);

            rs = stmt.executeQuery();

            while (rs.next()) {
                licencias.add(mapearResultSet(rs));
            }

            return licencias;

        } catch (SQLException e) {
            throw new BaseDatosException("Error al buscar licencias por conductor: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    /**
     * Obtiene todas las licencias
     * @return Lista de licencias
     * @throws BaseDatosException Si ocurre un error
     */
    public List<Licencia> obtenerTodas() throws BaseDatosException {
        String sql = "SELECT * FROM licencias ORDER BY fecha_emision DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Licencia> licencias = new ArrayList<>();

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                licencias.add(mapearResultSet(rs));
            }

            return licencias;

        } catch (SQLException e) {
            throw new BaseDatosException("Error al obtener licencias: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    /**
     * Obtiene licencias vigentes
     * @return Lista de licencias vigentes
     * @throws BaseDatosException Si ocurre un error
     */
    public List<Licencia> obtenerLicenciasVigentes() throws BaseDatosException {
        String sql = "SELECT * FROM licencias WHERE activa = TRUE AND fecha_vencimiento > CURDATE() " +
                "ORDER BY fecha_vencimiento";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Licencia> licencias = new ArrayList<>();

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                licencias.add(mapearResultSet(rs));
            }

            return licencias;

        } catch (SQLException e) {
            throw new BaseDatosException("Error al obtener licencias vigentes: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    /**
     * Elimina una licencia
     * @param id ID de la licencia a eliminar
     * @return true si se eliminó correctamente
     * @throws BaseDatosException Si ocurre un error
     */
    @Override
    public boolean eliminar(Long id) throws BaseDatosException {
        String sql = "DELETE FROM licencias WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            throw new BaseDatosException("Error al eliminar licencia: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    /**
     * Mapea un ResultSet a un objeto Licencia
     * @param rs ResultSet
     * @return Objeto Licencia
     * @throws SQLException Si ocurre un error
     */
    private Licencia mapearResultSet(ResultSet rs) throws SQLException {
        Licencia licencia = new Licencia();

        licencia.setId(rs.getLong("id"));
        licencia.setNumeroLicencia(rs.getString("numero_licencia"));
        licencia.setConductorId(rs.getLong("conductor_id"));
        licencia.setTipoLicencia(rs.getString("tipo_licencia"));

        Date fechaEmision = rs.getDate("fecha_emision");
        if (fechaEmision != null) {
            licencia.setFechaEmision(fechaEmision.toLocalDate());
        }

        Date fechaVencimiento = rs.getDate("fecha_vencimiento");
        if (fechaVencimiento != null) {
            licencia.setFechaVencimiento(fechaVencimiento.toLocalDate());
        }

        licencia.setActiva(rs.getBoolean("activa"));

        long pruebaId = rs.getLong("prueba_psicometrica_id");
        if (!rs.wasNull()) {
            licencia.setPruebaPsicometricaId(pruebaId);
        }

        licencia.setObservaciones(rs.getString("observaciones"));

        return licencia;
    }

    /**
     * Cierra recursos de base de datos
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
