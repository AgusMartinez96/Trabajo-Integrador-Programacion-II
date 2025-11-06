package dao;

import entities.Envio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnvioDao implements GenericDao<Envio> {

    @Override
    public void crear(Envio envio, Connection conn) throws Exception {
        String sql = "INSERT INTO envio (tracking, empresa, tipo, costo, fecha_despacho, fecha_estimada, estado, eliminado) VALUES (?, ?, ?, ?, ?, ?, ?, false)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, envio.getTracking());
            stmt.setString(2, envio.getEmpresa().name());
            stmt.setString(3, envio.getTipo().name());
            stmt.setDouble(4, envio.getCosto());
            stmt.setDate(5, Date.valueOf(envio.getFechaDespacho()));
            stmt.setDate(6, Date.valueOf(envio.getFechaEstimada()));
            stmt.setString(7, envio.getEstado().name());
            stmt.executeUpdate();
        }
    }

    @Override
    public Envio leerPorId(Long id, Connection conn) throws Exception {
        String sql = "SELECT * FROM envio WHERE id = ? AND eliminado = false";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearEnvio(rs);
            }
        }
        return null;
    }

    @Override
    public List<Envio> leerTodos(Connection conn) throws Exception {
        String sql = "SELECT * FROM envio WHERE eliminado = false";
        List<Envio> envios = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                envios.add(mapearEnvio(rs));
            }
        }
        return envios;
    }

    @Override
    public void actualizar(Envio envio, Connection conn) throws Exception {
        String sql = "UPDATE envio SET tracking = ?, empresa = ?, tipo = ?, costo = ?, fecha_despacho = ?, fecha_estimada = ?, estado = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, envio.getTracking());
            stmt.setString(2, envio.getEmpresa().name());
            stmt.setString(3, envio.getTipo().name());
            stmt.setDouble(4, envio.getCosto());
            stmt.setDate(5, Date.valueOf(envio.getFechaDespacho()));
            stmt.setDate(6, Date.valueOf(envio.getFechaEstimada()));
            stmt.setString(7, envio.getEstado().name());
            stmt.setLong(8, envio.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(Long id, Connection conn) throws Exception {
        String sql = "UPDATE envio SET eliminado = true WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
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
}