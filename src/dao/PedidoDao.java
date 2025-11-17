package dao;

import entities.Pedido;
import entities.Envio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.DatabaseConnection;

public class PedidoDao implements GenericDao<Pedido> {

    private final EnvioDao envioDao;
    
    // constructor con inyección de EnvioDao
    public PedidoDao(EnvioDao envioDao) {
        if (envioDao == null) {
            throw new IllegalArgumentException("EnvioDao no puede ser null");
        }
        this.envioDao = envioDao;
    }
    @Override
    public void crear(Pedido pedido) throws Exception {
        String sql = "INSERT INTO pedido (numero, fecha, cliente_nombre, total, estado, envio_id, eliminado) VALUES (?, ?, ?, ?, ?, ?, false)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setPedidoParameters(stmt, pedido);
            
            stmt.executeUpdate();
            setGeneratedId(stmt, pedido);
        }
    }
    
    // Método para creación atómica de pedido con envío
    @Override
    public void crearTx(Pedido pedido, Connection conn) throws Exception {
        String sql = "INSERT INTO pedido (numero, fecha, cliente_nombre, total, estado, envio_id, eliminado) VALUES (?, ?, ?, ?, ?, ?, false)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setPedidoParameters(stmt, pedido);
            
            stmt.executeUpdate();
            setGeneratedId(stmt, pedido);
        }
    }

    @Override
    public Pedido leerPorId(Long id) throws Exception {
        String sql = "SELECT p.*, e.tracking, e.empresa, e.tipo, e.costo, e.fecha_despacho, e.fecha_estimada, e.estado as envio_estado "
                       +" FROM pedido p LEFT JOIN envio e ON p.envio_id = e.id "
                       +" WHERE p.id = ? AND p.eliminado = false AND (e.eliminado = false OR e.id IS NULL)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    return mapearPedidoCompleto(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error: No se pudo obtener pedido por ID: " + id + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Pedido> leerTodos() throws Exception {
        String sql = "SELECT p.*, e.tracking, e.empresa, e.tipo, e.costo, e.fecha_despacho, e.fecha_estimada, e.estado as envio_estado " 
                    + " FROM pedido p LEFT JOIN envio e ON p.envio_id = e.id "
                    + " WHERE p.eliminado = false AND (e.eliminado = false OR e.id IS NULL)";
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pedidos.add(mapearPedidoCompleto(rs));
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
    public void actualizarTx(Pedido pedido, Connection conn) throws Exception {
        String sql = "UPDATE pedido SET numero = ?, fecha = ?, cliente_nombre = ?, total = ?, estado = ?, envio_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    public void actualizarEnvioAsociado(Long pedidoId, Long envioId, Connection conn) throws Exception {
        String sql = "UPDATE pedido SET envio_id = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (envioId != null) {
                pstmt.setLong(1, envioId);
            } else {
                pstmt.setNull(1, Types.BIGINT);
            }
            pstmt.setLong(2, pedidoId);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al actualizar envío asociado: " + e.getMessage(), e);
        }
    }
    
    public Pedido getByEnvioId(Long envioId) throws Exception {
        String sql = "SELECT * FROM pedido WHERE envio_id = ? AND eliminado = false";
        
        // Verificar que id no sea nulo
        if (envioId == null) {
          return null;  
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, envioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPedido(rs);
                }
            }
        }
        return null;
    }
    
    // Métodos de búsqueda para requeridos para opciones de menú
    
    // Buscar pedidos por número
    public Pedido buscarPorNumero(String numero) throws Exception {
        String sql = "SELECT * FROM pedido WHERE numero = ? AND eliminado = false";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPedido(rs);
                }
            }
        }
        return null;
    }
    
    // Buscar pedidos por cliente (busqueda parcial por nombre)
    public List<Pedido> buscarPorCliente(String clienteNombre) throws Exception {
        String sql = "SELECT * FROM pedido WHERE cliente_nombre LIKE ? AND eliminado = false ORDER BY fecha DESC";
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + clienteNombre + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
        }
        return pedidos;
    }
    
    // Buscar pedidos por estado
    public List<Pedido> buscarPorEstado(String estado) throws Exception {
        String sql = "SELECT * FROM pedido WHERE estado = ? AND eliminado = false ORDER BY fecha DESC";
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
        }
        return pedidos;
    }
    
    // Verificar si existe un pedido con el mismo número (excluyendo un id dado, para actualizaciones)
    public boolean existeNumeroPedido(String numero, Long excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pedido WHERE numero = ? AND eliminado = false AND id != ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero);
            stmt.setLong(2, excludeId != null ? excludeId : -1);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Verificar si existe un pedido con el mismo número (para inserciones)
    public boolean existeNumeroPedido(String numero) throws SQLException {
        return existeNumeroPedido(numero, null);
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
    
    private Pedido mapearPedidoCompleto(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getLong("id"));
        pedido.setEliminado(rs.getBoolean("eliminado"));
        pedido.setNumero(rs.getString("numero"));
        pedido.setFecha(rs.getDate("fecha").toLocalDate());
        pedido.setClienteNombre(rs.getString("cliente_nombre"));
        pedido.setTotal(rs.getDouble("total"));
        pedido.setEstado(Pedido.EstadoPedido.valueOf(rs.getString("estado")));

        // Verificar si hay envío y cargarlo completo
        long envioId = rs.getLong("envio_id");
        if (!rs.wasNull()) {
            Envio envio = mapearEnvioDesdeResultSet(rs, envioId);
            pedido.setEnvio(envio);
        }

        return pedido;
    }
    
    // Método para mapear envío desde el ResultSet del JOIN
    private Envio mapearEnvioDesdeResultSet(ResultSet rs, Long envioId) throws SQLException {
        Envio envio = new Envio();
        envio.setId(envioId);
        envio.setTracking(rs.getString("tracking"));
        envio.setEmpresa(Envio.Empresa.valueOf(rs.getString("empresa")));
        envio.setTipo(Envio.TipoEnvio.valueOf(rs.getString("tipo")));
        envio.setCosto(rs.getDouble("costo"));

        // Manejar fechas que podrían ser nulas
        java.sql.Date fechaDespacho = rs.getDate("fecha_despacho");
        if (fechaDespacho != null) {
            envio.setFechaDespacho(fechaDespacho.toLocalDate());
        }

        java.sql.Date fechaEstimada = rs.getDate("fecha_estimada");
        if (fechaEstimada != null) {
            envio.setFechaEstimada(fechaEstimada.toLocalDate());
        }

        envio.setEstado(Envio.EstadoEnvio.valueOf(rs.getString("envio_estado")));
        return envio;
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
        if (pedido.getId() != null) {
            stmt.setLong(7, pedido.getId());
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