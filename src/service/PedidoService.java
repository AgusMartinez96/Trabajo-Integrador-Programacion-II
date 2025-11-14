package service;

import dao.PedidoDao;
import entities.Pedido;
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

    @Override
    public void insertar(Pedido pedido) throws Exception {
        validatePedido(pedido);
        
        if (pedido.getEnvio() != null) {
            envioService.insertar(pedido.getEnvio());
        } else {
            envioService.actualizar(pedido.getEnvio());
        }
        pedidoDao.insertar(pedido);
    }

// Leer pedido por ID
    @Override
    public Pedido leerPorId(Long id) throws Exception {
        return pedidoDao.leerPorId(id);
    }

    // Leer todos los pedidos
    @Override
    public List<Pedido> leerTodos() throws Exception {
        return pedidoDao.leerTodos();
    }

    // Actualizar pedido
    @Override
    public void actualizar(Pedido pedido) throws Exception {
        validatePedido(pedido);
            pedidoDao.actualizar(pedido);
    }

    // Eliminar pedido (baja lógica)
    @Override
    public void eliminar(Long id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        pedidoDao.eliminar(id);
    }
    
    // Validar pedido
    private void validatePedido(Pedido pedido) {
        if (pedido.getTotal() < 0) {
            throw new IllegalArgumentException("El total no puede ser negativo");
         }
    }
}