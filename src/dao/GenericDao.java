package dao;

import java.sql.Connection;
import java.util.List;

public interface GenericDao<T> {
    void crear(T entidad, Connection conn) throws Exception;
    T leerPorId(Long id, Connection conn) throws Exception;
    List<T> leerTodos(Connection conn) throws Exception;
    void actualizar(T entidad, Connection conn) throws Exception;
    void eliminar(Long id, Connection conn) throws Exception; // Baja lógica
}