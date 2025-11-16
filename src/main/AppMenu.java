package main;

import dao.EnvioDao;
import dao.PedidoDao;
import entities.Pedido;
import entities.Envio;
import service.PedidoService;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import service.EnvioService;

public class AppMenu {

    //private final PedidoService pedidoService = new PedidoService();
    private final Scanner scanner = new Scanner(System.in);

    public void iniciar() {
        System.out.println("=== SISTEMA DE GESTIÓN DE PEDIDOS Y ENVÍOS ===");
    
        while (true) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Gestión de Pedidos");
            System.out.println("2. Búsquedas y Consultas");
            System.out.println("3. Listados Completos");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1 -> menuGestionPedidos();
                    case 2 -> menuBusquedas();
                    case 3 -> menuListados();
                    case 0 -> {
                        System.out.println("Saliendo del sistema...");
                        return;
                    }
                    default -> System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido.");
            } catch (Exception e) {
                System.out.println("Error inesperado: " + e.getMessage());
            }
        }
    }

    private void menuGestionPedidos() {
        while (true) {
            System.out.println("\n--- GESTIÓN DE PEDIDOS ---");
            System.out.println("1. Crear nuevo pedido con envío");
            System.out.println("2. Crear nuevo pedido sin envío");
            System.out.println("3. Actualizar pedido");
            System.out.println("4. Eliminar pedido (baja lógica)");
            System.out.println("5. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1 -> crearPedidoConEnvio();
                    case 2 -> crearPedidoSinEnvio();
                    case 3 -> actualizarPedido();
                    case 4 -> eliminarPedido();
                    case 5 -> { return; }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void menuBusquedas() {
        while (true) {
            System.out.println("\n--- BÚSQUEDAS Y CONSULTAS ---");
            System.out.println("1. Buscar pedido por número");
            System.out.println("2. Buscar pedidos por cliente");
            System.out.println("3. Buscar pedidos por estado");
            System.out.println("4. Buscar envío por tracking");
            System.out.println("5. Buscar envíos por empresa");
            System.out.println("6. Buscar envíos por estado");
            System.out.println("7. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1 -> buscarPedidoPorNumero();
                    case 2 -> buscarPedidosPorCliente();
                    case 3 -> buscarPedidosPorEstado();
                    case 4 -> buscarEnvioPorTracking();
                    case 5 -> buscarEnviosPorEmpresa();
                    case 6 -> buscarEnviosPorEstado();
                    case 7 -> { return; }
                    default -> System.out.println("❌ Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void menuListados() {
        while (true) {
            System.out.println("\n--- LISTADOS COMPLETOS ---");
            System.out.println("1. Listar todos los pedidos");
            System.out.println("2. Listar todos los envíos");
            System.out.println("3. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1 -> listarPedidos();
                    case 2 -> listarEnvios();
                    case 3 -> { return; }
                    default -> System.out.println("❌ Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void crearPedidoConEnvio() throws Exception {
        
        PedidoService pedidoService = createPedidoService();
        System.out.println("\n--- CREAR PEDIDO CON ENVÍO ---");
        Pedido pedido = new Pedido();
        Envio envio = new Envio();

        System.out.print("Número de pedido: ");
        pedido.setNumero(scanner.nextLine());

        System.out.print("Fecha (YYYY-MM-DD): ");
        pedido.setFecha(LocalDate.parse(scanner.nextLine()));

        System.out.print("Cliente: ");
        pedido.setClienteNombre(scanner.nextLine());

        System.out.print("Total: ");
        pedido.setTotal(Double.parseDouble(scanner.nextLine()));

        pedido.setEstado(seleccionarEstadoPedido());

        System.out.print("Tracking: ");
        envio.setTracking(scanner.nextLine());

        System.out.print("Costo: ");
        envio.setCosto(Double.parseDouble(scanner.nextLine()));

        envio.setEmpresa(seleccionarEmpresa());
        envio.setTipo(seleccionarTipoEnvio());
        envio.setEstado(seleccionarEstadoEnvio());
        
        envio.setFechaDespacho(LocalDate.now());
        envio.setFechaEstimada(LocalDate.now().plusDays(5));
        
        pedido.setEnvio(envio);
        
        confirmarCreacionPedido(pedido, pedidoService);
    }

    private void crearPedidoSinEnvio() throws Exception {
        PedidoService pedidoService = createPedidoService();
        System.out.println("\n--- CREAR PEDIDO SIN ENVÍO ---");

        // Crear y configurar el PEDIDO
        Pedido pedido = new Pedido();

        System.out.print("Número de pedido: ");
        pedido.setNumero(scanner.nextLine());

        System.out.print("Fecha (YYYY-MM-DD): ");
        pedido.setFecha(LocalDate.parse(scanner.nextLine()));

        System.out.print("Cliente: ");
        pedido.setClienteNombre(scanner.nextLine());

        System.out.print("Total: ");
        pedido.setTotal(Double.parseDouble(scanner.nextLine()));

        pedido.setEstado(seleccionarEstadoPedido());

        // Pedido sin envio
        pedido.setEnvio(null);
        
        confirmarCreacionPedido(pedido, pedidoService);
    }
    
    private void confirmarCreacionPedido(Pedido pedido, PedidoService pedidoService) {
        System.out.println("Confirmando datos del pedido:");
        System.out.println("Número: " + pedido.getNumero());
        System.out.println("Fecha: " + pedido.getFecha());
        System.out.println("Cliente: " + pedido.getClienteNombre());
        System.out.println("Total: $" + pedido.getTotal());
        System.out.println("Estado: " + pedido.getEstado());
        if (pedido.getEnvio() == null) {
            System.out.println("Envío: NO TIENE");
        } else {
            System.out.println("Envío: " + pedido.getEnvio().getEstado());
        }
            
        System.out.print("¿Confirmar creación del pedido? (S/N): ");
        String confirmacion = scanner.nextLine().trim().toUpperCase();

        if (confirmacion.equals("S") || confirmacion.equals("SI")) {
            try {
                pedidoService.insertar(pedido);
                System.out.println("Pedido creado con éxito.");
            } catch (Exception e) {
                System.out.println("Error al crear pedido: " + e.getMessage());
            }
        } else {
            System.out.println("Creación del pedido cancelada.");
        }
    }
    
    private void listarPedidos() throws Exception {
        System.out.println("\n--- LISTADO COMPLETO DE PEDIDOS ---");
        PedidoService pedidoService = createPedidoService();
        List<Pedido> pedidos = pedidoService.getAll();
        
        if (!pedidos.isEmpty()) {
            System.out.println("TOTAL DE PEDIDOS: " + pedidos.size());
            pedidos.forEach(p -> System.out.println(p));
        } else {
            System.out.println("No hay pedidos registrados en el sistema.");
        }
    }

    private void eliminarPedido() throws Exception {
        System.out.print("\nID del pedido a eliminar: ");
        PedidoService pedidoService = createPedidoService();
        Long id = Long.parseLong(scanner.nextLine());
        pedidoService.eliminar(id);
        System.out.println("Pedido eliminado (baja lógica).");
    }
    private PedidoService createPedidoService() {
        EnvioDao envioDao = new EnvioDao();
        PedidoDao pedidoDao = new PedidoDao();
        EnvioService envioService = new EnvioService(envioDao);
        return new PedidoService(pedidoDao, envioService);
    }
    
    private EnvioService createEnvioService() {
        EnvioDao envioDao = new EnvioDao();
        return new EnvioService(envioDao);
    }
    
    private void buscarPedidoPorNumero() throws Exception {
        System.out.println("\n--- BUSCAR PEDIDO POR NÚMERO ---");
        System.out.print("Ingrese el número de pedido: ");
        String numero = scanner.nextLine().trim();

        if (numero.isEmpty()) {
            System.out.println("El número de pedido no puede estar vacío.");
            return;
        }

        PedidoService pedidoService = createPedidoService();
        Pedido pedido = pedidoService.buscarPorNumero(numero);

        if (pedido != null) {
            System.out.println("\nPEDIDO ENCONTRADO:");
            System.out.println(pedido);
        } else {
            System.out.println("No se encontró ningún pedido con el número: " + numero);
        }
    }
    
    private void buscarPedidosPorCliente() throws Exception {
        System.out.println("\n--- BUSCAR PEDIDOS POR CLIENTE ---");
        System.out.print("Ingrese el nombre del cliente (puede ser parcial): ");
        String cliente = scanner.nextLine().trim();

        if (cliente.isEmpty()) {
            System.out.println("El nombre del cliente no puede estar vacío.");
            return;
        }

        PedidoService pedidoService = createPedidoService();
        List<Pedido> pedidos = pedidoService.buscarPorCliente(cliente);

        if (!pedidos.isEmpty()) {
            System.out.println("\nPEDIDOS ENCONTRADOS (" + pedidos.size() + "):");
            pedidos.forEach(p -> System.out.println("• " + p));
        } else {
            System.out.println("No se encontraron pedidos para el cliente: " + cliente);
        }
    }
    
    private void buscarPedidosPorEstado() throws Exception {
        System.out.println("\n--- BUSCAR PEDIDOS POR ESTADO ---");
        System.out.println("Estados disponibles: NUEVO, FACTURADO, ENVIADO");
        System.out.print("Ingrese el estado: ");
        String estado = scanner.nextLine().trim().toUpperCase();

        PedidoService pedidoService = createPedidoService();
        List<Pedido> pedidos = pedidoService.buscarPorEstado(estado);

        if (!pedidos.isEmpty()) {
            System.out.println("\nPEDIDOS ENCONTRADOS (" + pedidos.size() + "):");
            pedidos.forEach(p -> System.out.println("• " + p));
        } else {
            System.out.println("No se encontraron pedidos con estado: " + estado);
        }
    }
    
    private void buscarEnvioPorTracking() throws Exception {
        System.out.println("\n--- BUSCAR ENVÍO POR TRACKING ---");
        System.out.print("Ingrese el número de tracking: ");
        String tracking = scanner.nextLine().trim();

        if (tracking.isEmpty()) {
            System.out.println("El número de tracking no puede estar vacío.");
            return;
        }

        EnvioService envioService = createEnvioService();
        Envio envio = envioService.buscarPorTracking(tracking);

        if (envio != null) {
            System.out.println("\nENVÍO ENCONTRADO:");
            System.out.println(envio);
        } else {
            System.out.println("No se encontró ningún envío con tracking: " + tracking);
        }
    }
    
    private void buscarEnviosPorEmpresa() throws Exception {
        System.out.println("\n--- BUSCAR ENVÍOS POR EMPRESA ---");
        System.out.println("Empresas disponibles: ANDREANI, OCA, CORREO_ARG");
        System.out.print("Ingrese la empresa: ");
        String empresa = scanner.nextLine().trim().toUpperCase();

        EnvioService envioService = createEnvioService();
        List<Envio> envios = envioService.buscarPorEmpresa(empresa);

        if (!envios.isEmpty()) {
            System.out.println("\nENVÍOS ENCONTRADOS (" + envios.size() + "):");
            envios.forEach(e -> System.out.println("• " + e));
        } else {
            System.out.println("No se encontraron envíos para la empresa: " + empresa);
        }
    }
    
    private void buscarEnviosPorEstado() throws Exception {
        System.out.println("\n--- BUSCAR ENVÍOS POR ESTADO ---");
        System.out.println("Estados disponibles: EN_PREPARACION, EN_TRANSITO, ENTREGADO");
        System.out.print("Ingrese el estado: ");
        String estado = scanner.nextLine().trim().toUpperCase();

        EnvioService envioService = createEnvioService();
        List<Envio> envios = envioService.buscarPorEstado(estado);

        if (!envios.isEmpty()) {
            System.out.println("\nENVÍOS ENCONTRADOS (" + envios.size() + "):");
            envios.forEach(e -> System.out.println("• " + e));
        } else {
            System.out.println("No se encontraron envíos con estado: " + estado);
        }
    }
    
    private void listarEnvios() throws Exception {
        System.out.println("\n--- LISTADO COMPLETO DE ENVÍOS ---");
        EnvioService envioService = createEnvioService();
        List<Envio> envios = envioService.getAll();

        if (!envios.isEmpty()) {
            System.out.println("TOTAL DE ENVÍOS: " + envios.size());
            envios.forEach(e -> System.out.println(e));
        } else {
            System.out.println("No hay envíos registrados en el sistema.");
        }
    }
    
    private void actualizarPedido() throws Exception {
        System.out.println("\n--- ACTUALIZAR PEDIDO ---");
        System.out.print("Ingrese el ID del pedido a actualizar: ");
        Long id = Long.parseLong(scanner.nextLine());

        PedidoService pedidoService = createPedidoService();
        EnvioService envioService = createEnvioService();
        Pedido pedido = pedidoService.getById(id);

        if (pedido == null) {
            System.out.println("No se encontró el pedido con ID: " + id);
            return;
        }

        System.out.println("Pedido encontrado:");
        mostrarResumenPedido(pedido);
    
        // Actualizar datos básicos del pedido
        actualizarDatosBasicosPedido(pedido, pedidoService);
        
        // Volver a obtener el pedido luego de la actualizacion
        pedido = pedidoService.getById(id);
        // Gestión del envío
        gestionarEnvioPedido(pedido, pedidoService, envioService);
        
        // Volver a obtener el pedido luego de la actualizacion
        pedido = pedidoService.getById(id);

        System.out.println("Proceso de actualización completado.");
        mostrarResumenPedido(pedido);
    }
    
    // Métodos auxiliares para selección de enums
    private Envio.Empresa seleccionarEmpresa() {
        while (true) {
            System.out.println("\n--- SELECCIONAR EMPRESA DE ENVÍO ---");
            System.out.println("1. ANDREANI");
            System.out.println("2. OCA");
            System.out.println("3. CORREO_ARG");
            System.out.print("Seleccione una opción (1-3): ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1 -> { return Envio.Empresa.ANDREANI; }
                    case 2 -> { return Envio.Empresa.OCA; }
                    case 3 -> { return Envio.Empresa.CORREO_ARG; }
                    default -> System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido.");
            }
        }
    }

    private Envio.TipoEnvio seleccionarTipoEnvio() {
        while (true) {
            System.out.println("\n--- SELECCIONAR TIPO DE ENVÍO ---");
            System.out.println("1. ESTÁNDAR");
            System.out.println("2. EXPRÉS");
            System.out.print("Seleccione una opción (1-2): ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1 -> { return Envio.TipoEnvio.ESTANDAR; }
                    case 2 -> { return Envio.TipoEnvio.EXPRES; }
                    default -> System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido.");
            }
        }
    }

    private Envio.EstadoEnvio seleccionarEstadoEnvio() {
        while (true) {
            System.out.println("\n--- SELECCIONAR ESTADO DEL ENVÍO ---");
            System.out.println("1. EN PREPARACIÓN");
            System.out.println("2. EN TRÁNSITO");
            System.out.println("3. ENTREGADO");
            System.out.print("Seleccione una opción (1-3): ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1 -> { return Envio.EstadoEnvio.EN_PREPARACION; }
                    case 2 -> { return Envio.EstadoEnvio.EN_TRANSITO; }
                    case 3 -> { return Envio.EstadoEnvio.ENTREGADO; }
                    default -> System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido.");
            }
        }
    }

    private Pedido.EstadoPedido seleccionarEstadoPedido() {
        while (true) {
            System.out.println("\n--- SELECCIONAR ESTADO DEL PEDIDO ---");
            System.out.println("1. NUEVO");
            System.out.println("2. FACTURADO");
            System.out.println("3. ENVIADO");
            System.out.print("Seleccione una opción (1-3): ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1 -> { return Pedido.EstadoPedido.NUEVO; }
                    case 2 -> { return Pedido.EstadoPedido.FACTURADO; }
                    case 3 -> { return Pedido.EstadoPedido.ENVIADO; }
                    default -> System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido.");
            }
        }
    }
    
    private void actualizarDatosBasicosPedido(Pedido pedido, PedidoService pedidoService) throws Exception {
        System.out.println("ACTUALIZAR DATOS BÁSICOS (deje vacío para mantener valor actual):");

        String nuevoNumero = null;
        LocalDate nuevaFecha = null;
        String nuevoCliente = null;
        Double nuevoTotal = null;
        Pedido.EstadoPedido nuevoEstado = null;

        // Número de pedido
        System.out.print("Nuevo número de pedido [" + pedido.getNumero() + "]: ");
        String inputNumero = scanner.nextLine();
        if (!inputNumero.trim().isEmpty()) {
            nuevoNumero = inputNumero;
        }

        // Fecha
        System.out.print("Nueva fecha (YYYY-MM-DD) [" + pedido.getFecha() + "]: ");
        String inputFecha = scanner.nextLine();
        if (!inputFecha.trim().isEmpty()) {
            try {
                nuevaFecha = LocalDate.parse(inputFecha);
            } catch (Exception e) {
                System.out.println("Formato de fecha inválido. Se mantiene fecha actual.");
            }
        }

        // Cliente
        System.out.print("Nuevo nombre de cliente [" + pedido.getClienteNombre() + "]: ");
        String inputCliente = scanner.nextLine();
        if (!inputCliente.trim().isEmpty()) {
            nuevoCliente = inputCliente;
        }

        // Total
        System.out.print("Nuevo total [" + pedido.getTotal() + "]: ");
        String inputTotal = scanner.nextLine();
        if (!inputTotal.trim().isEmpty()) {
            try {
                nuevoTotal = Double.parseDouble(inputTotal);
            } catch (NumberFormatException e) {
                System.out.println("Formato de total inválido. Se mantiene valor actual.");
            }
        }

        // Estado del pedido
        System.out.println("Estado actual: " + pedido.getEstado());
        System.out.print("¿Cambiar estado? (S/N): ");
        String cambiarEstado = scanner.nextLine().trim().toUpperCase();
        if (cambiarEstado.equals("S") || cambiarEstado.equals("SI")) {
            nuevoEstado = seleccionarEstadoPedido();
        }

        // Llamar al servicio para aplicar los cambios
        if (nuevoNumero != null || nuevaFecha != null || nuevoCliente != null || nuevoTotal != null || nuevoEstado != null) {
            pedidoService.actualizarDatosBasicos(pedido.getId(), nuevoNumero, nuevaFecha, nuevoCliente, nuevoTotal, nuevoEstado);
            System.out.println("Datos básicos actualizados correctamente.");
        } else {
            System.out.println("No se realizaron cambios en los datos básicos.");
        }
    }

    private void gestionarEnvioPedido(Pedido pedido, PedidoService pedidoService, EnvioService envioService) throws Exception {
        System.out.println("\nGESTIÓN DE ENVÍO:");

        if (pedido.getEnvio() == null) {
            // Pedido sin envío - ofrecer crear uno
            System.out.println("Este pedido no tiene envío asociado.");
            System.out.print("¿Desea agregar un envío? (S/N): ");
            String agregarEnvio = scanner.nextLine().trim().toUpperCase();

            if (agregarEnvio.equals("S") || agregarEnvio.equals("SI")) {
                Envio nuevoEnvio = crearEnvioInteractivo();
                pedidoService.agregarEnvioAPedido(pedido.getId(), nuevoEnvio);
                System.out.println("Envío agregado al pedido.");
            }
        } else {
            // Pedido con envío existente - ofrecer opciones
            System.out.println("Opciones para el envío existente:");
            System.out.println("1. Actualizar datos del envío");
            System.out.println("2. Quitar envío del pedido");
            System.out.println("3. Mantener envío actual");
            System.out.print("Seleccione una opción (1-3): ");

            String opcionEnvio = scanner.nextLine().trim();
            switch (opcionEnvio) {
                case "1" -> {
                    actualizarEnvioExistente(pedido.getEnvio().getId(), envioService);
                    System.out.println("Envío actualizado.");
                }
                case "2" -> {
                    System.out.print("¿Confirmar que desea quitar el envío del pedido? (S/N): ");
                    String confirmar = scanner.nextLine().trim().toUpperCase();
                    if (confirmar.equals("S") || confirmar.equals("SI")) {
                        pedidoService.quitarEnvioDePedido(pedido.getId());
                        System.out.println("Envío removido del pedido.");
                    } else {
                        System.out.println("Operación cancelada. Se mantiene el envío.");
                    }
                }
                case "3" -> System.out.println("Se mantiene el envío actual.");
                default -> System.out.println("Opción inválida. Se mantiene el envío actual.");
            }
        }
    }

    private void actualizarEnvioExistente(Long envioId, EnvioService envioService) throws Exception {
        System.out.println("\n✏️  ACTUALIZAR ENVÍO EXISTENTE (deje vacío para mantener valor actual):");

        String nuevoTracking = null;
        Double nuevoCosto = null;
        Envio.Empresa nuevaEmpresa = null;
        Envio.TipoEnvio nuevoTipo = null;
        Envio.EstadoEnvio nuevoEstado = null;
        LocalDate nuevaFechaDespacho = null;
        LocalDate nuevaFechaEstimada = null;

        // Obtener envío actual para mostrar valores
        Envio envioActual = envioService.getById(envioId);

        // Tracking
        System.out.print("Nuevo tracking [" + envioActual.getTracking() + "]: ");
        String inputTracking = scanner.nextLine();
        if (!inputTracking.trim().isEmpty()) {
            nuevoTracking = inputTracking;
        }

        // Costo
        System.out.print("Nuevo costo [" + envioActual.getCosto() + "]: ");
        String inputCosto = scanner.nextLine();
        if (!inputCosto.trim().isEmpty()) {
            try {
                nuevoCosto = Double.parseDouble(inputCosto);
            } catch (NumberFormatException e) {
                System.out.println("Formato de costo inválido. Se mantiene valor actual.");
            }
        }

        // Empresa
        System.out.println("Empresa actual: " + envioActual.getEmpresa());
        System.out.print("¿Cambiar empresa? (S/N): ");
        String cambiarEmpresa = scanner.nextLine().trim().toUpperCase();
        if (cambiarEmpresa.equals("S") || cambiarEmpresa.equals("SI")) {
            nuevaEmpresa = seleccionarEmpresa();
        }

        // Tipo de envío
        System.out.println("Tipo actual: " + envioActual.getTipo());
        System.out.print("¿Cambiar tipo? (S/N): ");
        String cambiarTipo = scanner.nextLine().trim().toUpperCase();
        if (cambiarTipo.equals("S") || cambiarTipo.equals("SI")) {
            nuevoTipo = seleccionarTipoEnvio();
        }

        // Estado del envío
        System.out.println("Estado actual: " + envioActual.getEstado());
        System.out.print("¿Cambiar estado? (S/N): ");
        String cambiarEstado = scanner.nextLine().trim().toUpperCase();
        if (cambiarEstado.equals("S") || cambiarEstado.equals("SI")) {
            nuevoEstado = seleccionarEstadoEnvio();
        }

        // Fechas
        System.out.print("Nueva fecha de despacho (YYYY-MM-DD) [" + envioActual.getFechaDespacho() + "]: ");
        String inputFechaDespacho = scanner.nextLine();
        if (!inputFechaDespacho.trim().isEmpty()) {
            try {
                nuevaFechaDespacho = LocalDate.parse(inputFechaDespacho);
            } catch (Exception e) {
                System.out.println("Formato de fecha inválido. Se mantiene fecha actual.");
            }
        }

        System.out.print("Nueva fecha estimada (YYYY-MM-DD) [" + envioActual.getFechaEstimada() + "]: ");
        String inputFechaEstimada = scanner.nextLine();
        if (!inputFechaEstimada.trim().isEmpty()) {
            try {
                nuevaFechaEstimada = LocalDate.parse(inputFechaEstimada);
            } catch (Exception e) {
                System.out.println("Formato de fecha inválido. Se mantiene fecha actual.");
            }
        }

        // Llamar al servicio para aplicar los cambios
        if (nuevoTracking != null || nuevoCosto != null || nuevaEmpresa != null || nuevoTipo != null || 
            nuevoEstado != null || nuevaFechaDespacho != null || nuevaFechaEstimada != null) {
            envioService.actualizarDatosEnvio(envioId, nuevoTracking, nuevoCosto, nuevaEmpresa, nuevoTipo, nuevoEstado, nuevaFechaDespacho, nuevaFechaEstimada);
        } else {
            System.out.println("No se realizaron cambios en el envío.");
        }
    }
    
    private void mostrarResumenPedido(Pedido pedido) {
        System.out.println("\nINFORMACIÓN ACTUAL DEL PEDIDO:");
        System.out.println("  ID: " + pedido.getId());
        System.out.println("  Número: " + pedido.getNumero());
        System.out.println("  Fecha: " + pedido.getFecha());
        System.out.println("  Cliente: " + pedido.getClienteNombre());
        System.out.println("  Total: $" + pedido.getTotal());
        System.out.println("  Estado: " + pedido.getEstado());

        if (pedido.getEnvio() != null) {
            System.out.println("ENVÍO ASOCIADO:");
            System.out.println(" Tracking: " + pedido.getEnvio().getTracking());
            System.out.println(" Empresa: " + pedido.getEnvio().getEmpresa());
            System.out.println(" Tipo: " + pedido.getEnvio().getTipo());
            System.out.println(" Costo: $" + pedido.getEnvio().getCosto());
            System.out.println(" Estado: " + pedido.getEnvio().getEstado());
            System.out.println(" Fecha despacho: " + pedido.getEnvio().getFechaDespacho());
            System.out.println(" Fecha estimada: " + pedido.getEnvio().getFechaEstimada());
        } else {
            System.out.println("ENVÍO: NO TIENE ENVÍO ASOCIADO");
        }
        System.out.println();
    }
    
    private Envio crearEnvioInteractivo() throws Exception {
        System.out.println("\nCREAR NUEVO ENVÍO:");

        // Recolectar datos del usuario
        System.out.print("Número de tracking: ");
        String tracking = scanner.nextLine();

        // Validación de costo
        double costo = 0.0;
        while (true) {
            System.out.print("Costo del envío: ");
            try {
                costo = Double.parseDouble(scanner.nextLine());
                if (costo < 0) {
                    System.out.println("El costo no puede ser negativo.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido.");
            }
        }

        Envio.Empresa empresa = seleccionarEmpresa();
        Envio.TipoEnvio tipo = seleccionarTipoEnvio();
        Envio.EstadoEnvio estado = seleccionarEstadoEnvio();
        LocalDate fechaDespacho = ingresarFecha("Fecha de despacho");
        LocalDate fechaEstimada = ingresarFecha("Fecha estimada de entrega");

        // Crear el objeto Envio con los datos recolectados
        Envio envio = new Envio();
        envio.setTracking(tracking);
        envio.setCosto(costo);
        envio.setEmpresa(empresa);
        envio.setTipo(tipo);
        envio.setEstado(estado);
        envio.setFechaDespacho(fechaDespacho);
        envio.setFechaEstimada(fechaEstimada);

        // Validar que la fecha estimada sea posterior a la de despacho
        if (fechaEstimada.isBefore(fechaDespacho)) {
            System.out.println("⚠️  Advertencia: La fecha estimada de entrega es anterior a la fecha de despacho.");
        }

        return envio;
    }
    
    // Agregar fechas validadas
    private LocalDate ingresarFecha(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (YYYY-MM-DD): ");
            String fechaStr = scanner.nextLine().trim();

            if (fechaStr.isEmpty()) {
                System.out.println("La fecha no puede estar vacía.");
                continue;
            }

            try {
                return LocalDate.parse(fechaStr);
            } catch (Exception e) {
                System.out.println("Formato de fecha inválido. Use el formato YYYY-MM-DD (ej: 2024-01-15).");
            }
        }
    }
}