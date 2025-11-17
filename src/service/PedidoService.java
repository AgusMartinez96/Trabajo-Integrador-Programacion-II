package service;

import dao.PedidoDao;
import entities.Pedido;
import entities.Envio;
import entities.Pedido.EstadoPedido;
import config.DatabaseConnection;
import java.sql.Connection;
import java.time.LocalDate;
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
        validarNumeroPedidoUnico(pedido.getNumero());
        // Se maneja la conexion desde acá para lograr atomicidad en el insert pedido con envio
        Connection conn = null;
        try {
            
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            // Crear pedido
            pedidoDao.crearTx(pedido, conn);
            // Logica de negocio
            if (pedido.getEnvio() != null) {
                envioService.insertar(pedido.getEnvio(), conn);
                validarEnvioUnico(pedido.getEnvio().getId());
                asociarEnvioAPedido(pedido.getId(), pedido.getEnvio().getId(), conn);
            }
            
            conn.commit();
            
        } catch (Exception e) {
            throw new Exception("Error al crear el pedido: " + e.getMessage(), e);
        } 
    }
    
     // Actualizar pedido
    @Override
    public void actualizar(Pedido pedido) throws Exception {
    validatePedido(pedido);

    try {

        // Actualizar el Envío (si existe)
        if (pedido.getEnvio() != null) {
            // Si el ID es nulo, deberíamos insertarlo.
            if (pedido.getEnvio().getId() == null) {
                envioService.insertar(pedido.getEnvio());

                // (REGLA 1-a-1)
                validarEnvioUnico(pedido.getEnvio().getId());
            } else {
                envioService.actualizar(pedido.getEnvio());
            }
        }

        pedidoDao.actualizar(pedido);

    } catch (Exception e) {
        throw new Exception("Error al actualizar el pedido: " + e.getMessage(), e);
    }
}

    // Eliminar pedido (baja lógica)
    @Override
    public void eliminar(Long id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        
        // Primero obtenemos el pedido para ver si tiene envío asociado
        Pedido pedido = pedidoDao.leerPorId(id);
        if (pedido == null) {
            throw new Exception("No se encontró el pedido con ID: " + id);
        }
        try {
            // Si el pedido tiene un envío asociado, lo eliminamos primero
            if (pedido.getEnvio() != null) {
                try {
                    Long envioId = pedido.getEnvio().getId();
                    System.out.println("Eliminando envío asociado ID: " + envioId);
                    envioService.eliminar(envioId);
                    System.out.println("Envío eliminado correctamente.");
                } catch (Exception e) {
                    throw new Exception("Error al eliminar el envío asociado: " + e.getMessage());
                }
            }
            
            // Eliminar el Pedido
            pedidoDao.eliminar(id);
            
        } catch (Exception e) {
            throw new Exception("Error al eliminar el pedido: " + e.getMessage(), e);
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
    
    // Métodos de búsqueda para Pedido
    public Pedido buscarPorNumero(String numero) throws Exception {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de pedido no puede estar vacío");
        }
        
        return pedidoDao.buscarPorNumero(numero.trim());
        
    }
    
    public List<Pedido> buscarPorCliente(String clienteNombre) throws Exception {
        if (clienteNombre == null || clienteNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío");
        }
        return pedidoDao.buscarPorCliente(clienteNombre.trim());
    }
    
    public List<Pedido> buscarPorEstado(String estado) throws Exception {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede estar vacío");
        }
        try {
            // Validar que el estado sea válido
            Pedido.EstadoPedido.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido. Use: NUEVO, FACTURADO o ENVIADO");
        }
        
        return pedidoDao.buscarPorEstado(estado.toUpperCase());
    }
    
    // Métodos para actualización parcial de pedido
    public void actualizarDatosBasicos(Long pedidoId, String numero, LocalDate fecha, String clienteNombre, Double total, EstadoPedido estado) throws Exception {
        
        try {
            
            Pedido pedido = pedidoDao.leerPorId(pedidoId);
            if (pedido == null) {
                throw new Exception("No se encontró el pedido con ID: " + pedidoId);
            }

            // Validar unicidad si se está cambiando el número
            if (numero != null && !numero.equals(pedido.getNumero())) {
                validarNumeroPedidoUnico(numero, pedidoId); // Validar con exclusión del pedido actual
                pedido.setNumero(numero);
            }
            
            // Aplicar cambios solo si se proporcionan nuevos valores
            if (numero != null) pedido.setNumero(numero);
            if (fecha != null) pedido.setFecha(fecha);
            if (clienteNombre != null) pedido.setClienteNombre(clienteNombre);
            if (total != null) {
                if (total < 0) throw new IllegalArgumentException("El total no puede ser negativo");
                pedido.setTotal(total);
            }
            if (estado != null) pedido.setEstado(estado);

            // Validar el pedido completo después de los cambios
            validatePedido(pedido);

            pedidoDao.actualizar(pedido);

        } catch (Exception e) {
            throw e;
        } 
    }

    // Método para agregar envío a un pedido
    public void agregarEnvioAPedido(Long pedidoId, Envio envio) throws Exception {
        // Gestionar la tx para una actualizacion atómica de pedido y envio
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            Pedido pedido = pedidoDao.leerPorId(pedidoId);
            if (pedido == null) {
                throw new Exception("No se encontró el pedido con ID: " + pedidoId);
            }

            if (pedido.getEnvio() != null) {
                throw new Exception("El pedido ya tiene un envío asociado");
            }

            // Validar y crear el envío
            envioService.validarEnvio(envio);
            envioService.insertar(envio, conn);

            // Validar que el envío sea único
            validarEnvioUnico(envio.getId());

            // Asignar el envío al pedido
            asociarEnvioAPedido(pedidoId, envio.getId(), conn);
            //pedidoDao.actualizarTx(pedido, conn);

            conn.commit();

        } catch (Exception e) {
            throw e;
        } 
    }

    // Método para quitar envío de un pedido
    public void quitarEnvioDePedido(Long pedidoId) throws Exception {

        try {
            
            Pedido pedido = pedidoDao.leerPorId(pedidoId);
            if (pedido == null) {
                throw new Exception("No se encontró el pedido con ID: " + pedidoId);
            }

            if (pedido.getEnvio() == null) {
                throw new Exception("El pedido no tiene envío asociado");
            }

            // Quitar el envío del pedido (NO eliminamos el envío de la base de datos)
            pedido.setEnvio(null);
            pedidoDao.actualizar(pedido);

        } catch (Exception e) {
            throw e;
        } 
    }
    
     // Método para validar que el número de pedido sea único (para inserción)
    public void validarNumeroPedidoUnico(String numero) throws Exception {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de pedido no puede estar vacío");
        }

        try {
            if (pedidoDao.existeNumeroPedido(numero)) {
                throw new Exception("Ya existe un pedido con el número: " + numero + 
                                  ". Por favor, use un número diferente.");
            }
        } catch (Exception e) {
            throw new Exception("Error al validar número de pedido: " + e.getMessage(), e);
        }
    }

    // Método para validar unicidad excluyendo el pedido actual (para actualización)
    private void validarNumeroPedidoUnico(String numero, Long excludePedidoId) throws Exception {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de pedido no puede estar vacío");
        }

        if (pedidoDao.existeNumeroPedido(numero, excludePedidoId)) {
            throw new Exception("Ya existe otro pedido con el número: " + numero + 
                              ". Por favor, use un número diferente.");
        }
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
    
    private void validarEnvioUnico(Long envioId) throws Exception {
        if (envioId == null) return; // No se puede validar
        
        Pedido pedidoExistente = pedidoDao.getByEnvioId(envioId);
        
        if (pedidoExistente != null) {
            throw new Exception("Error: El envío con ID " + envioId + " ya está asignado al pedido " + pedidoExistente.getId());
        }
    }
    
    private void asociarEnvioAPedido(Long pedidoId, Long envioId, Connection conn) throws Exception {
        pedidoDao.actualizarEnvioAsociado(pedidoId, envioId, conn);
    }
    
}