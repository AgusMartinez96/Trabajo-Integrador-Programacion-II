# Historias de Usuario - Sistema de Gestión de Pedidos y Envíos

Especificaciones funcionales completas del sistema CRUD de pedidos y envíos.

## Tabla de Contenidos

- [Épica 1: Gestión de Pedidos](#épica-1-gestión-de-pedidos)
- [Épica 2: Gestión de Envíos](#épica-2-gestión-de-envíos)
- [Épica 3: Operaciones de Búsqueda y Consulta](#épica-3-operaciones-de-búsqueda-y-consulta)
- [Épica 4: Listados y Reportes](#épica-4-listados-y-reportes)
- [Reglas de Negocio](#reglas-de-negocio)
- [Modelo de Datos](#modelo-de-datos)

---

## Épica 1: Gestión de Pedidos

### HU-001: Crear Pedido con Envío

**Como** usuario del sistema
**Quiero** crear un pedido con su envío asociado
**Para** registrar completamente una venta con su información de envío

#### Criterios de Aceptación

Escenario: Crear pedido con envío exitosamente
  Dado que el usuario selecciona "Crear pedido con envío"
  Cuando ingresa número "PED-001", fecha "2024-01-15", cliente "Juan Pérez", total "15000.50"
  Y selecciona estado "NUEVO"
  Y ingresa tracking "TRK-123456", costo "2500.00", empresa "ANDREANI", tipo "ESTANDAR", estado "EN_PREPARACION"
  Entonces el sistema crea el pedido y el envío asociado
  Y muestra "Pedido creado con éxito"

Escenario: Crear pedido con número duplicado
  Dado que existe un pedido con número "PED-001"
  Cuando el usuario intenta crear otro pedido con el mismo número
  Entonces el sistema muestra "Ya existe un pedido con el número: PED-001"
  Y no crea el registro

Escenario: Crear pedido con datos inválidos
  Dado que el usuario selecciona "Crear pedido con envío"
  Cuando ingresa total "-100" (negativo)
  Entonces el sistema muestra "El total no puede ser negativo"
  Y no crea el registro
  
# Reglas de Negocio Aplicables
RN-001: Número de pedido único en el sistema

RN-002: Total no puede ser negativo

RN-003: Tracking de envío único en el sistema

RN-004: Costo de envío no puede ser negativo

RN-005: Transacción atómica: si falla el envío, no se crea el pedido

# Implementación Técnica
Clase: MenuHandler.crearPedidoConEnvio() (líneas 25-67)

Servicio: PedidoService.insertar() con transacción

Validación: PedidoService.validatePedido() + validarNumeroPedidoUnico()

Flujo Transaccional: Crear envío → Validar unicidad → Crear pedido

### HU-002: Crear Pedido sin Envío
**Como** usuario del sistema
**Quiero** crear un pedido sin envío asociado
**Para** registrar ventas que no requieren envío físico

# Criterios de Aceptación
Escenario: Crear pedido sin envío
  Dado que el usuario selecciona "Crear pedido sin envío"
  Cuando ingresa número "PED-002", fecha "2024-01-16", cliente "María García", total "8000.00"
  Y selecciona estado "FACTURADO"
  Entonces el sistema crea el pedido sin envío asociado
  Y muestra "Pedido creado con éxito"

Escenario: Pedido sin envío con estado ENVIADO
  Dado que el usuario crea pedido sin envío
  Cuando selecciona estado "ENVIADO"
  Entonces el sistema permite la creación
  Y el pedido queda con estado ENVIADO pero sin envío asociado
  
# Reglas de Negocio Aplicables
RN-006: Pedido puede existir sin envío asociado

RN-007: Estado ENVIADO es válido incluso sin envío

RN-008: Relación pedido-envío es opcional (0..1)

# Implementación Técnica
Clase: MenuHandler.crearPedidoSinEnvio() (líneas 69-89)

Servicio: PedidoService.insertar() con envio = null

DAO: PedidoDao.crear() con envio_id NULL

### HU-003: Actualizar Pedido
**Como** usuario del sistema
**Quiero** actualizar los datos de un pedido existente
**Para** corregir información o actualizar estados

# Criterios de Aceptación
Escenario: Actualizar datos básicos del pedido
  Dado que existe pedido ID 1 con cliente "Juan Pérez"
  Cuando el usuario actualiza el pedido ID 1
  Y cambia cliente a "Juan Pérez González"
  Y mantiene otros campos sin cambios (presiona Enter)
  Entonces el sistema actualiza solo el campo modificado
  Y muestra "Pedido actualizado correctamente"

Escenario: Actualizar con número duplicado
  Dado que existen pedidos "PED-001" y "PED-002"
  Cuando el usuario intenta cambiar número de "PED-002" a "PED-001"
  Entonces el sistema muestra "Ya existe otro pedido con el número: PED-001"
  Y no realiza la actualización

Escenario: Agregar envío a pedido existente
  Dado que pedido ID 1 no tiene envío
  Cuando el usuario actualiza y agrega envío
  Entonces el sistema crea el envío y lo asocia al pedido
  Y actualiza el estado del pedido si es necesario
  
# Reglas de Negocio Aplicables
RN-009: Validación de número único excluyendo el pedido actual

RN-010: Campos vacíos en actualización mantienen valor anterior

RN-011: Se puede agregar envío a pedido existente

RN-012: Se puede quitar envío de pedido existente

# Implementación Técnica
Clase: MenuHandler.actualizarPedido() (líneas 166-218)

Servicio: PedidoService.actualizarDatosBasicos()

Métodos Auxiliares: actualizarDatosBasicosPedido(), gestionarEnvioPedido()

### HU-004: Eliminar Pedido
**Como** usuario del sistema
**Quiero** eliminar un pedido del sistema
**Para** mantener solo registros activos y preservar integridad histórica

# Criterios de Aceptación

Escenario: Eliminar pedido sin envío
  Dado que existe pedido ID 1 sin envío asociado
  Cuando el usuario elimina pedido ID 1
  Entonces el sistema marca el pedido como eliminado
  Y muestra "Pedido eliminado (baja lógica)"

Escenario: Eliminar pedido con envío
  Dado que existe pedido ID 2 con envío asociado
  Cuando el usuario elimina pedido ID 2
  Entonces el sistema marca el envío como eliminado
  Y luego marca el pedido como eliminado
  Y muestra ambos eliminados correctamente

Escenario: Eliminar pedido inexistente
  Dado que no existe pedido ID 999
  Cuando el usuario intenta eliminar pedido ID 999
  Entonces el sistema muestra "No se pudo encontrar pedido con ID: 999"

# Reglas de Negocio Aplicables
RN-013: Eliminación lógica (soft delete)

RN-014: Eliminación en cascada controlada para envíos

RN-015: Verificación de existencia antes de eliminar

RN-016: Transacción atómica para pedido con envío

# Implementación Técnica
Clase: MenuHandler.eliminarPedido() (líneas 220-225)

Servicio: PedidoService.eliminar() con transacción

Flujo: Obtener pedido → Eliminar envío (si existe) → Eliminar pedido

## Épica 2: Gestión de Envíos
### HU-005: Actualizar Envío
**Como** usuario del sistema
**Quiero** actualizar la información de un envío existente
**Para** reflejar cambios en el estado de entrega o correcciones

# Criterios de Aceptación

Escenario: Actualizar estado de envío
  Dado que existe envío con tracking "TRK-123456" en estado "EN_PREPARACION"
  Cuando el usuario actualiza el estado a "EN_TRANSITO"
  Entonces el sistema modifica el estado del envío
  Y muestra "Envío actualizado correctamente"

Escenario: Actualizar tracking único
  Dado que existe envío con tracking "TRK-123456"
  Cuando el usuario intenta cambiar tracking a "TRK-999999" que ya existe
  Entonces el sistema muestra "Error: Tracking ya existe"
  Y no realiza la actualización

Escenario: Actualización parcial
  Dado que existe envío con costo "2500.00"
  Cuando el usuario actualiza solo la fecha estimada
  Y deja otros campos vacíos
  Entonces el sistema modifica solo la fecha estimada
  Y mantiene el costo original
# Reglas de Negocio Aplicables
RN-017: Tracking debe permanecer único después de actualización

RN-018: Campos vacíos mantienen valores anteriores

RN-019: Validación de fechas (fecha estimada no anterior a despacho)

RN-020: Estados válidos: EN_PREPARACION, EN_TRANSITO, ENTREGADO

# Implementación Técnica
Clase: MenuHandler.actualizarEnvio() (líneas 227-259)

Servicio: EnvioService.actualizarDatosEnvio()

DAO: EnvioDao.actualizar() con actualización completa

### HU-006: Eliminar Envío
**Como** usuario del sistema
**Quiero** eliminar un envío del sistema
**Para** remover envíos incorrectos o duplicados

# Criterios de Aceptación

Escenario: Eliminar envío independiente
  Dado que existe envío con tracking "TRK-999999" no asociado a pedido
  Cuando el usuario elimina el envío por tracking
  Entonces el sistema marca el envío como eliminado
  Y muestra "Envío eliminado correctamente (baja lógica)"

Escenario: Eliminar envío asociado a pedido
  Dado que existe envío asociado a pedido ID 1
  Cuando el usuario elimina el envío
  Entonces el sistema marca el envío como eliminado
  Y el pedido queda con referencia NULL al envío
  Y muestra "Envío eliminado y referencia actualizada"

Escenario: Eliminar envío inexistente
  Dado que no existe envío con tracking "TRK-INEXISTENTE"
  Cuando el usuario intenta eliminarlo
  Entonces el sistema muestra "No se encontró ningún envío con tracking: TRK-INEXISTENTE"
  
# Reglas de Negocio Aplicables
RN-021: Eliminación lógica de envíos

RN-022: Si el envío está asociado a pedido, se actualiza la referencia

RN-023: Búsqueda por tracking para eliminación

RN-024: Confirmación antes de eliminar

# Implementación Técnica
Clase: MenuHandler.eliminarEnvio() (líneas 261-295)

Servicio: EnvioService.eliminar()

Flujo: Buscar por tracking → Confirmar → Eliminar (soft delete)

## Épica 3: Operaciones de Búsqueda y Consulta
### HU-007: Buscar Pedido por Número
**Como** usuario del sistema
**Quiero** buscar un pedido por su número exacto
**Para** encontrar rápidamente un pedido específico

# Criterios de Aceptación

Escenario: Buscar pedido existente
  Dado que existe pedido con número "PED-001"
  Cuando el usuario busca por "PED-001"
  Entonces el sistema encuentra el pedido
  Y muestra toda su información incluyendo envío si existe

Escenario: Buscar pedido no existente
  Dado que no existe pedido con número "PED-999"
  Cuando el usuario busca por "PED-999"
  Entonces el sistema muestra "No se encontró ningún pedido con el número: PED-999"

Escenario: Búsqueda case insensitive
  Dado que existe pedido con número "PED-001"
  Cuando el usuario busca por "ped-001"
  Entonces el sistema encuentra el pedido
  Y muestra la información correctamente
  
# Reglas de Negocio Aplicables
RN-025: Búsqueda exacta por número

RN-026: Solo muestra pedidos no eliminados

RN-027: Incluye información de envío asociado si existe

# Implementación Técnica
Clase: MenuHandler.buscarPedidoPorNumero() (líneas 91-110)

DAO: PedidoDao.buscarPorNumero()

Servicio: PedidoService.buscarPorNumero()

### HU-008: Buscar Pedidos por Cliente
**Como** usuario del sistema
**Quiero** buscar pedidos por nombre de cliente
**Para** consultar todos los pedidos de un cliente específico

# Criterios de Aceptación

Escenario: Búsqueda parcial por cliente
  Dado que existen pedidos de "Juan Pérez" y "Juan García"
  Cuando el usuario busca por "Juan"
  Entonces el sistema muestra ambos pedidos
  Y indica el total de pedidos encontrados

Escenario: Búsqueda exacta por cliente
  Dado que existen pedidos de "María González"
  Cuando el usuario busca por "María González"
  Entonces el sistema muestra solo los pedidos de ese cliente
  Y los ordena por fecha descendente

Escenario: Cliente sin pedidos
  Dado que no existen pedidos de "Cliente Inexistente"
  Cuando el usuario busca por "Cliente Inexistente"
  Entonces el sistema muestra "No se encontraron pedidos para el cliente: Cliente Inexistente"

# Reglas de Negocio Aplicables
RN-028: Búsqueda parcial con LIKE %cliente%

RN-029: Ordenamiento por fecha descendente

RN-030: Solo muestra pedidos activos

RN-031: Indicación de cantidad de resultados

# Implementación Técnica
Clase: MenuHandler.buscarPedidosPorCliente() (líneas 112-133)

DAO: PedidoDao.buscarPorCliente()

Query: "SELECT * FROM pedido WHERE cliente_nombre LIKE ? AND eliminado = false ORDER BY fecha DESC"

### HU-009: Buscar Pedidos por Estado
**Como** usuario del sistema
**Quiero** buscar pedidos por su estado
**Para** filtrar pedidos según su situación actual

# Criterios de Aceptación

Escenario: Buscar pedidos por estado NUEVO
  Dado que existen 3 pedidos con estado NUEVO
  Cuando el usuario busca por estado "NUEVO"
  Entonces el sistema muestra los 3 pedidos
  Y los ordena por fecha descendente

Escenario: Buscar con estado inválido
  Dado que el usuario busca por estado "INVALIDO"
  Entonces el sistema muestra "Estado inválido. Use: NUEVO, FACTURADO o ENVIADO"
  Y no realiza la búsqueda

Escenario: Estado sin pedidos
  Dado que no existen pedidos con estado "ENVIADO"
  Cuando el usuario busca por estado "ENVIADO"
  Entonces el sistema muestra "No se encontraron pedidos con estado: ENVIADO"
  
# Reglas de Negocio Aplicables
RN-032: Estados válidos: NUEVO, FACTURADO, ENVIADO

RN-033: Validación de estado antes de búsqueda

RN-034: Ordenamiento por fecha descendente

# Implementación Técnica
Clase: MenuHandler.buscarPedidosPorEstado() (líneas 135-153)

DAO: PedidoDao.buscarPorEstado()

Validación: Pedido.EstadoPedido.valueOf(estado.toUpperCase())

### HU-010: Buscar Envío por Tracking
**Como** usuario del sistema
**Quiero** buscar un envío por su número de tracking
**Para** consultar el estado y información de un envío específico

# Criterios de Aceptación

Escenario: Buscar envío existente
  Dado que existe envío con tracking "TRK-123456"
  Cuando el usuario busca por "TRK-123456"
  Entonces el sistema encuentra el envío
  Y muestra toda su información completa

Escenario: Buscar envío no existente
  Dado que no existe envío con tracking "TRK-999999"
  Cuando el usuario busca por "TRK-999999"
  Entonces el sistema muestra "No se encontró ningún envío con tracking: TRK-999999"

Escenario: Tracking vacío
  Dado que el usuario busca con tracking vacío
  Cuando el sistema detecta el tracking vacío
  Entonces muestra "El número de tracking no puede estar vacío"
  
# Reglas de Negocio Aplicables
RN-035: Búsqueda exacta por tracking

RN-036: Tracking no puede estar vacío

RN-037: Solo muestra envíos no eliminados

# Implementación Técnica
Clase: MenuHandler.buscarEnvioPorTracking() (líneas 155-174)

DAO: EnvioDao.buscarPorTracking()

Servicio: EnvioService.buscarPorTracking()

### HU-011: Buscar Envíos por Empresa
**Como** usuario del sistema
**Quiero** buscar envíos por empresa de transporte
**Para** consultar todos los envíos gestionados por una empresa específica

# Criterios de Aceptación

Escenario: Buscar envíos por empresa ANDREANI
  Dado que existen 5 envíos con empresa ANDREANI
  Cuando el usuario busca por empresa "ANDREANI"
  Entonces el sistema muestra los 5 envíos
  Y los ordena por fecha de despacho descendente

Escenario: Buscar con empresa inválida
  Dado que el usuario busca por empresa "INVALIDA"
  Entonces el sistema muestra "Empresa inválida. Use: ANDREANI, OCA o CORREO_ARG"
  Y no realiza la búsqueda

Escenario: Empresa sin envíos
  Dado que no existen envíos con empresa "CORREO_ARG"
  Cuando el usuario busca por "CORREO_ARG"
  Entonces el sistema muestra "No se encontraron envíos para la empresa: CORREO_ARG"
  
# Reglas de Negocio Aplicables
RN-038: Empresas válidas: ANDREANI, OCA, CORREO_ARG

RN-039: Validación de empresa antes de búsqueda

RN-040: Ordenamiento por fecha de despacho descendente

# Implementación Técnica
Clase: MenuHandler.buscarEnviosPorEmpresa() (líneas 176-195)

DAO: EnvioDao.buscarPorEmpresa()

Validación: Envio.Empresa.valueOf(empresa.toUpperCase())

### HU-012: Buscar Envíos por Estado
**Como** usuario del sistema
**Quiero** buscar envíos por su estado actual
**Para** filtrar envíos según su situación de entrega

# Criterios de Aceptación

Escenario: Buscar envíos en tránsito
  Dado que existen 3 envíos con estado EN_TRANSITO
  Cuando el usuario busca por estado "EN_TRANSITO"
  Entonces el sistema muestra los 3 envíos
  Y los ordena por fecha de despacho descendente

Escenario: Buscar con estado inválido
  Dado que el usuario busca por estado "INVALIDO"
  Entonces el sistema muestra "Estado inválido. Use: EN_PREPARACION, EN_TRANSITO o ENTREGADO"
  Y no realiza la búsqueda

Escenario: Estado sin envíos
  Dado que no existen envíos con estado "ENTREGADO"
  Cuando el usuario busca por "ENTREGADO"
  Entonces el sistema muestra "No se encontraron envíos con estado: ENTREGADO"
  
# Reglas de Negocio Aplicables
RN-041: Estados válidos: EN_PREPARACION, EN_TRANSITO, ENTREGADO

RN-042: Validación de estado antes de búsqueda

RN-043: Ordenamiento por fecha de despacho descendente

# Implementación Técnica
Clase: MenuHandler.buscarEnviosPorEstado() (líneas 197-216)

DAO: EnvioDao.buscarPorEstado()

Validación: Envio.EstadoEnvio.valueOf(estado.toUpperCase())

## Épica 4: Listados y Reportes
### HU-013: Listar Todos los Pedidos
**Como** usuario del sistema
**Quiero** ver un listado completo de todos los pedidos
**Para** tener una visión general del negocio

# Criterios de Aceptación

Escenario: Listar pedidos existentes
  Dado que existen 10 pedidos en el sistema
  Cuando el usuario selecciona "Listar todos los pedidos"
  Entonces el sistema muestra los 10 pedidos
  Y indica "TOTAL DE PEDIDOS: 10"
  Y para cada pedido muestra información completa incluyendo envío

Escenario: No hay pedidos
  Dado que no existen pedidos en el sistema
  Cuando el usuario lista todos los pedidos
  Entonces el sistema muestra "No hay pedidos registrados en el sistema."

Escenario: Pedidos con y sin envío
  Dado que existen pedidos con envío y sin envío
  Cuando el usuario lista todos los pedidos
  Entonces el sistema muestra correctamente ambos tipos
  Y indica claramente cuáles tienen envío asociado
  
# Reglas de Negocio Aplicables
RN-044: Solo muestra pedidos no eliminados

RN-045: Incluye información de envío mediante LEFT JOIN

RN-046: Muestra contador total de pedidos

RN-047: Formato consistente para todos los pedidos

# Implementación Técnica
Clase: MenuHandler.listarPedidos() (líneas 218-230)

DAO: PedidoDao.leerTodos() con JOIN a envíos

Query: "SELECT p.*, e.tracking, e.empresa, e.tipo, e.costo, e.fecha_despacho, e.fecha_estimada, e.estado as envio_estado FROM pedido p LEFT JOIN envio e ON p.envio_id = e.id WHERE p.eliminado = false AND (e.eliminado = false OR e.id IS NULL)"

### HU-014: Listar Todos los Envíos
**Como** usuario del sistema
**Quiero** ver un listado completo de todos los envíos
**Para** gestionar y monitorear las entregas

# Criterios de Aceptación

Escenario: Listar envíos existentes
  Dado que existen 15 envíos en el sistema
  Cuando el usuario selecciona "Listar todos los envíos"
  Entonces el sistema muestra los 15 envíos
  Y indica "TOTAL DE ENVÍOS: 15"
  Y para cada envío muestra información completa

Escenario: No hay envíos
  Dado que no existen envíos en el sistema
  Cuando el usuario lista todos los envíos
  Entonces el sistema muestra "No hay envíos registrados en el sistema."

Escenario: Envíos con diferentes estados
  Dado que existen envíos en diferentes estados
  Cuando el usuario lista todos los envíos
  Entonces el sistema muestra claramente el estado de cada envío
  Y permite identificar rápidamente los envíos pendientes
  
# Reglas de Negocio Aplicables
RN-048: Solo muestra envíos no eliminados

RN-049: Muestra contador total de envíos

RN-050: Información completa de cada envío

RN-051: Ordenamiento implícito por fecha de creación

# Implementación Técnica
Clase: MenuHandler.listarEnvios() (líneas 232-244)

DAO: EnvioDao.leerTodos()

Query: "SELECT * FROM envio WHERE eliminado = false"

#### Reglas de Negocio
## Validación de Datos (RN-001 a RN-015)
Código	Regla						Implementación
RN-001	Número de pedido único		PedidoService.validarNumeroPedidoUnico()
RN-002	Total no negativo			PedidoService.validatePedido()
RN-003	Tracking único				EnvioDao.buscarPorTracking() + DB UNIQUE constraint
RN-004	Costo no negativo			EnvioService.validarEnvio()
RN-005	Transacción atómica			PedidoService.insertar() con conn.setAutoCommit(false)
RN-006	Pedido sin envío válido		FK nullable en base de datos
RN-007	Estado ENVIADO sin envío	No hay restricción explícita
RN-008	Relación 1..1				Diseño de base de datos

## Operaciones de Base de Datos (RN-016 a RN-030)
Código	Regla							Implementación
RN-013	Soft delete pedidos				UPDATE pedido SET eliminado = true
RN-014	Soft delete envíos				UPDATE envio SET eliminado = true
RN-015	Verificación existencia			Verificación rowsAffected en DAOs
RN-016	Transacción pedido-envío		PedidoService.eliminar() con transacción
RN-021	Eliminación lógica envíos		EnvioService.eliminar()
RN-022	Actualizar referencia			Pedido.setEnvio(null) antes de eliminar
RN-026	Solo listar no eliminados		WHERE eliminado = false en queries
RN-030	Indicación cantidad resultados	List.size() en MenuHandler

## Búsquedas y Filtros (RN-031 a RN-051)
Código	Regla							Implementación
RN-028	Búsqueda parcial cliente		LIKE %cliente% en SQL
RN-029	Ordenamiento fecha DESC			ORDER BY fecha DESC
RN-032	Validación estado pedido		Pedido.EstadoPedido.valueOf()
RN-038	Validación empresa				Envio.Empresa.valueOf()
RN-041	Validación estado envío			Envio.EstadoEnvio.valueOf()
RN-045	JOIN para envíos				LEFT JOIN en PedidoDao.leerTodos()
RN-046	Contador resultados				System.out.println("TOTAL: " + list.size())
RN-051	Ordenamiento implícito			Sin ORDER BY específico en algunos listados

#### Modelo de Datos
## Diagrama Entidad-Relación
┌─────────────────────────────────┐       ┌──────────────────────────────────┐
│            pedido               │       │              envio               │
├─────────────────────────────────┤       ├──────────────────────────────────┤
│ id: BIGINT PK AUTO_INCREMENT    │       │ id: BIGINT PK AUTO_INCREMENT     │
│ numero: VARCHAR(20) UNIQUE NOT NULL │   │ tracking: VARCHAR(40) UNIQUE NOT NULL │
│ fecha: DATE NOT NULL            │       │ empresa: ENUM(...) NOT NULL      │
│ cliente_nombre: VARCHAR(100) NOT NULL │ │ tipo: ENUM(ESTANDAR,EXPRES) NOT NULL │
│ total: DECIMAL(10,2) NOT NULL   │       │ costo: DECIMAL(10,2) NOT NULL    │
│ estado: ENUM(...) NOT NULL      │       │ fecha_despacho: DATE NOT NULL    │
│ envio_id: BIGINT FK NULL        │───────│ fecha_estimada: DATE NOT NULL    │
│ eliminado: BOOLEAN DEFAULT FALSE│       │ estado: ENUM(...) NOT NULL       │
└─────────────────────────────────┘       │ eliminado: BOOLEAN DEFAULT FALSE │
                                          └──────────────────────────────────┘
## Enums y Dominios
# EstadoPedido
NUEVO: Pedido recién creado

FACTURADO: Pedido facturado pero no enviado

ENVIADO: Pedido enviado al cliente

# Empresa
ANDREANI: Empresa de transporte Andreani

OCA: Empresa de transporte OCA

CORREO_ARG: Correo Argentino

# TipoEnvio
ESTANDAR: Envío estándar (económico)

EXPRES: Envío express (urgente)

# EstadoEnvio
EN_PREPARACION: Envío en preparación

EN_TRANSITO: Envío en camino

ENTREGADO: Envío entregado

#### Flujos Técnicos Críticos
## Flujo 1: Crear Pedido con Envío (Transaccional)
Usuario (MenuHandler.crearPedidoConEnvio())
    ↓ captura datos con validación
PedidoService.insertar(pedido)
    ↓ validatePedido()
    ↓ validarNumeroPedidoUnico()
    ↓ Connection conn = DatabaseConnection.getConnection()
    ↓ conn.setAutoCommit(false)
    ↓ if (pedido.getEnvio() != null)
EnvioService.insertar(envio, conn)
    ↓ validarEnvio(envio)
    ↓ EnvioDao.crear(envio, conn)
        ↓ INSERT envio + obtener generatedId
    ↓ validarEnvioUnico(envio.getId(), conn)
PedidoDao.crear(pedido, conn)
    ↓ INSERT pedido con envio_id
    ↓ conn.commit()
Usuario recibe: "Pedido creado con éxito."

## Flujo 2: Búsqueda Avanzada con JOIN
Usuario (MenuHandler.listarPedidos())
    ↓ PedidoService.getAll()
        ↓ PedidoDao.leerTodos()
            ↓ Connection conn = DatabaseConnection.getConnection()
            ↓ PreparedStatement con JOIN SQL:
              SELECT p.*, e.tracking, e.empresa, e.tipo, e.costo, 
                     e.fecha_despacho, e.fecha_estimada, e.estado as envio_estado
              FROM pedido p LEFT JOIN envio e ON p.envio_id = e.id
              WHERE p.eliminado = false AND (e.eliminado = false OR e.id IS NULL)
            ↓ ResultSet processing
            ↓ mapearPedidoCompleto(rs) con envío completo
    ↓ Mostrar lista con formato

## Flujo 3: Eliminación Segura con Transacción
Usuario (MenuHandler.eliminarPedido())
    ↓ PedidoService.eliminar(id)
        ↓ pedido = pedidoDao.leerPorId(id)
        ↓ Connection conn = DatabaseConnection.getConnection()
        ↓ conn.setAutoCommit(false)
        ↓ if (pedido.getEnvio() != null)
            ↓ envioService.eliminar(pedido.getEnvio().getId(), conn)
                ↓ UPDATE envio SET eliminado = true WHERE id = ?
        ↓ pedidoDao.eliminar(id, conn)
            ↓ UPDATE pedido SET eliminado = true WHERE id = ?
        ↓ conn.commit()
    ↓ "Pedido eliminado (baja lógica)."
	
#### Resumen de Operaciones del Menú
Menú		Opción	Operación						HU
Principal	1		Gestión de Pedidos y Envíos		-
Gestión		1		Crear pedido con envío			HU-001
Gestión		2		Crear pedido sin envío			HU-002
Gestión		3		Actualizar pedido				HU-003
Gestión		4		Eliminar pedido					HU-004
Gestión		5		Actualizar envío				HU-005
Gestión		6		Eliminar envío					HU-006
Principal	2		Búsquedas y Consultas			-
Búsquedas	1		Buscar pedido por número		HU-007
Búsquedas	2		Buscar pedidos por cliente		HU-008
Búsquedas	3		Buscar pedidos por estado		HU-009
Búsquedas	4		Buscar envío por tracking		HU-010
Búsquedas	5		Buscar envíos por empresa		HU-011
Búsquedas	6		Buscar envíos por estado		HU-012
Principal	3		Listados Completos				-
Listados	1		Listar todos los pedidos		HU-013
Listados	2		Listar todos los envíos			HU-014

---
## Documentación Relacionada

- **README.md**: Guía de instalación, configuración y uso
- **CLAUDE.md**: Documentación técnica para desarrollo, arquitectura detallada, patrones de código

---
**Versión**: 1.0
**Total Historias de Usuario**: 14
**Total Reglas de Negocio**: 51
**Arquitectura**: 4 capas (Main → Service → DAO → Entities)
**Patrones**: DAO, Service Layer, Generic Programming, Dependency Injection
---
**Versión**: 1.0
**Java**: 17+
**MySQL**: 8.x
**Proyecto Educativo** - Trabajo Práctico Integrador de Programación 2