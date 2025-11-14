package dao;

import entities.Pedido;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.DatabaseConnection;

public class PedidoDao implements GenericDao<Pedido> {

    @Override
    public void insertar(Pedido pedido) throws Exception {
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

    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getLong("id"));
        pedido.setEliminado(rs.getBoolean("eliminado"));
        pedido.setNumero(rs.getString("numero"));
        pedido.setFecha(rs.getDate("fecha").toLocalDate());
        pedido.setClienteNombre(rs.getString("cliente_nombre"));
        pedido.setTotal(rs.getDouble("total"));
        pedido.setEstado(Pedido.EstadoPedido.valueOf(rs.getString("estado")));
        // Envio se carga por separado en Service
        return pedido;
    }
    
    private void setPedidoParameters(PreparedStatement stmt, Pedido pedido) throws SQLException {
        stmt.setString(1, pedido.getNumero());
        stmt.setDate(2, Date.valueOf(pedido.getFecha()));
        stmt.setString(3, pedido.getClienteNombre());
        stmt.setDouble(4, pedido.getTotal());
        stmt.setString(5, pedido.getEstado().name());
        stmt.setLong(6, pedido.getEnvio().getId());
    }
}