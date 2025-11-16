# Documentación Técnica - Sistema de Gestión de Pedidos y Envíos

## Arquitectura Técnica

### Patrones de Diseño Implementados

#### 1. Arquitectura en Capas
```
// Capa de Presentación
main.AppMenu → main.MenuHandler → main.MenuDisplay

// Capa de Servicio
service.PedidoService → service.EnvioService

// Capa de DAO
dao.PedidoDao → dao.EnvioDao

// Capa de Entidades
entities.Pedido → entities.Envio → entities.GenericEntity
```
#### 2. Generic Programming
```
public interface GenericDao<T> {
    void crear(T entidad) throws Exception;
    T leerPorId(Long id) throws Exception;
    List<T> leerTodos() throws Exception;
    void actualizar(T entidad) throws Exception;
    void eliminar(Long id) throws Exception;
}

public interface GenericService<T> {
    void insertar(T entidad) throws Exception;
    void actualizar(T entidad) throws Exception;
    void eliminar(Long id) throws Exception;
    T getById(Long id) throws Exception;
    List<T> getAll() throws Exception;
}
```
#### 3. Data Access Object (DAO)
```
public class PedidoDao implements GenericDao<Pedido> {
    // Implementación específica para Pedido
    public Pedido buscarPorNumero(String numero, Connection conn) throws Exception;
    public List<Pedido> buscarPorCliente(String clienteNombre, Connection conn) throws Exception;
}
```
## Flujos Transaccionales Críticos
#### Flujo 1: Crear Pedido con Envío
```
// En PedidoService.insertar()
Connection conn = DatabaseConnection.getConnection();
conn.setAutoCommit(false);

try {
    if (pedido.getEnvio() != null) {
        envioService.insertar(pedido.getEnvio(), conn);
        validarEnvioUnico(pedido.getEnvio().getId(), conn);
    }
    
    pedidoDao.crear(pedido, conn);
    conn.commit();
} catch (Exception e) {
    conn.rollback();
    throw e;
} finally {
    conn.close();
}
```
#### Flujo 2: Eliminación Segura
```
// En PedidoService.eliminar()
public void eliminar(Long id) throws Exception {
    Pedido pedido = pedidoDao.leerPorId(id);
    
    Connection conn = DatabaseConnection.getConnection();
    conn.setAutoCommit(false);
    
    try {
        if (pedido.getEnvio() != null) {
            envioService.eliminar(pedido.getEnvio().getId(), conn);
        }
        
        pedidoDao.eliminar(id, conn);
        conn.commit();
    } catch (Exception e) {
        conn.rollback();
        throw e;
    } finally {
        conn.close();
    }
}
```
### Modelo de Datos Detallado
#### Esquema de Base de Datos
# Tabla: envio
```
CREATE TABLE envio (
    id BIGINT NOT NULL AUTO_INCREMENT,
    eliminado BOOLEAN DEFAULT FALSE,
    tracking VARCHAR(40) UNIQUE,
    empresa VARCHAR(20) NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    costo DECIMAL(10, 2),
    fecha_despacho DATE,
    fecha_estimada DATE,
    estado VARCHAR(20) NOT NULL,

    PRIMARY KEY (id)
);
```
# Tabla: pedido
```
CREATE TABLE pedido (
    id BIGINT NOT NULL AUTO_INCREMENT,
    eliminado BOOLEAN DEFAULT FALSE,
    numero VARCHAR(20) NOT NULL UNIQUE,
    fecha DATE NOT NULL,
    cliente_nombre VARCHAR(120) NOT NULL,
    total DECIMAL(12, 2) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    envio_id BIGINT,
    PRIMARY KEY (id),
    
    -- Definición de la relación 1-a-1
    CONSTRAINT fk_pedido_envio
        FOREIGN KEY (envio_id) 
        REFERENCES envio(id)
        ON DELETE CASCADE,
        
    UNIQUE KEY uk_envio_id (envio_id)
);
```
#### Entidades del Dominio
# GenericEntity
```
public abstract class GenericEntity {
    private Long id;
    private boolean eliminado;
    
    // Constructores, getters y setters
}
```
# Envio
```
public class Envio extends GenericEntity {
    private String tracking;
    private Empresa empresa; // ANDREANI, OCA, CORREO_ARG
    private TipoEnvio tipo; // ESTANDAR, EXPRES
    private double costo;
    private LocalDate fechaDespacho;
    private LocalDate fechaEstimada;
    private EstadoEnvio estado; // EN_PREPARACION, EN_TRANSITO, ENTREGADO
    
    // Enums internos
    public enum Empresa { ANDREANI, OCA, CORREO_ARG }
    public enum TipoEnvio { ESTANDAR, EXPRES }
    public enum EstadoEnvio { EN_PREPARACION, EN_TRANSITO, ENTREGADO }
}
```
# Pedido
```
public class Pedido extends GenericEntity {
    private String numero;
    private LocalDate fecha;
    private String clienteNombre;
    private double total;
    private EstadoPedido estado; // NUEVO, FACTURADO, ENVIADO
    private Envio envio;
    
    public enum EstadoPedido { NUEVO, FACTURADO, ENVIADO }
}
```
#### Componentes Técnicos
## Gestión de Conexiones
# DatabaseConnection
```
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/tpi_pedido_envio";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```
## Operaciones DAO Principales
# EnvioDao - Métodos Transaccionales
```
public void crear(Envio envio, Connection conn) throws Exception {
    String sql = "INSERT INTO envio (tracking, empresa, tipo, costo, fecha_despacho, fecha_estimada, estado, eliminado) VALUES (?, ?, ?, ?, ?, ?, ?, false)";
    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        setEnvioParameters(stmt, envio);
        stmt.executeUpdate();
        setGeneratedId(stmt, envio);
    }
}
```
# PedidoDao - Búsquedas Especializadas
```
public Pedido buscarPorNumero(String numero, Connection conn) throws Exception {
    String sql = "SELECT * FROM pedido WHERE numero = ? AND eliminado = false";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, numero);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapearPedido(rs);
            }
        }
    }
    return null;
}
```
#### Servicios de Negocio
## Validaciones en EnvioService
```
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
}
```
## Coordinación en PedidoService
```
private void validarEnvioUnico(Long envioId, Connection conn) throws Exception {
    if (envioId == null) return;
    
    Pedido pedidoExistente = pedidoDao.getByEnvioId(envioId, conn);
    
    if (pedidoExistente != null) {
        throw new Exception("Error: El envío con ID " + envioId + " ya está asignado al pedido " + pedidoExistente.getId());
    }
}
```
#### Consultas SQL Principales
# Consultas de Pedido
```
-- Leer pedido completo con JOIN
SELECT p.*, e.tracking, e.empresa, e.tipo, e.costo, 
       e.fecha_despacho, e.fecha_estimada, e.estado as envio_estado
FROM pedido p 
LEFT JOIN envio e ON p.envio_id = e.id
WHERE p.eliminado = false AND (e.eliminado = false OR e.id IS NULL)

-- Buscar por número
SELECT * FROM pedido WHERE numero = ? AND eliminado = false

-- Buscar por cliente (parcial)
SELECT * FROM pedido WHERE cliente_nombre LIKE ? AND eliminado = false
Consultas de Envío
sql
-- Buscar por tracking
SELECT * FROM envio WHERE tracking = ? AND eliminado = false

-- Buscar por empresa
SELECT * FROM envio WHERE empresa = ? AND eliminado = false

-- Buscar por estado
SELECT * FROM envio WHERE estado = ? AND eliminado = false
```
#### Manejo de Excepciones
# Estrategia de Manejo de Errores
```
public class MenuHandler {
    public void crearPedidoConEnvio() throws Exception {
        try {
            // Lógica de creación
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un número válido.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }
}
```
# Validaciones en Servicio
```
public class PedidoService {
    public void validarNumeroPedidoUnico(String numero) throws Exception {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de pedido no puede estar vacío");
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (pedidoDao.existeNumeroPedido(numero, conn)) {
                throw new Exception("Ya existe un pedido con el número: " + numero);
            }
        }
    }
}
```
#### Configuración y Build
```
plugins {
    id 'java'
    id 'application'
}

group = 'com.sistema.pedidos'
version = '1.0.0'

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'mysql:mysql-connector-java:8.0.33'
}

application {
    mainClass = 'main.Main'
}
```
#### Estructura de Paquetes
src
├── config/
│   └── DatabaseConnection.java
├── dao/
│   ├── GenericDao.java
│   ├── EnvioDao.java
│   └── PedidoDao.java
├── entities/
│   ├── GenericEntity.java
│   ├── Envio.java
│   └── Pedido.java
├── main/
│   ├── AppMenu.java
│   ├── Main.java
│   ├── MenuDisplay.java
│   ├── MenuHandler.java
│   └── TestConexion.java
└── service/
    ├── GenericService.java
    ├── EnvioService.java
    └── PedidoService.java
	
