package service;

import config.DatabaseConnection;
import dao.EnvioDao;
import entities.Envio;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EnvioService implements GenericService<Envio> {

    private final EnvioDao envioDao;
    
    // Constructor con inyección de dependencias
    public EnvioService(EnvioDao envioDao){
        // Validar que el Dao no esa nulo
        if (envioDao == null) {
            throw new IllegalArgumentException("Error: EnvioDao no puede ser null");
        }
        this.envioDao = envioDao;
    }
    
    // Métodos de la interfaz ----------------------
    // Crear envío con validaciones
    @Override
    public void insertar(Envio envio) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            this.insertar(envio, conn);
            
            conn.commit();
        } catch (Exception e){
            if (conn != null) conn.rollback();
            throw new Exception("Error al insertar envío: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }

    @Override
    public void actualizar(Envio envio) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            this.actualizar(envio, conn);

            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error al actualizar envío: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    @Override
    public void eliminar(Long id) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            this.eliminar(id, conn);

            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error al eliminar envío: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    @Override
    public Envio getById(Long id) throws Exception {
            return envioDao.leerPorId(id);
    }

    @Override
    public List<Envio> getAll() throws Exception {
            return envioDao.leerTodos();
    }
    
    // Métodos de transaccionales para ser usados por otros servicios ----------------------
    
    public void insertar(Envio envio, Connection conn) throws Exception {
        validarEnvio(envio);
        envioDao.crear(envio, conn);
    }
    
    public void actualizar(Envio envio, Connection conn) throws Exception {
        validarEnvio(envio);
        envioDao.actualizar(envio, conn);
    }
    
    public void eliminar(Long id, Connection conn) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID para eliminar no es válido");
        }
        envioDao.eliminar(id, conn);
    }
    
    // Métodos privados de la clase ----------------------------
    private void validarEnvio(Envio envio) {
        if (envio == null) {
            throw new IllegalArgumentException("El envío no puede ser nulo");
        }
        if (envio.getTracking() == null || envio.getTracking().isBlank()) {
            throw new IllegalArgumentException("El tracking no puede estar vacío");
        }
        if (envio.getTracking().length() > 40) {
            throw new IllegalArgumentException("El tracking no puede superar los 40 caracteres");
        }
        if (envio.getCosto() < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo");
        }
        if (envio.getEmpresa() == null || envio.getTipo() == null) {
            throw new IllegalArgumentException("Empresa y tipo de envío son obligatorios");
        }
    }
}