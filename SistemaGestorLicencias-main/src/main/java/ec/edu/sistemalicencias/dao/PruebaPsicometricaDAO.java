package ec.edu.sistemalicencias.dao;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import ec.edu.sistemalicencias.model.entities.PruebaPsicometrica;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;
import ec.edu.sistemalicencias.model.interfaces.Persistible;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PruebaPsicometricaDAO implements Persistible<PruebaPsicometrica> {

    private final DatabaseConfig dbConfig;

    public PruebaPsicometricaDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public Long guardar(PruebaPsicometrica prueba) throws BaseDatosException {
        if (prueba.getId() == null || prueba.getId() == 0) {
            return insertar(prueba);
        } else {
            actualizar(prueba);
            return prueba.getId();
        }
    }

    private Long insertar(PruebaPsicometrica prueba) throws BaseDatosException {

        String sql = "INSERT INTO pruebas_psicometricas (conductor_id, reaccion, atencion, " +
                "coordinacion, percepcion, psicologia, promedio, aprobado, observaciones, fecha_prueba) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setLong(1, prueba.getConductorId());
            stmt.setInt(2, (int) prueba.getNotaReaccion());
            stmt.setInt(3, (int) prueba.getNotaAtencion());
            stmt.setInt(4, (int) prueba.getNotaCoordinacion());
            stmt.setInt(5, (int) prueba.getNotaPercepcion());
            stmt.setInt(6, (int) prueba.getNotaPsicologica());

            stmt.setDouble(7, prueba.calcularPromedio());
            stmt.setBoolean(8, prueba.estaAprobado());

            stmt.setString(9, prueba.getObservaciones());

            if (prueba.getFechaRealizacion() != null) {
                stmt.setDate(10, Date.valueOf(prueba.getFechaRealizacion().toLocalDate()));
            } else {
                stmt.setDate(10, new Date(System.currentTimeMillis()));
            }

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) throw new BaseDatosException("Fallo al insertar prueba");

            rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getLong(1);
            else throw new BaseDatosException("No se obtuvo ID");

        } catch (SQLException e) {
            throw new BaseDatosException("Error al insertar prueba: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    private void actualizar(PruebaPsicometrica prueba) throws BaseDatosException {
        String sql = "UPDATE pruebas_psicometricas SET reaccion=?, atencion=?, coordinacion=?, " +
                "percepcion=?, psicologia=?, promedio=?, aprobado=?, observaciones=? WHERE id=?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, (int) prueba.getNotaReaccion());
            stmt.setInt(2, (int) prueba.getNotaAtencion());
            stmt.setInt(3, (int) prueba.getNotaCoordinacion());
            stmt.setInt(4, (int) prueba.getNotaPercepcion());
            stmt.setInt(5, (int) prueba.getNotaPsicologica());
            stmt.setDouble(6, prueba.calcularPromedio());
            stmt.setBoolean(7, prueba.estaAprobado());
            stmt.setString(8, prueba.getObservaciones());
            stmt.setLong(9, prueba.getId());

            if (stmt.executeUpdate() == 0) throw new BaseDatosException("No existe prueba ID: " + prueba.getId());
        } catch (SQLException e) {
            throw new BaseDatosException("Error al actualizar: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

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
            if (rs.next()) return mapearResultSet(rs);
            return null;
        } catch (SQLException e) {
            throw new BaseDatosException("Error buscando por ID: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    public List<PruebaPsicometrica> buscarPorConductor(Long conductorId) throws BaseDatosException {
        String sql = "SELECT * FROM pruebas_psicometricas WHERE conductor_id = ? ORDER BY fecha_prueba DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<PruebaPsicometrica> pruebas = new ArrayList<>();
        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, conductorId);
            rs = stmt.executeQuery();
            while (rs.next()) pruebas.add(mapearResultSet(rs));
            return pruebas;
        } catch (SQLException e) {
            throw new BaseDatosException("Error listando pruebas: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    public PruebaPsicometrica obtenerUltimaPruebaAprobada(Long conductorId) throws BaseDatosException {
        String sql = "SELECT * FROM pruebas_psicometricas WHERE conductor_id = ? AND aprobado = TRUE ORDER BY fecha_prueba DESC LIMIT 1";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, conductorId);
            rs = stmt.executeQuery();
            if (rs.next()) return mapearResultSet(rs);
            return null;
        } catch (SQLException e) {
            throw new BaseDatosException("Error buscando aprobada: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    @Override
    public boolean eliminar(Long id) throws BaseDatosException {
        return false;
    }

    private PruebaPsicometrica mapearResultSet(ResultSet rs) throws SQLException {
        PruebaPsicometrica p = new PruebaPsicometrica();
        p.setId(rs.getLong("id"));
        p.setConductorId(rs.getLong("conductor_id"));

        p.setNotaReaccion(rs.getDouble("reaccion"));
        p.setNotaAtencion(rs.getDouble("atencion"));
        p.setNotaCoordinacion(rs.getDouble("coordinacion"));
        p.setNotaPercepcion(rs.getDouble("percepcion"));
        p.setNotaPsicologica(rs.getDouble("psicologia"));
        p.setObservaciones(rs.getString("observaciones"));

        Date fecha = rs.getDate("fecha_prueba");
        if (fecha != null) p.setFechaRealizacion(fecha.toLocalDate().atStartOfDay());

        return p;
    }

    private void cerrarRecursos(Connection conn, Statement stmt, ResultSet rs) {
        try { if(rs!=null) rs.close(); if(stmt!=null) stmt.close(); if(conn!=null) conn.close(); } catch(Exception e){}
    }
}
