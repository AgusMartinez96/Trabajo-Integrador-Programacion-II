# Sistema de Gestión de Pedidos y Envíos

## Trabajo Práctico Integrador - Programación 2

### Descripción del Proyecto

Este Trabajo Práctico Integrador tiene como objetivo demostrar la aplicación práctica de los conceptos fundamentales de Programación Orientada a Objetos y Persistencia de Datos aprendidos durante el cursado de Programación 2.  
El proyecto consiste en desarrollar un sistema completo de gestión de **pedidos y envíos**, permitiendo realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre estas entidades, implementando una arquitectura robusta y profesional.

---

### Objetivos Académicos

- **Arquitectura en Capas (Layered Architecture)**  
  - Presentación (Main/UI): interacción con el usuario mediante consola  
  - Lógica de Negocio (Service): validaciones y reglas de negocio  
  - Acceso a Datos (DAO): operaciones de persistencia con JDBC  
  - Modelo (Entities): representación de entidades del dominio  

- **Programación Orientada a Objetos**  
  - Principios SOLID  
  - Herencia mediante clase abstracta `Base`  
  - Interfaces genéricas (`GenericDAO`, `GenericService`)  
  - Encapsulamiento y métodos de acceso  
  - Sobrescritura de `equals`, `hashCode`, `toString`  

- **Persistencia con JDBC**  
  - Conexión a MySQL  
  - PreparedStatements para prevenir SQL Injection  
  - Transacciones con commit y rollback  
  - Relaciones 1–1 entre entidades (`Pedido` ↔ `Envio`)  

---

## Requisitos del Sistema
Componente  	Versión Requerida
Java JDK	    17 o superior
MySQL	        8.0 o superior
Gradle	        8.12 (wrapper incluido)
SO	            Windows, Linux o macOS

## Instalación

### 1. Configurar Base de Datos

```sql
CREATE DATABASE IF NOT EXISTS tpi_pedido_envio;
USE tpi_pedido_envio;

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
    CONSTRAINT fk_pedido_envio FOREIGN KEY (envio_id) REFERENCES envio(id),
    UNIQUE KEY uk_envio_id (envio_id)
);

### 2. Compilar el Proyecto

```bash
# Linux/macOS
./gradlew clean build

# Windows
gradlew.bat clean build
```

### 3. Configurar Conexión (Opcional)

Por defecto conecta a:
- **Host**: localhost:3306
- **Base de datos**: dbtpi3
- **Usuario**: root
- **Contraseña**: (vacía)

Para cambiar la configuración, usar propiedades del sistema:

```bash
java -Ddb.url=jdbc:mysql://localhost:3306/dbtpi3 \
     -Ddb.user=usuario \
     -Ddb.password=clave \
     -cp ...
```

## Ejecución

### Opción 1: Desde IDE
1. Abrir proyecto en IntelliJ IDEA o Eclipse
2. Ejecutar clase `Main.Main`

## Uso del Sistema

### Menú Principal  <!-- Modificar al hacer el menu -->

========= MENU =========
1. Crear pedido
2. Listar pedidos
3. Actualizar pedido
4. Eliminar pedido
5. Crear envío
6. Listar envíos
7. Actualizar envío
8. Eliminar envío
0. Salir

### Operaciones Disponibles

#### 1. Crear Pedido
- Captura número, fecha, cliente, total y estado
- Permite asociar un envío opcionalmente
- Valida número único de pedido

**Ejemplo:**
Número: PED-001 
Fecha: 2025-11-14 
Cliente: Juan Pérez 
Total: 15000.00 
Estado: NUEVO 
¿Desea asociar un envío? (s/n): s 
Empresa: OCA 
Tipo: Express 
Tracking: TRK12345


#### 2. Listar Pedidos
Dos opciones:
- **(1) Listar todos**: Muestra todos los pedidos activos
- **(2) Buscar**: Filtra por número o cliente

**Ejemplo de búsqueda:**
Ingrese texto a buscar: Juan
**Resultado:**
ID: 1, Número: PED-001, Cliente: Juan Pérez, Total: 15000.00, Estado: NUEVO Envío: OCA, Tracking: TRK12345


#### 3. Actualizar Pedido
- Permite modificar cliente, total, estado
- Permite actualizar o asociar un envío
- Presionar Enter sin escribir mantiene el valor actual

**Ejemplo:**
ID del pedido a actualizar: 1 
Nuevo cliente (actual: Juan Pérez, Enter para mantener): 
Nuevo total (actual: 15000.00, Enter para mantener): 18000.00 
Nuevo estado (actual: NUEVO, Enter para mantener): FACTURADO 
¿Desea actualizar el envío? (s/n): n


#### 4. Eliminar Pedido
- Eliminación lógica (marca como eliminado, no borra físicamente)
- Requiere ID del pedido

#### 5. Crear Envío
- Captura empresa, tipo, costo, fechas y estado
- Valida tracking único

**Ejemplo:**
Empresa: OCA 
Tipo: Express 
Costo: 1200.00 
Fecha despacho: 2025-11-14 
Fecha estimada: 2025-11-20 
Estado: EN_PREPARACION 
Tracking: TRK12345


#### 6. Listar Envíos
- Muestra todos los envíos activos con ID, empresa, tipo y estado

#### 7. Actualizar Envío
- Permite modificar empresa, tipo, costo, fechas y estado
- Requiere ID del envío

#### 8. Eliminar Envío
-  **ADVERTENCIA**: Puede dejar referencias huérfanas si está asociado a un pedido
-  Usar eliminación segura desde el pedido como alternativa

## Arquitectura

### Estructura en Capas

```
┌─────────────────────────────────────┐
│     Main / UI Layer                 │
│  (Interacción con usuario)          │
│  AppMenu, MenuHandler, MenuDisplay  │
└───────────┬─────────────────────────┘
            │
