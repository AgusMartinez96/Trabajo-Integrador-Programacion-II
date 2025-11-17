package dao;

import entities.Envio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.DatabaseConnection;

public class EnvioDao implements GenericDao<Envio> {

    // Métodos de la interfaz ----------------------
    
    @Override
    public void crear(Envio envio) throws Exception {
        String sql = "INSERT INTO envio (tracking, empresa, tipo, costo, fecha_despacho, fecha_estimada, estado, eliminado) VALUES (?, ?, ?, ?, ?, ?, ?, false)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setEnvioParameters(stmt, envio);
            stmt.executeUpdate();
            setGeneratedId(stmt,envio);
        }
    }

    // Método para creacion atómica de pedido con envio
    @Override
    public void crearTx(Envio envio, Connection conn) throws Exception {
        String sql = "INSERT INTO envio (tracking, empresa, tipo, costo, fecha_despacho, fecha_estimada, estado, eliminado) VALUES (?, ?, ?, ?, ?, ?, ?, false)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setEnvioParameters(stmt, envio);
            stmt.executeUpdate();
            setGeneratedId(stmt,envio);
        }
    }
    
    
    @Override
    public Envio leerPorId(Long id) throws Exception {
        String sql = "SELECT * FROM envio WHERE id = ? AND eliminado = false";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return mapearEnvio(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Envio> leerTodos() throws Exception {
        String sql = "SELECT * FROM envio WHERE eliminado = false";
        List<Envio> envios = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    envios.add(mapearEnvio(rs));
                }
            }
        }
        return envios;
    }

    @Override
    public void actualizar(Envio envio) throws Exception {
        String sql = "UPDATE envio SET tracking = ?, empresa = ?, tipo = ?, costo = ?, fecha_despacho = ?, fecha_estimada = ?, estado = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            setEnvioParameters(stmt, envio);
            stmt.setLong(8, envio.getId());
            stmt.executeUpdate();
        }
    }
    @Override
    public void actualizarTx(Envio envio, Connection conn) throws Exception {
        String sql = "UPDATE envio SET tracking = ?, empresa = ?, tipo = ?, costo = ?, fecha_despacho = ?, fecha_estimada = ?, estado = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setEnvioParameters(stmt, envio);
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
            
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo eliminar el envío. No se encontró envío con ID: " + id);
            }
        }
    }
        
    // Métodos de búsqueda para requeridos para opciones de menú
    
    // Buscar envío por tracking
    public Envio buscarPorTracking(String tracking) throws Exception {
        String sql = "SELECT * FROM envio WHERE tracking = ? AND eliminado = false";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tracking);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearEnvio(rs);
                }
            }
        }
        return null;
    }
    
    // Buscar envíos por empresa
    public List<Envio> buscarPorEmpresa(String empresa) throws Exception {
        String sql = "SELECT * FROM envio WHERE empresa = ? AND eliminado = false ORDER BY fecha_despacho DESC";
        List<Envio> envios = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, empresa);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    envios.add(mapearEnvio(rs));
                }
            }
        }
        return envios;
    }
    
    // Buscar envíos por estado
    public List<Envio> buscarPorEstado(String estado) throws Exception {
        String sql = "SELECT * FROM envio WHERE estado = ? AND eliminado = false ORDER BY fecha_despacho DESC";
        List<Envio> envios = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    envios.add(mapearEnvio(rs));
                }
            }
        }
        return envios;
    }
    
    
    // Métodos privados de la clase ----------------------------
    
    private Envio mapearEnvio(ResultSet rs) throws SQLException {
        Envio envio = new Envio();
        envio.setId(rs.getLong("id"));
        envio.setEliminado(rs.getBoolean("eliminado"));
        envio.setTracking(rs.getString("tracking"));
        envio.setEmpresa(Envio.Empresa.valueOf(rs.getString("empresa")));
        envio.setTipo(Envio.TipoEnvio.valueOf(rs.getString("tipo")));
        envio.setCosto(rs.getDouble("costo"));
        
        // Manejo seguro de fechas
        Date fechaDespacho = rs.getDate("fecha_despacho");
        if (fechaDespacho != null) {
            envio.setFechaDespacho(fechaDespacho.toLocalDate());
        }

        Date fechaEstimada = rs.getDate("fecha_estimada");
        if (fechaEstimada != null) {
            envio.setFechaEstimada(fechaEstimada.toLocalDate());
        }
        
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