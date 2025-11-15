package service;

import config.DatabaseConnection;
import dao.PedidoDao;
import entities.Pedido;
import entities.Envio;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PedidoService implements GenericService<Pedido> {

    private final PedidoDao pedidoDao;
    private final EnvioService envioService;
    // Constructor con inyeccion de dependencias
    public PedidoService(PedidoDao pedidoDao, EnvioService envioService) {
            // Validaciones de negocio
            if (pedidoDao == null) {
                throw new IllegalArgumentException("PedidoDao no puede ser null");
            }
            if (envioService == null) {
                throw new IllegalArgumentException("EnvioService no puede ser null");
            }
            
            this.pedidoDao = pedidoDao;
            this.envioService = envioService;
       
    }

    // Métodos de la interfaz ----------------------
    @Override
    public void insertar(Pedido pedido) throws Exception {
        validatePedido(pedido);
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Logica de negocio
            if (pedido.getEnvio() != null) {
                // Llamar a envio service con la conexion
                envioService.insertar(pedido.getEnvio(), conn);
                
                // Regla 1 a 1: validar que el envio no esté en otro pedido
                validarEnvioUnico(pedido.getEnvio().getId(), conn);
            }
            // Crear pedido
            pedidoDao.crear(pedido, conn);
            
            // Confirmar si no hay error
            conn.commit();
        } catch (Exception e) {
            // si algo falló se revierte la transaccion
            if (conn != null) conn.rollback();
            throw new Exception("Error al crear el pedido: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
    
     // Actualizar pedido
    @Override
    public void actualizar(Pedido pedido) throws Exception {
        validatePedido(pedido);
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Actualizar el Envío (si existe)
            if (pedido.getEnvio() != null) {
                // Si el ID es nulo, deberíamos insertarlo.
                if (pedido.getEnvio().getId() == null) {
                    envioService.insertar(pedido.getEnvio(), conn);
                    
                    // (REGLA 1-a-1)
                    validarEnvioUnico(pedido.getEnvio().getId(), conn);
                } else {
                    envioService.actualizar(pedido.getEnvio(), conn);
                }
            }
        
            pedidoDao.actualizar(pedido);
            conn.commit();
            
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error al actualizar el pedido: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }

    // Eliminar pedido (baja lógica)
    @Override
    public void eliminar(Long id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Eliminar el Pedido
            pedidoDao.eliminar(id, conn);
            
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error al eliminar el pedido: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    // Leer pedido por ID
    @Override
    public Pedido getById(Long id) throws Exception {
        return pedidoDao.leerPorId(id);
    }

    // Leer todos los pedidos
    @Override
    public List<Pedido> getAll() throws Exception {
        return pedidoDao.leerTodos();
    }

    // Métodos privados de la clase ------------------------
    
    // Validar pedido
    private void validatePedido(Pedido pedido) {
        if (pedido == null) {
             throw new IllegalArgumentException("El pedido no puede ser nulo");
        }
        if (pedido.getTotal() < 0) {
            throw new IllegalArgumentException("El total no puede ser negativo");
         }
    }
    
    private void validarEnvioUnico(Long envioId, Connection conn) throws Exception {
        if (envioId == null) return; // No se puede validar
        
        Pedido pedidoExistente = pedidoDao.getByEnvioId(envioId, conn);
        
        if (pedidoExistente != null) {
            throw new Exception("Error: El envío con ID " + envioId + " ya está asignado al pedido " + pedidoExistente.getId());
        }
    }
}