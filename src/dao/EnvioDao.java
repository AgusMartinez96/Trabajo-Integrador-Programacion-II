package dao;

import entities.Envio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.DatabaseConnection;

public class EnvioDao implements GenericDao<Envio> {

    @Override
    public void insertar(Envio envio) throws Exception {
        String sql = "INSERT INTO envio (tracking, empresa, tipo, costo, fecha_despacho, fecha_estimada, estado, eliminado) VALUES (?, ?, ?, ?, ?, ?, ?, false)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setEnvioParameters(stmt, envio);
            stmt.executeUpdate();
            
            setGeneratedId(stmt, envio);
        }
    }

    @Override
    public Envio leerPorId(Long id) throws Exception {
        String sql = "SELECT * FROM envio WHERE id = ? AND eliminado = false";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearEnvio(rs);
            }
        }
        return null;
    }

    @Override
    public List<Envio> leerTodos() throws Exception {
        String sql = "SELECT * FROM envio WHERE eliminado = false";
        List<Envio> envios = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                envios.add(mapearEnvio(rs));
            }
        }
        return envios;
    }

    @Override
    public void actualizar(Envio envio) throws Exception {
        String sql = "UPDATE envio SET tracking = ?, empresa = ?, tipo = ?, costo = ?, fecha_despacho = ?, fecha_estimada = ?, estado = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Se reutiliza el metodo setEnvioParameters
            setEnvioParameters(stmt, envio);
            // Setear el id que no está en el método
            stmt.setLong(8, envio.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(Long id) throws Exception {
        String sql = "UPDATE envio SET eliminado = true WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            
            // Dar aviso si no se pudo eliminar
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo eliminar el envío. No se encontró envío con ID: " + id);
            }
        }
    }

    private Envio mapearEnvio(ResultSet rs) throws SQLException {
        Envio envio = new Envio();
        envio.setId(rs.getLong("id"));
        envio.setEliminado(rs.getBoolean("eliminado"));
        envio.setTracking(rs.getString("tracking"));
        envio.setEmpresa(Envio.Empresa.valueOf(rs.getString("empresa")));
        envio.setTipo(Envio.TipoEnvio.valueOf(rs.getString("tipo")));
        envio.setCosto(rs.getDouble("costo"));
        envio.setFechaDespacho(rs.getDate("fecha_despacho").toLocalDate());
        envio.setFechaEstimada(rs.getDate("fecha_estimada").toLocalDate());
        envio.setEstado(Envio.EstadoEnvio.valueOf(rs.getString("estado")));
        return envio;
    }
    
    private void setEnvioParameters(PreparedStatement stmt, Envio envio) throws SQLException {  
        stmt.setString(1, envio.getTracking());
        stmt.setString(2, envio.getEmpresa().name());
        stmt.setString(3, envio.getTipo().name());
        stmt.setDouble(4, envio.getCosto());
        stmt.setDate(5, Date.valueOf(envio.getFechaDespacho()));
        stmt.setDate(6, Date.valueOf(envio.getFechaEstimada()));
        stmt.setString(7, envio.getEstado().name());
    }
    
    // Obtener el id generado por el insert en la base de datos
    private void setGeneratedId(PreparedStatement stmt, Envio envio) throws SQLException {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                envio.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("La inserción del envío falló, no se obtuvo ID generado");
            }
        }
    }
}