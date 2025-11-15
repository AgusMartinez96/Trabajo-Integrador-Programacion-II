package dao;

import entities.Pedido;
import entities.Envio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.DatabaseConnection;

public class PedidoDao implements GenericDao<Pedido> {

    @Override
    public void crear(Pedido pedido) throws Exception {
        String sql = "INSERT INTO pedido (numero, fecha, cliente_nombre, total, estado, envio_id, eliminado) VALUES (?, ?, ?, ?, ?, ?, false)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setPedidoParameters(stmt, pedido);
            
            stmt.executeUpdate();
        }
    }

    @Override
    public Pedido leerPorId(Long id) throws Exception {
        String sql = "SELECT * FROM pedido WHERE id = ? AND eliminado = false";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    return mapearPedido(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error: No se pudo obtener pedido por ID: " + id + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Pedido> leerTodos() throws Exception {
        String sql = "SELECT * FROM pedido WHERE eliminado = false";
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pedidos.add(mapearPedido(rs));
            }
        } catch (SQLException e) {
            throw new Exception("Error: No se pudieron obtener todos los pedidos: " + e.getMessage());
        }
        return pedidos;
    }

    @Override
    public void actualizar(Pedido pedido) throws Exception {
        String sql = "UPDATE pedido SET numero = ?, fecha = ?, cliente_nombre = ?, total = ?, estado = ?, envio_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            setPedidoParameters(stmt, pedido);
            
            stmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(Long id) throws Exception {
        String sql = "UPDATE pedido SET eliminado = true WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new SQLException("Error: No se pudo encontrar pedido con ID: " + id);
            }
        }
    }

    // Métodos de transaccionales para usar en la capa de servicios ----------------------
    
    public void crear(Pedido pedido, Connection conn) throws Exception {
        String sql = "INSERT INTO pedido (numero, fecha, cliente_nombre, total, estado, envio_id, eliminado) VALUES (?, ?, ?, ?, ?, ?, false)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setPedidoParameters(stmt, pedido);
            stmt.executeUpdate();
            // VER !!!
            setGeneratedId(stmt, pedido);
        }
    }
    
    public Pedido leerPorId(Long id, Connection conn) throws Exception {
        String sql = "SELECT * FROM pedido WHERE id = ? AND eliminado = false";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPedido(rs);
                }
            }
        }
        return null;
    }
    
    public List<Pedido> leerTodos(Connection conn) throws Exception {
        String sql = "SELECT * FROM pedido WHERE eliminado = false";
        List<Pedido> pedidos = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
        }
        return pedidos;
    }
    
    public void actualizar(Pedido pedido, Connection conn) throws Exception {
        String sql = "UPDATE pedido SET numero = ?, fecha = ?, cliente_nombre = ?, total = ?, estado = ?, envio_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setPedidoParameters(stmt, pedido);
            stmt.setLong(7, pedido.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public void eliminar(Long id, Connection conn) throws Exception {
        String sql = "UPDATE pedido SET eliminado = true WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new SQLException("Error: No se pudo encontrar pedido con ID: " + id);
            }
        }
    }
    
    public Pedido getByEnvioId(Long envioId, Connection conn) throws Exception {
        String sql = "SELECT * FROM pedido WHERE envio_id = ? AND eliminado = false";
        
        // Verificar que id no sea nulo
        if (envioId == null) {
          return null;  
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, envioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPedido(rs);
                }
            }
        }
        return null;
    }
    // Métodos privados de la clase ----------------------------
    
    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getLong("id"));
        pedido.setEliminado(rs.getBoolean("eliminado"));
        pedido.setNumero(rs.getString("numero"));
        pedido.setFecha(rs.getDate("fecha").toLocalDate());
        pedido.setClienteNombre(rs.getString("cliente_nombre"));
        pedido.setTotal(rs.getDouble("total"));
        pedido.setEstado(Pedido.EstadoPedido.valueOf(rs.getString("estado")));
        // Prevenir nullpointerexception
        long envioId = rs.getLong("envio_id");
        if (!rs.wasNull()) {
            // Creamos un objeto Envio "parcial" solo con el ID.
            Envio envioProxy = new Envio();
            envioProxy.setId(envioId);
            pedido.setEnvio(envioProxy);
        }
        return pedido;
    }
    
    private void setPedidoParameters(PreparedStatement stmt, Pedido pedido) throws SQLException {
        stmt.setString(1, pedido.getNumero());
        stmt.setDate(2, Date.valueOf(pedido.getFecha()));
        stmt.setString(3, pedido.getClienteNombre());
        stmt.setDouble(4, pedido.getTotal());
        stmt.setString(5, pedido.getEstado().name());
        // Prevenir nullpointerexception
        if (pedido.getEnvio() != null && pedido.getEnvio().getId() != null && pedido.getEnvio().getId() > 0) {
            stmt.setLong(6, pedido.getEnvio().getId());
        } else {
            // Permite insertar un pedido sin envío
            stmt.setNull(6, java.sql.Types.BIGINT);
        }
    }
    
    private void setGeneratedId(PreparedStatement stmt, Pedido pedido) throws SQLException {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                pedido.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("La inserción del pedido falló, no se obtuvo ID generado");
            }
        }
    }
}