┌───────────▼─────────────────────────┐
│     Service Layer                   │
│  (Lógica de negocio y validación)   │
│  PedidoService                      │
│  EnvioService                       │
└───────────┬─────────────────────────┘
            │
┌───────────▼─────────────────────────┐
│     DAO Layer                       │
│  (Acceso a datos)                   │
│  PedidoDAO, EnvioDAO                │
└───────────┬─────────────────────────┘
            │
┌───────────▼─────────────────────────┐
│     Models Layer                    │
│  (Entidades de dominio)             │
│  Pedido, Envio, Base                │
└─────────────────────────────────────┘
```

### Componentes Principales

**Config/**
- `DatabaseConnection.java`: Gestión de conexiones JDBC con validación en inicialización estática
- `TransactionManager.java`: Manejo de transacciones con AutoCloseable

**Models/**
- `Base.java`: Clase abstracta con campos id y eliminado
- `Pedido.java`: Entidad Pedido (número, fecha, cliente, total, estado, envío)
- `Envio.java`: Entidad Envío (tracking, empresa, tipo, costo, fechas, estado)

**Dao/**
- `GenericDAO<T>`: Interface genérica con operaciones CRUD
- `PedidoDAO`: Implementación con queries para incluir envío asociado
- `EnvioDAO`: Implementación para envíos

**Service/**
- `GenericService<T>`: Interface genérica para servicios
- `PedidoServiceImpl`: Validaciones de pedido y coordinación con envío
- `EnvioServiceImpl`: Validaciones de envío

**Main/**
- `Main.java`: Punto de entrada
- `AppMenu.java`: Orquestador del ciclo de menú
- `MenuHandler.java`: Implementación de operaciones CRUD con captura de entrada
- `MenuDisplay.java`: Lógica de visualización de menús
- `TestConexion.java`: Utilidad para verificar conexión a BD

## Modelo de Datos

```
┌────────────────────┐          ┌──────────────────┐
│     pedido         │          │   envio          │
├────────────────────┤          ├──────────────────┤
│ id (PK)            │          │ id (PK)          │
│ numero (UNIQUE)    │          │ tracking (UNIQUE)│
│ fecha              │          │ empresa          │
│ cliente_nombre     │          │ tipo             │
│ total              │──────┐   │ costo            │
│ estado             │      │   │ fecha_despacho   │
│ envio_id(FK,UQ)    │      │   │fecha_estimada    │
│ eliminado          │      │   │estado            │
└────────────────────┘      │   │eliminado         │
                            │   └──────────────────┘
                            └──▶ Relación 1..1
```

## Reglas

- Un pedido puede tener 0 o 1 envío asociado
- Número de pedido es único (constraint en base de datos y validación en aplicación)
- Tracking de envío es único (constraint en base de datos y validación en aplicación)
- Eliminación lógica: campo `eliminado = TRUE`
- Foreign key `envio_id` en pedido puede ser NULL (pedido sin envío)

## Patrones y Buenas Prácticas

### Seguridad
- **100% PreparedStatements**: Prevención de SQL injection
- **Validación multi-capa**: Service layer valida antes de persistir
- **Número de pedido único**: Constraint en BD + validación en `PedidoServiceImpl.validateNumeroUnico()`
- **Tracking único**: Constraint en BD + validación en `EnvioServiceImpl.validateTrackingUnico()`

### Gestión de Recursos
- **Try-with-resources**: Todas las conexiones, statements y resultsets
- **AutoCloseable**: TransactionManager cierra y hace rollback automático
- **Scanner cerrado**: En `AppMenu.run()` al finalizar

### Validaciones
- **Input trimming**: Todos los inputs usan `.trim()` inmediatamente
- **Campos obligatorios**: Validación de null y empty en service layer
- **IDs positivos**: Validación `id > 0` en todas las operaciones
- **Verificación de rowsAffected**: En UPDATE y DELETE

### Soft Delete
- DELETE ejecuta: `UPDATE tabla SET eliminado = TRUE WHERE id = ?`
- SELECT filtra: `WHERE eliminado = FALSE`
- No hay eliminación física de datos

## Reglas de Negocio Principales

1. **Número de pedido único**: No se permiten pedidos con número duplicado
2. **Tracking único**: No se permiten envíos con tracking duplicado
3. **Campos obligatorios**: Pedido requiere número, fecha, cliente, total y estado; Envío requiere empresa, tipo, estado y tracking
4. **Validación antes de persistir**: Service layer valida antes de llamar a DAO
5. **Eliminación segura de envío**: Usar eliminación desde pedido en lugar de eliminar envío directamente para evitar referencias huérfanas
6. **Preservación de valores**: En actualización, campos vacíos mantienen valor original
7. **Búsqueda flexible**: LIKE con % permite coincidencias parciales por número de pedido o cliente
8. **Transacciones**: Operaciones complejas soportan rollback automático

## Solución de Problemas

### Error: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
**Causa**: JAR de MySQL no está en classpath

**Solución**: Incluir `mysql-connector-j-8.4.0.jar` en el comando `java -cp`

### Error: "Communications link failure"
**Causa**: MySQL no está ejecutándose

**Solución**:
```bash
# Linux/macOS
sudo systemctl start mysql
# O
brew services start mysql

