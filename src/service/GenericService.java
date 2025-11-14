
package service;

import java.util.List;

/**
 *
 * @author Andres Meshler
 */
public interface GenericService<T>{
    void insertar(T entidad) throws Exception;
    void actualizar(T entidad) throws Exception;
    void eliminar(Long id) throws Exception;
    T leerPorId(Long id) throws Exception;
    List<T> leerTodos() throws Exception;
}