####Patrones y Buenas Prácticas
# 1. Inyección de Dependencias
```
public class PedidoService {
    private final PedidoDao pedidoDao;
    private final EnvioService envioService;
    
    public PedidoService(PedidoDao pedidoDao, EnvioService envioService) {
        this.pedidoDao = pedidoDao;
        this.envioService = envioService;
    }
}
```
# 2. Try-with-Resources
```
public Envio leerPorId(Long id) throws Exception {
    try (Connection conn = DatabaseConnection.getConnection()) {
        return this.leerPorId(id, conn);
    }
}
```
# 3. PreparedStatements (100%)
```
public void crear(Envio envio, Connection conn) throws Exception {
    String sql = "INSERT INTO envio (tracking, empresa, tipo, costo, fecha_despacho, fecha_estimada, estado, eliminado) VALUES (?, ?, ?, ?, ?, ?, ?, false)";
    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        setEnvioParameters(stmt, envio);
        stmt.executeUpdate();
        setGeneratedId(stmt, envio);
    }
}
```
# 4. Soft Delete Pattern
```
public void eliminar(Long id, Connection conn) throws Exception {
    String sql = "UPDATE pedido SET eliminado = true WHERE id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setLong(1, id);
        int rowsAffected = stmt.executeUpdate();
        
        if (rowsAffected == 0) {
            throw new SQLException("No se pudo encontrar pedido con ID: " + id);
        }
    }
}
```
#### Testing y Calidad
# Verificación de Conexión
```
public class TestConexion {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Conexión establecida con éxito");
                DatabaseMetaData metaData = conn.getMetaData();
                System.out.println("Usuario conectado: " + metaData.getUserName());
                System.out.println("Base de datos: " + conn.getCatalog());
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
    }
}
```
#### Performance y Optimización
# Índices Recomendados
```
CREATE INDEX idx_pedido_numero ON pedido(numero);
CREATE INDEX idx_pedido_cliente ON pedido(cliente_nombre);
CREATE INDEX idx_pedido_estado ON pedido(estado);
CREATE INDEX idx_envio_tracking ON envio(tracking);
CREATE INDEX idx_envio_empresa ON envio(empresa);
CREATE INDEX idx_envio_estado ON envio(estado);
```
# Optimización de Consultas
Uso de LEFT JOIN para relaciones opcionales

