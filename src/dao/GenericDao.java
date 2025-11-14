package dao;

import java.util.List;

public interface GenericDao<T> {
    void insertar(T entidad) throws Exception;
    T leerPorId(Long id) throws Exception;
    List<T> leerTodos() throws Exception;
    void actualizar(T entidad) throws Exception;
    void eliminar(Long id) throws Exception; // Baja lógica
}