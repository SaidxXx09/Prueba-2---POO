package ec.edu.sistemalicencias.dao;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import ec.edu.sistemalicencias.model.entities.PruebaPsicometrica;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;
import ec.edu.sistemalicencias.model.interfaces.Persistible;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad PruebaPsicometrica.
 * Gestiona la persistencia de las pruebas psicométricas.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class PruebaPsicometricaDAO implements Persistible<PruebaPsicometrica> {

    private final DatabaseConfig dbConfig;

    /**
     * Constructor
     */
    public PruebaPsicometricaDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /**
     * Guarda o actualiza una prueba psicométrica
     * @param prueba Prueba a persistir
     * @return ID de la prueba guardada
     * @throws BaseDatosException Si ocurre un error en la operación
     */
    @Override
    public Long guardar(PruebaPsicometrica prueba) throws BaseDatosException {
        if (prueba.getId() == null) {
            return insertar(prueba);
        } else {
            actualizar(prueba);
            return prueba.getId();
        }
    }

    /**
     * Inserta una nueva prueba psicométrica
     * @param prueba Prueba a insertar
     * @return ID generado
     * @throws BaseDatosException Si ocurre un error
     */
    private Long insertar(PruebaPsicometrica prueba) throws BaseDatosException {
        String sql = "INSERT INTO pruebas_psicometricas (conductor_id, nota_reaccion, " +
                "nota_atencion, nota_coordinacion, nota_percepcion, nota_psicologica, " +
                "observaciones, fecha_realizacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setLong(1, prueba.getConductorId());
            stmt.setDouble(2, prueba.getNotaReaccion());
            stmt.setDouble(3, prueba.getNotaAtencion());
            stmt.setDouble(4, prueba.getNotaCoordinacion());
            stmt.setDouble(5, prueba.getNotaPercepcion());
            stmt.setDouble(6, prueba.getNotaPsicologica());
            stmt.setString(7, prueba.getObservaciones());
            stmt.setTimestamp(8, Timestamp.valueOf(prueba.getFechaRealizacion()));

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new BaseDatosException("No se pudo insertar la prueba psicométrica");
            }

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new BaseDatosException("No se pudo obtener el ID generado");
            }

        } catch (SQLException e) {
            throw new BaseDatosException("Error al insertar prueba psicométrica: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    /**
     * Actualiza una prueba psicométrica existente
     * @param prueba Prueba a actualizar
     * @throws BaseDatosException Si ocurre un error
     */
    private void actualizar(PruebaPsicometrica prueba) throws BaseDatosException {
        String sql = "UPDATE pruebas_psicometricas SET nota_reaccion = ?, nota_atencion = ?, " +
                "nota_coordinacion = ?, nota_percepcion = ?, nota_psicologica = ?, " +
                "observaciones = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);

            stmt.setDouble(1, prueba.getNotaReaccion());
            stmt.setDouble(2, prueba.getNotaAtencion());
            stmt.setDouble(3, prueba.getNotaCoordinacion());
            stmt.setDouble(4, prueba.getNotaPercepcion());
            stmt.setDouble(5, prueba.getNotaPsicologica());
            stmt.setString(6, prueba.getObservaciones());
            stmt.setLong(7, prueba.getId());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new BaseDatosException("No se encontró la prueba con ID: " + prueba.getId());
            }

        } catch (SQLException e) {
            throw new BaseDatosException("Error al actualizar prueba psicométrica: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    /**
     * Busca una prueba por ID
     * @param id ID de la prueba
     * @return Prueba encontrada o null
     * @throws BaseDatosException Si ocurre un error
     */
    @Override
    public PruebaPsicometrica buscarPorId(Long id) throws BaseDatosException {
        String sql = "SELECT * FROM pruebas_psicometricas WHERE id = ?";

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
            throw new BaseDatosException("Error al buscar prueba por ID: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    /**
     * Busca pruebas por conductor
     * @param conductorId ID del conductor
     * @return Lista de pruebas del conductor
     * @throws BaseDatosException Si ocurre un error
     */
    public List<PruebaPsicometrica> buscarPorConductor(Long conductorId) throws BaseDatosException {
        String sql = "SELECT * FROM pruebas_psicometricas WHERE conductor_id = ? " +
                "ORDER BY fecha_realizacion DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<PruebaPsicometrica> pruebas = new ArrayList<>();

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, conductorId);

            rs = stmt.executeQuery();

            while (rs.next()) {
                pruebas.add(mapearResultSet(rs));
            }

            return pruebas;

        } catch (SQLException e) {
            throw new BaseDatosException("Error al buscar pruebas por conductor: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    /**
     * Obtiene la última prueba aprobada de un conductor
     * @param conductorId ID del conductor
     * @return Última prueba aprobada o null
     * @throws BaseDatosException Si ocurre un error
     */
    public PruebaPsicometrica obtenerUltimaPruebaAprobada(Long conductorId) throws BaseDatosException {
        String sql = "SELECT * FROM pruebas_psicometricas WHERE conductor_id = ? " +
                "AND aprobado = TRUE ORDER BY fecha_realizacion DESC LIMIT 1";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, conductorId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearResultSet(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new BaseDatosException("Error al obtener última prueba aprobada: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    /**
     * Elimina una prueba
     * @param id ID de la prueba a eliminar
     * @return true si se eliminó correctamente
     * @throws BaseDatosException Si ocurre un error
     */
    @Override
    public boolean eliminar(Long id) throws BaseDatosException {
        String sql = "DELETE FROM pruebas_psicometricas WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            throw new BaseDatosException("Error al eliminar prueba: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    /**
     * Mapea un ResultSet a un objeto PruebaPsicometrica
     * @param rs ResultSet
     * @return Objeto PruebaPsicometrica
     * @throws SQLException Si ocurre un error
     */
    private PruebaPsicometrica mapearResultSet(ResultSet rs) throws SQLException {
        PruebaPsicometrica prueba = new PruebaPsicometrica();

        prueba.setId(rs.getLong("id"));
        prueba.setConductorId(rs.getLong("conductor_id"));
        prueba.setNotaReaccion(rs.getDouble("nota_reaccion"));
        prueba.setNotaAtencion(rs.getDouble("nota_atencion"));
        prueba.setNotaCoordinacion(rs.getDouble("nota_coordinacion"));
        prueba.setNotaPercepcion(rs.getDouble("nota_percepcion"));
        prueba.setNotaPsicologica(rs.getDouble("nota_psicologica"));
        prueba.setObservaciones(rs.getString("observaciones"));

        Timestamp fechaRealizacion = rs.getTimestamp("fecha_realizacion");
        if (fechaRealizacion != null) {
            prueba.setFechaRealizacion(fechaRealizacion.toLocalDateTime());
        }

        return prueba;
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
