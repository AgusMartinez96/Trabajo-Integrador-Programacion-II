package service;

import dao.EnvioDao;
import entities.Envio;
import entities.Envio.Empresa;
import entities.Envio.EstadoEnvio;
import entities.Envio.TipoEnvio;
import java.sql.Connection;
import java.time.LocalDate;
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
        validarEnvio(envio);
        try {
            envioDao.crear(envio);   
        } catch (Exception e){
            throw new Exception("Error al insertar envío: " + e.getMessage(), e);
        }
    }

    
    public void insertar(Envio envio, Connection conn) throws Exception {
        validarEnvio(envio);
        try {
            envioDao.crearTx(envio, conn);   
        } catch (Exception e){
            throw new Exception("Error al insertar envío: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void actualizar(Envio envio) throws Exception {
        validarEnvio(envio);
        try {
            envioDao.actualizar(envio);
        } catch (Exception e) {
            throw new Exception("Error al actualizar envío: " + e.getMessage(), e);
        } 
    }
    
    @Override
    public void eliminar(Long id) throws Exception {
        try {
            envioDao.eliminar(id);
            
        } catch (Exception e) {
            throw new Exception("Error al eliminar envío: " + e.getMessage(), e);
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
    
    // Métodos de búsqueda para Envio
    public Envio buscarPorTracking(String tracking) throws Exception {
        if (tracking == null || tracking.trim().isEmpty()) {
            throw new IllegalArgumentException("El tracking no puede estar vacío");
        }
        try {
            return envioDao.buscarPorTracking(tracking.trim());
        }catch (Exception e){
            throw new Exception("Error al buscar envío por tracking" + e.getMessage(), e);
        }
    }
    
    public List<Envio> buscarPorEmpresa(String empresa) throws Exception {
        if (empresa == null || empresa.trim().isEmpty()) {
            throw new IllegalArgumentException("La empresa no puede estar vacía");
        }
        try {
            // Validar que la empresa sea válida
            Envio.Empresa.valueOf(empresa.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Empresa inválida. Use: ANDREANI, OCA o CORREO_ARG");
        }

        try  {
            return envioDao.buscarPorEmpresa(empresa.toUpperCase());
        }catch (Exception e){
            throw new Exception("Error al buscar envío por empresa" + e.getMessage(), e);
        }
    }
    
    public List<Envio> buscarPorEstado(String estado) throws Exception {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede estar vacío");
        }
        try {
            // Validar que el estado sea válido
            Envio.EstadoEnvio.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido. Use: EN_PREPARACION, EN_TRANSITO o ENTREGADO");
        }

        try {
            return envioDao.buscarPorEstado(estado.toUpperCase());
        }catch (Exception e){
            throw new Exception("Error al buscar envío por estado" + e.getMessage(), e);
        }
    }
    
    public void validarEnvio(Envio envio) {
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
    
    // Método para actualización parcial de envío
    public void actualizarDatosEnvio(Long envioId, String tracking, Double costo, Empresa empresa,
            TipoEnvio tipo, EstadoEnvio estado, LocalDate fechaDespacho, LocalDate fechaEstimada) throws Exception {
        try {
            
            Envio envio = envioDao.leerPorId(envioId);
            if (envio == null) {
                throw new Exception("No se encontró el envío con ID: " + envioId);
            }

            // Aplicar cambios solo si se proporcionan nuevos valores
            if (tracking != null) envio.setTracking(tracking);
            if (costo != null) {
                if (costo < 0) throw new IllegalArgumentException("El costo no puede ser negativo");
                envio.setCosto(costo);
            }
            if (empresa != null) envio.setEmpresa(empresa);
            if (tipo != null) envio.setTipo(tipo);
            if (estado != null) envio.setEstado(estado);
            if (fechaDespacho != null) envio.setFechaDespacho(fechaDespacho);
            if (fechaEstimada != null) envio.setFechaEstimada(fechaEstimada);

            // Validar el envío completo después de los cambios
            validarEnvio(envio);

            envioDao.actualizar(envio);

        } catch (Exception e) {
            throw e;
        } 
    }
}