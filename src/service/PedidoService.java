package service;

import config.DatabaseConnection;
import dao.PedidoDao;
import dao.EnvioDao;
import entities.Pedido;
import entities.Envio;

import java.sql.Connection;
import java.util.List;

public class PedidoService {

    private final PedidoDao pedidoDao = new PedidoDao();
    private final EnvioDao envioDao = new EnvioDao();

    public void crearPedidoConEnvio(Pedido pedido, Envio envio) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Validaciones de negocio
            if (pedido.getTotal() < 0) {
                throw new IllegalArgumentException("El total no puede ser negativo");
            }

            // Crear Envio primero
            envioDao.crear(envio, conn);
            pedido.setEnvio(envio); // asociar el envio creado

            // Crear Pedido
            pedidoDao.crear(pedido, conn);

            conn.commit();
        } catch (Exception e) {
            throw new Exception("Error al crear pedido con envío: " + e.getMessage(), e);
        }
    }

// Leer pedido por ID
    public Pedido leerPorId(Long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return pedidoDao.leerPorId(id, conn);
        }
    }

    // Leer todos los pedidos
    public List<Pedido> leerTodos() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return pedidoDao.leerTodos(conn);
        }
    }

    // Actualizar pedido
    public void actualizar(Pedido pedido) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            if (pedido.getTotal() < 0) {
                throw new IllegalArgumentException("El total no puede ser negativo");
            }

            pedidoDao.actualizar(pedido, conn);
            conn.commit();
        } catch (Exception e) {
            throw new Exception("Error al actualizar pedido: " + e.getMessage(), e);
        }
    }

    // Eliminar pedido (baja lógica)
    public void eliminar(Long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            pedidoDao.eliminar(id, conn);
        }
    }
}