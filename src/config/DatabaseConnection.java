package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties props = new Properties();
            if (input == null) {
                throw new RuntimeException("No se encontró el archivo db.properties");
            }
            props.load(input);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            String driver = props.getProperty("db.driver");

            Class.forName(driver); // Carga del driver JDBC

            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new SQLException("Error al conectar con la base de datos: " + e.getMessage(), e);
        }
    }
}