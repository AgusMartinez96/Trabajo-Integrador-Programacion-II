package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Datos para conexión
    private static final String URL = "jdbc:mysql://localhost:3306/tpi_pedido_envio";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    static {
        try {
            // Cargar el driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Validar datos de conexion de manera temprana
            validateConfiguration();
        } catch (ClassNotFoundException e) {
            // Lanzar excepcion si no se encunetra el driver
            throw new RuntimeException("Error: No se encontró el driver JDBC. " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new ExceptionInInitializerError("Error en la configuracion de la base de datos: " + e.getMessage());
        }

    }

    // Método para conectar con la base de datos
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    private static void validateConfiguration(){
        if (URL == null || URL.trim().isEmpty()) {
            throw new IllegalStateException("La URL de la base de datos no está configurada");
        }
        if (USER == null || USER.trim().isEmpty()) {
            throw new IllegalStateException("El usuario de la base de datos no está configurada");
        }
        if (PASSWORD == null) {
            throw new IllegalStateException("La contraseña de la base de datos no está configurada");
        }
    }
    // Constuctor para validar que no se instancie la clase
    private DatabaseConnection() {
        throw new UnsupportedOperationException("Esta es una clase utilitaria y no debe ser instanciada");
    }

}