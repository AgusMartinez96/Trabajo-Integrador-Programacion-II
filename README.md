# Sistema de Gestión de Pedidos y Envíos

# Enlace del Video: https://youtu.be/7QRvEbFJupk

## Descripción del Proyecto

Sistema completo de gestión de pedidos y envíos desarrollado en Java que permite realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre estas entidades, implementando una arquitectura robusta en capas con persistencia en base de datos MySQL.

### Características Principales

- **Gestión Completa de Pedidos**: Crear, listar, actualizar y eliminar pedidos con validaciones
- **Gestión de Envíos**: Administrar envíos asociados a pedidos con diferentes empresas y estados
- **Relación 1 a 1**: Un pedido puede tener un envío asociado (opcional)
- **Búsquedas Avanzadas**: Filtrar por número, cliente, estado, tracking, empresa
- **Soft Delete**: Eliminación lógica que preserva la integridad de datos
- **Validaciones Multi-nivel**: Validaciones en capa de servicio y base de datos
- **Transacciones**: Operaciones atómicas con soporte para rollback
- **Interfaz de Consola**: Menú interactivo con todas las funcionalidades

## Arquitectura del Sistema
┌─────────────────────────────────────┐
│ Main Layer │
│ (Interfaz de usuario) │
│ AppMenu, MenuHandler, MenuDisplay │
└───────────┬─────────────────────────┘
│
┌───────────▼─────────────────────────┐
│ Service Layer │
│ (Lógica de negocio) │
│ PedidoService, EnvioService │
└───────────┬─────────────────────────┘
│
┌───────────▼─────────────────────────┐
│ DAO Layer │
│ (Acceso a datos) │
│ PedidoDao, EnvioDao │
└───────────┬─────────────────────────┘
│
┌───────────▼─────────────────────────┐
│ Entities Layer │
│ (Modelos de dominio) │
│ Pedido, Envio, GenericEntity │
└─────────────────────────────────────┘

text

## Requisitos del Sistema

| Componente | Versión Requerida |
|------------|-------------------|
| Java JDK | 17 o superior |
| MySQL | 8.0 o superior |
| Gradle | 8.12 (incluido en wrapper) |

## Instalación y Configuración

### 1. Configurar Base de Datos

Ejecutar el siguiente script SQL en MySQL:

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
    
    -- Definición de la relación 1-a-1
    CONSTRAINT fk_pedido_envio
        FOREIGN KEY (envio_id) 
        REFERENCES envio(id)
        ON DELETE CASCADE,
        
    UNIQUE KEY uk_envio_id (envio_id)
);
````

### 2. Compilar el Proyecto
´´´
Linux/macOS
./gradlew clean build

Windows
gradlew.bat clean build
´´´

### 3. Configurar Conexión a BD
Modificar la clase config.DatabaseConnection con tus credenciales:

// Configuración por defecto:
// URL: jdbc:mysql://localhost:3306/gestion_pedidos_envios
// Usuario: root
// Contraseña: (vacía)
# Ejecución
Desde IDE:
Ejecutar la clase main.Main

Desde línea de comandos:
Compilar primero
./gradlew build

Ejecutar
java -cp "build/classes/java/main:lib/mysql-connector-java-8.0.33.jar" main.Main

# Verificar Conexión:

java -cp "build/classes/java/main:lib/mysql-connector-java-8.0.33.jar" main.TestConexion


## Uso del Sistema

### Menú Principal
```
=== SISTEMA DE GESTIÓN DE PEDIDOS Y ENVÍOS ===

--- MENÚ PRINCIPAL ---
1. Gestión de Pedidos y Envios
2. Búsquedas y Consultas
3. Listados Completos
0. Salir
```
# Gestión de Pedidos y Envíos
```
--- GESTIÓN DE PEDIDOS Y ENVIOS ---
1. Crear nuevo pedido con envío
2. Crear nuevo pedido sin envío
3. Actualizar pedido
4. Eliminar pedido (baja lógica)
5. Actualizar envío
6. Eliminar envío (baja lógica)
7. Volver al menú principal
```
# Búsquedas y Consultas
```
--- BÚSQUEDAS Y CONSULTAS ---
1. Buscar pedido por número
2. Buscar pedidos por cliente
3. Buscar pedidos por estado
4. Buscar envío por tracking
5. Buscar envíos por empresa
6. Buscar envíos por estado
7. Volver al menú principal
```
# Listados Completos
```
--- LISTADOS COMPLETOS ---
1. Listar todos los pedidos
2. Listar todos los envíos
3. Volver al menú principal
```