# Windows
net start MySQL80
```
### Error: "Access denied for user 'root'@'localhost'"
**Causa**: Credenciales incorrectas

**Solución**: Verificar usuario/contraseña en `DatabaseConnection.java` o usar `-Ddb.user` y `-Ddb.password`

### Error: "Unknown database 'tpi_pedido_envio'"
**Causa**: Base de datos no creada

**Solución**: Ejecutar script de creación de base de datos (ver sección Instalación)

### Error: "Table 'pedido' or 'envio' doesn't exist"
**Causa**: Tablas no creadas

**Solución**: Ejecutar script de creación de tablas (ver sección Instalación)

## Limitaciones Conocidas

1. **No hay tarea maven/gradle run**: Debe ejecutarse con `java -cp` manualmente o desde IDE
2. **Interfaz solo consola**: No hay GUI gráfica
3. **Un envío por pedido**: No soporta múltiples envíos asociados a un mismo pedido
4. **Sin paginación**: Listar todos puede ser lento con muchos registros
5. **Eliminación directa de envío peligrosa**: Puede dejar referencias huérfanas si está asociado a un pedido (usar eliminación desde pedido)
6. **Sin pool de conexiones**: Nueva conexión por operación (aceptable para app de consola)
7. **Sin transacciones en MenuHandler**: Actualizar pedido + envío puede fallar parcialmente

## Documentación Adicional

- **CLAUDE.md**: Documentación técnica detallada para desarrollo
  - Comandos de build y ejecución con Maven/Gradle
  - Arquitectura en capas (UI, Service, DAO, Models)
  - Patrones de código críticos aplicados en Pedidos y Envíos
  - Troubleshooting avanzado de JDBC y MySQL
  - Verificación de calidad (score académico)

- **HISTORIAS_DE_USUARIO.md**: Especificaciones funcionales completas
  - Historias de usuario detalladas para gestión de pedidos y envíos
  - Reglas de negocio numeradas (pedido único, tracking único, relación 1–1)
  - Criterios de aceptación en formato Gherkin
  - Diagramas de flujo de operaciones CRUD

## Tecnologías Utilizadas

- **Lenguaje**: Java 17+
- **Build Tool**: Maven 3.8+ (o Gradle 8.12 si se usa wrapper)
- **Base de Datos**: MySQL 8.x
- **JDBC Driver**: mysql-connector-j 8.4.0
- **Testing**: JUnit 5 (configurado, sin tests implementados)

## Estructura de Directorios

```
TPI-Prog2-fusion-final/
├── src/main/java/
│   ├── Config/          # Configuración de BD y transacciones
│   ├── Dao/             # Capa de acceso a datos
│   ├── Main/            # UI y punto de entrada
│   ├── Models/          # Entidades de dominio
│   └── Service/         # Lógica de negocio
├── build.gradle         # Configuración de Gradle
├── gradlew              # Gradle wrapper (Unix)
├── gradlew.bat          # Gradle wrapper (Windows)
├── README.md            # Este archivo
├── CLAUDE.md            # Documentación técnica
└── HISTORIAS_DE_USUARIO.md  # Especificaciones funcionales
```

## Convenciones de Código

- **Idioma**: Español (nombres de clases, métodos, variables)
- **Nomenclatura**:
  - Clases: PascalCase (Ej: `PedidoDAO`, `EnvioDAO`, `AppMenu`)
  - Métodos: camelCase (Ej: `buscarPorNumeroPedido`, `validarTrackingUnico`)
  - Constantes SQL: UPPER_SNAKE_CASE (Ej: `SELECT_PEDIDO_BY_ID_SQL`, `INSERT_ENVIO_SQL`)
- **Indentación**: 4 espacios
- **Recursos**: Siempre usar try-with-resources
- **SQL**: Constantes privadas `static final`
- **Excepciones**: Capturar y manejar con mensajes claros al usuario



