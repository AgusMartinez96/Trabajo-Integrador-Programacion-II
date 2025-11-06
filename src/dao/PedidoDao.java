package dao;

import entities.Pedido;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDao implements GenericDao<Pedido> {

    @Override
    public void crear(Pedido pedido, Connection conn) throws Exception {
        String sql = "INSERT INTO pedido (numero, fecha, cliente_nombre, total, estado, envio_id, eliminado) VALUES (?, ?, ?, ?, ?, ?, false)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, pedido.getNumero());
            stmt.setDate(2, Date.valueOf(pedido.getFecha()));
            stmt.setString(3, pedido.getClienteNombre());
            stmt.setDouble(4, pedido.getTotal());
            stmt.setString(5, pedido.getEstado().name());
            stmt.setLong(6, pedido.getEnvio().getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public Pedido leerPorId(Long id, Connection conn) throws Exception {
        String sql = "SELECT * FROM pedido WHERE id = ? AND eliminado = false";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearPedido(rs);
            }
        }
        return null;
    }

    @Override
    public List<Pedido> leerTodos(Connection conn) throws Exception {
        String sql = "SELECT * FROM pedido WHERE eliminado = false";
        List<Pedido> pedidos = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pedidos.add(mapearPedido(rs));
            }
        }
        return pedidos;
    }

    @Override
    public void actualizar(Pedido pedido, Connection conn) throws Exception {
        String sql = "UPDATE pedido SET numero = ?, fecha = ?, cliente_nombre = ?, total = ?, estado = ?, envio_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pedido.getNumero());
            stmt.setDate(2, Date.valueOf(pedido.getFecha()));
            stmt.setString(3, pedido.getClienteNombre());
            stmt.setDouble(4, pedido.getTotal());
            stmt.setString(5, pedido.getEstado().name());
            stmt.setLong(6, pedido.getEnvio().getId());
            stmt.setLong(7, pedido.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(Long id, Connection conn) throws Exception {
        String sql = "UPDATE pedido SET eliminado = true WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
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
}