## Ejemplos de Uso
### Crear Pedido con Envío

```
--- CREAR PEDIDO CON ENVÍO ---
Número de pedido: PED-001
Fecha (YYYY-MM-DD): 2024-01-15
Cliente: Juan Pérez
Total: 15000.50
Estado: NUEVO
Tracking: TRK-123456
Costo: 2500.00
Empresa: ANDREANI
Tipo: ESTANDAR
Estado: EN_PREPARACION
```

### Buscar Pedido por Número
```
--- BUSCAR PEDIDO POR NÚMERO ---
Ingrese el número de pedido: PED-001

PEDIDO ENCONTRADO:
Pedido{id=1, eliminado=false, numero='PED-001', fecha=2024-01-15, clienteNombre='Juan Pérez', total=15000.5, estado=NUEVO, envio=TRK-123456}
```
## Modelo de Datos
### Entidades Principales
# Pedido
id: Identificador único

numero: Número único del pedido (máx. 20 caracteres)

fecha: Fecha del pedido

clienteNombre: Nombre del cliente

total: Monto total del pedido

estado: NUEVO, FACTURADO, ENVIADO

envio: Relación opcional con envío

# Envio
id: Identificador único

tracking: Número de tracking único (máx. 40 caracteres)

empresa: ANDREANI, OCA, CORREO_ARG

tipo: ESTANDAR, EXPRES

costo: Costo del envío

fechaDespacho: Fecha de despacho

fechaEstimada: Fecha estimada de entrega

estado: EN_PREPARACION, EN_TRANSITO, ENTREGADO

## Reglas de Negocio
### Validaciones de Pedido
Número de pedido único en el sistema

Total no puede ser negativo

Estado debe ser válido (NUEVO, FACTURADO, ENVIADO)

Cliente nombre no puede estar vacío

### Validaciones de Envío
Tracking único en el sistema

Costo no puede ser negativo

Fechas deben ser válidas

Empresa y tipo deben ser valores válidos

### Relación Pedido-Envío
Un pedido puede tener 0 o 1 envío

Un envío pertenece a exactamente un pedido

Eliminación en cascada controlada

### Estructura del Proyecto
text
src/
├── main/
│   ├── java/
│   │   ├── config/
│   │   │   └── DatabaseConnection.java
│   │   ├── dao/
│   │   │   ├── GenericDao.java
│   │   │   ├── EnvioDao.java
│   │   │   └── PedidoDao.java
│   │   ├── entities/
│   │   │   ├── GenericEntity.java
│   │   │   ├── Envio.java
│   │   │   └── Pedido.java
│   │   ├── main/
│   │   │   ├── AppMenu.java
│   │   │   ├── Main.java
│   │   │   ├── MenuDisplay.java
│   │   │   ├── MenuHandler.java
│   │   │   └── TestConexion.java
│   │   └── service/
│   │       ├── GenericService.java
│   │       ├── EnvioService.java
│   │       └── PedidoService.java
│   └── resources/
└── build.gradle

## Tecnologías Utilizadas
Java 17: Lenguaje de programación

MySQL 8.x: Base de datos relacional

JDBC: Conector para base de datos

Gradle 8.12: Sistema de build

Patrón DAO: Acceso a datos

Arquitectura en Capas: Separación de responsabilidades

## Solución de Problemas Comunes
# Error de Conexión a BD
Verificar que MySQL esté ejecutándose

Confirmar credenciales en DatabaseConnection

Validar que la base de datos exista

# Error de Dependencias
Ejecutar ./gradlew clean build

Verificar que mysql-connector esté en classpath

# Error de Construcción
Verificar versión de Java (mínimo 17)

Confirmar estructura de proyectos
---
## Documentación Relacionada
**CLAUDE.md**: Documentación técnica detallada
**HISTORIAS_DE_USUARIO.md** - Especificaciones funcionales
---
**Versión**: 1.0
**Java**: 17+
**MySQL**: 8.x
**Proyecto Educativo** - Trabajo Práctico Integrador de Programación 2