Filtrado por eliminado = false en todas las consultas

Prepared statements para reutilización de planes de ejecución

Transacciones para operaciones múltiples

#### Seguridad
# Prevención de SQL Injection
100% uso de PreparedStatements

Validación de parámetros en capa de servicio

Escape de caracteres especiales automático

# Validación de Datos
```
private void validatePedido(Pedido pedido) {
    if (pedido == null) {
        throw new IllegalArgumentException("El pedido no puede ser nulo");
    }
    if (pedido.getTotal() < 0) {
        throw new IllegalArgumentException("El total no puede ser negativo");
    }
}
```
#### Mantenimiento y Extensibilidad
# Agregar Nueva Entidad
Crear clase en entities/ extendiendo GenericEntity

Implementar GenericDao para la nueva entidad

Implementar GenericService para la nueva entidad

Agregar funcionalidad en MenuHandler

# Modificar Reglas de Negocio
Las reglas de negocio están centralizadas en los servicios

Las validaciones están en métodos específicos

Fácil modificación sin afectar otras capas

#### Troubleshooting Avanzado
# Problemas de Conexión
```
# Verificar que MySQL esté corriendo
sudo systemctl status mysql

# Verificar permisos de usuario
mysql -u root -p -e "SHOW GRANTS"

# Probar conexión manualmente
mysql -h localhost -u root -p tpi_pedido_envio
```
# Problemas de Performance
Revisar índices en tablas

Verificar planes de ejecución de consultas

Monitorear conexiones simultáneas

# Debugging Transacciones
```
// Agregar logging en operaciones transaccionales
System.out.println("Iniciando transacción para pedido ID: " + pedido.getId());
conn.setAutoCommit(false);
// ... operaciones
conn.commit();
System.out.println("Transacción completada exitosamente");
```
---
## Documentación Relacionada
- **README.md**: Guía de instalación, configuración y uso
- **HISTORIAS_DE_USUARIO.md**: Especificaciones funcionales
---
**Versión**: 1.0
**Java**: 17+
**MySQL**: 8.x
**Proyecto Educativo** - Trabajo Práctico Integrador de Programación 2