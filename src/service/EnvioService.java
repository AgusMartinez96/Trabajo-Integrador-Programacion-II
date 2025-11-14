package service;

import config.DatabaseConnection;
import dao.EnvioDao;
import dao.GenericDao;
import entities.Envio;

import java.sql.Connection;
import java.util.List;

public class EnvioService implements GenericService<Envio> {

    private final GenericDao<Envio> envioDao;
    
    // Constructor con inyección de dependencias
    public EnvioService(GenericDao<Envio> envioDao){
        // Validar que el Dao no esa nulo
        if (envioDao == null) {
            throw new IllegalArgumentException("Error: EnvioDao no puede ser null");
        }
        this.envioDao = envioDao;
    }
    
    

    // Crear envío con validaciones
    @Override
    public void insertar(Envio envio) throws Exception {
        validarEnvio(envio);
        envioDao.insertar(envio);
    }

    // Leer envío por ID
    public Envio leerPorId(Long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return envioDao.leerPorId(id);
        }
    }

    // Leer todos los envíos
    public List<Envio> leerTodos() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return envioDao.leerTodos();
        }
    }

    // Actualizar envío con validaciones
    public void actualizar(Envio envio) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Validaciones de negocio
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

            envioDao.actualizar(envio);
            conn.commit();
        } catch (Exception e) {
            throw new Exception("Error al actualizar envío: " + e.getMessage(), e);
        }
    }

    // Eliminar envío (baja lógica)
    public void eliminar(Long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            envioDao.eliminar(id);
        }
    }
    private void validarEnvio(Envio envio){
        // Validaciones de negocio
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