
package main;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import config.DatabaseConnection;
/**
 *
 * @author Andres Meshler
 */
public class TestConexion {
    public static void main(String[] args) {
    // Se usa un bloque try-with-resources para asegurarse de que la conexion
    // se cierra automáticamente al salir.
    
        try (Connection conn = DatabaseConnection.getConnection()){
            if (conn != null) {
                System.out.println("Conexion establecida con exito");

                DatabaseMetaData metaData = conn.getMetaData();
                    System.out.println("Usuario conectado: " + metaData.getUserName());
                    System.out.println("Base de datos: " + conn.getCatalog());
                    System.out.println("URL: " + metaData.getURL());
                    System.out.println("Driver: " + metaData.getDriverName() + " v" + metaData.getDriverVersion());
            } else {
                System.out.println("No se pudo establecer la conexion");
            }


        }catch(SQLException e){
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();// para depurar
        }
    }
}
