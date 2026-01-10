package ec.edu.sistemalicencias.dao;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import ec.edu.sistemalicencias.model.entities.Licencia;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;
import ec.edu.sistemalicencias.model.interfaces.Persistible;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LicenciaDAO implements Persistible<Licencia> {

    private final DatabaseConfig dbConfig;

    public LicenciaDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public Long guardar(Licencia licencia) throws BaseDatosException {
        if (licencia.getId() == null || licencia.getId() == 0) {
            return insertar(licencia);
        } else {
            actualizar(licencia);
            return licencia.getId();
        }
    }

    private Long insertar(Licencia licencia) throws BaseDatosException {
        String sql = "INSERT INTO licencias (conductor_id, tipo_licencia, " +
                "fecha_emision, fecha_vencimiento, estado, puntos, costo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.obtenerConexion();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setLong(1, licencia.getConductorId());


            String tipoJava = licencia.getTipoLicencia();
            String tipoParaBD = tipoJava;

            if (tipoJava != null && tipoJava.contains("_")) {
                
                tipoParaBD = tipoJava.substring(tipoJava.lastIndexOf("_") + 1);
            }
            stmt.setString(2, tipoParaBD);
           

            LocalDate emision = licencia.getFechaEmision() != null ? licencia.getFechaEmision() : LocalDate.now();
            LocalDate vencimiento = licencia.getFechaVencimiento() != null ? licencia.getFechaVencimiento() : emision.plusYears(5);

            stmt.setDate(3, Date.valueOf(emision));
            stmt.setDate(4, Date.valueOf(vencimiento));

            String estadoCorto = licencia.isActiva() ? "ACT" : "INA";
            stmt.setString(5, estadoCorto);

            stmt.setInt(6, 30);
            stmt.setDouble(7, 60.00);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new BaseDatosException("No se pudo insertar la licencia");
            }

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new BaseDatosException("No se generó ID");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new BaseDatosException("Error SQL: " + e.getMessage(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    private void actualizar(Licencia licencia) throws BaseDatosException {
        String sql = "UPDATE licencias SET estado = ? WHERE id = ?";
        try (Connection conn = dbConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String estadoCorto = licencia.isActiva() ? "ACT" : "INA";
            stmt.setString(1, estadoCorto);
            stmt.setLong(2, licencia.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BaseDatosException("Error actualizando: " + e.getMessage(), e);
        }
    }

    @Override
    public Licencia buscarPorId(Long id) throws BaseDatosException {
        String sql = "SELECT * FROM licencias WHERE id = ?";
        try (Connection conn = dbConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapearResultSet(rs);
            }
        } catch (SQLException e) {
            throw new BaseDatosException("Error buscando ID: " + e.getMessage(), e);
        }
        return null;
    }

    public Licencia buscarPorNumero(String numeroLicencia) throws BaseDatosException {
        try {
            Long id = Long.parseLong(numeroLicencia);
            return buscarPorId(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public List<Licencia> buscarPorConductor(Long conductorId) throws BaseDatosException {
        String sql = "SELECT * FROM licencias WHERE conductor_id = ? ORDER BY fecha_emision DESC";
        List<Licencia> lista = new ArrayList<>();
        try (Connection conn = dbConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, conductorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Licencia l = mapearResultSet(rs);
                    if (l != null) lista.add(l); 
                }
            }
        } catch (SQLException e) {
            throw new BaseDatosException("Error buscando por conductor: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Licencia> obtenerTodas() throws BaseDatosException {
        String sql = "SELECT * FROM licencias ORDER BY fecha_emision DESC";
        List<Licencia> lista = new ArrayList<>();
        try (Connection conn = dbConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Licencia l = mapearResultSet(rs);
                if (l != null) lista.add(l);
            }
        } catch (SQLException e) { throw new BaseDatosException(e.getMessage(), e); }
        return lista;
    }

    public List<Licencia> obtenerLicenciasVigentes() throws BaseDatosException {
        String sql = "SELECT * FROM licencias WHERE estado = 'ACT' AND fecha_vencimiento > CURRENT_DATE";
        List<Licencia> lista = new ArrayList<>();
        try (Connection conn = dbConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Licencia l = mapearResultSet(rs);
                if (l != null) lista.add(l);
            }
        } catch (SQLException e) { throw new BaseDatosException(e.getMessage(), e); }
        return lista;
    }

    @Override
    public boolean eliminar(Long id) throws BaseDatosException { return false; }

   
    private Licencia mapearResultSet(ResultSet rs) throws SQLException {
        try {
            Licencia l = new Licencia();
            l.setId(rs.getLong("id"));
            l.setNumeroLicencia(String.valueOf(rs.getLong("id")));
            l.setConductorId(rs.getLong("conductor_id"));

           
            
            String tipoBD = rs.getString("tipo_licencia");

            
            if (tipoBD == null || tipoBD.trim().isEmpty() || tipoBD.equals("TIPO_")) {
                tipoBD = "B"; // Valor por defecto seguro
            }

            
            if (!tipoBD.startsWith("TIPO_")) {
                l.setTipoLicencia("TIPO_" + tipoBD);
            } else {
                l.setTipoLicencia(tipoBD);
            }
           

            Date fEmision = rs.getDate("fecha_emision");
            if (fEmision != null) l.setFechaEmision(fEmision.toLocalDate());

            Date fVenc = rs.getDate("fecha_vencimiento");
            if (fVenc != null) l.setFechaVencimiento(fVenc.toLocalDate());

            String estado = rs.getString("estado");
            l.setActiva(estado != null && (estado.startsWith("ACT") || estado.startsWith("VIG")));

            return l;
        } catch (Exception e) {
            
            System.err.println("Saltando licencia corrupta ID " + rs.getLong("id") + ": " + e.getMessage());
            return null;
        }
    }

    private void cerrarRecursos(Connection conn, Statement stmt, ResultSet rs) {
        try { if(rs!=null) rs.close(); if(stmt!=null) stmt.close(); if(conn!=null) conn.close(); } catch(Exception e){}
    }
}
