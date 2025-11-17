
package main;

import entities.Pedido;
import entities.Envio;
import service.PedidoService;
import service.EnvioService;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


public class MenuHandler {
    
    private final Scanner scanner;
    private final PedidoService pedidoService;
    private final EnvioService envioService;
    private final MenuDisplay menuDisplay = new MenuDisplay();
    
    public MenuHandler(Scanner scanner, PedidoService pedidoService, EnvioService envioService) {
        
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner no puede ser null");
        }
        if (pedidoService == null) {
            throw new IllegalArgumentException("PedidoService no puede ser null");
        }
        if (envioService == null) {
            throw new IllegalArgumentException("EnvioService no puede ser null");
        }
        this.scanner = scanner;
        this.pedidoService = pedidoService;
        this.envioService = envioService;
    }
 
        // Métodos de creación de pedidos
    public void crearPedidoConEnvio() throws Exception {
        
        menuDisplay.mostrarTitulo("CREAR PEDIDO CON ENVÍO");
        
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
        
        confirmarCreacionPedido(pedido);
    }
    
    public void crearPedidoSinEnvio() throws Exception {
        
        menuDisplay.mostrarTitulo("CREAR PEDIDO SIN ENVÍO");

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
        pedido.setEnvio(null);
        
        confirmarCreacionPedido(pedido);
    }

    // Métodos de búsqueda
    public void buscarPedidoPorNumero() throws Exception {
        menuDisplay.mostrarTitulo("BUSCAR PEDIDO POR NÚMERO");
        System.out.print("Ingrese el número de pedido: ");
        String numero = scanner.nextLine().trim();

        if (numero.isEmpty()) {
            menuDisplay.mostrarError("El número de pedido no puede estar vacío.");
            return;
        }

        Pedido pedido = pedidoService.buscarPorNumero(numero);

        if (pedido != null) {
            System.out.println("\nPEDIDO ENCONTRADO:");
            System.out.println(pedido);
        } else {
            System.out.println("No se encontró ningún pedido con el número: " + numero);
        }
    }
    
    public void buscarPedidosPorCliente() throws Exception {
        menuDisplay.mostrarTitulo("BUSCAR PEDIDOS POR CLIENTE");
        System.out.print("Ingrese el nombre del cliente (puede ser parcial): ");
        String cliente = scanner.nextLine().trim();

        if (cliente.isEmpty()) {
            menuDisplay.mostrarError("El nombre del cliente no puede estar vacío.");
            return;
        }

        List<Pedido> pedidos = pedidoService.buscarPorCliente(cliente);

        if (!pedidos.isEmpty()) {
            System.out.println("\nPEDIDOS ENCONTRADOS (" + pedidos.size() + "):");
            pedidos.forEach(p -> System.out.println("• " + p));
        } else {
            System.out.println("No se encontraron pedidos para el cliente: " + cliente);
        }
    }
    
    public void buscarPedidosPorEstado() throws Exception {
        menuDisplay.mostrarTitulo("BUSCAR PEDIDOS POR ESTADO");
        System.out.println("Estados disponibles: NUEVO, FACTURADO, ENVIADO");
        System.out.print("Ingrese el estado: ");
        String estado = scanner.nextLine().trim().toUpperCase();

        List<Pedido> pedidos = pedidoService.buscarPorEstado(estado);

        if (!pedidos.isEmpty()) {
            System.out.println("\nPEDIDOS ENCONTRADOS (" + pedidos.size() + "):");
            pedidos.forEach(p -> System.out.println("• " + p));
        } else {
            System.out.println("No se encontraron pedidos con estado: " + estado);
        }
    }
    
    public void buscarEnvioPorTracking() throws Exception {
        menuDisplay.mostrarTitulo("BUSCAR ENVÍO POR TRACKING");
        System.out.print("Ingrese el número de tracking: ");
        String tracking = scanner.nextLine().trim();

        if (tracking.isEmpty()) {
            menuDisplay.mostrarError("El número de tracking no puede estar vacío.");
            return;
        }

        Envio envio = envioService.buscarPorTracking(tracking);

        if (envio != null) {
            System.out.println("\nENVÍO ENCONTRADO:");
            System.out.println(envio);
        } else {
            System.out.println("No se encontró ningún envío con tracking: " + tracking);
        }
    }
    
    public void buscarEnviosPorEmpresa() throws Exception {
        menuDisplay.mostrarTitulo("BUSCAR ENVÍOS POR EMPRESA");
        System.out.println("Empresas disponibles: ANDREANI, OCA, CORREO_ARG");
        System.out.print("Ingrese la empresa: ");
        String empresa = scanner.nextLine().trim().toUpperCase();

        List<Envio> envios = envioService.buscarPorEmpresa(empresa);

        if (!envios.isEmpty()) {
            System.out.println("\nENVÍOS ENCONTRADOS (" + envios.size() + "):");
            envios.forEach(e -> System.out.println("• " + e));
        } else {
            System.out.println("No se encontraron envíos para la empresa: " + empresa);
        }
    }
    
    public void buscarEnviosPorEstado() throws Exception {
        menuDisplay.mostrarTitulo("BUSCAR ENVÍOS POR ESTADO");
        System.out.println("Estados disponibles: EN_PREPARACION, EN_TRANSITO, ENTREGADO");
        System.out.print("Ingrese el estado: ");
        String estado = scanner.nextLine().trim().toUpperCase();

        List<Envio> envios = envioService.buscarPorEstado(estado);

        if (!envios.isEmpty()) {
            System.out.println("\nENVÍOS ENCONTRADOS (" + envios.size() + "):");
            envios.forEach(e -> System.out.println("• " + e));
        } else {
            System.out.println("No se encontraron envíos con estado: " + estado);
        }
    }
    
    // Métodos de listado
    public void listarPedidos() throws Exception {
        menuDisplay.mostrarTitulo("LISTADO COMPLETO DE PEDIDOS");
        List<Pedido> pedidos = pedidoService.getAll();
        
        if (!pedidos.isEmpty()) {
            System.out.println("TOTAL DE PEDIDOS: " + pedidos.size());
            pedidos.forEach(p -> System.out.println(p));
        } else {
            System.out.println("No hay pedidos registrados en el sistema.");
        }
    }
    
    public void listarEnvios() throws Exception {
        menuDisplay.mostrarTitulo("LISTADO COMPLETO DE ENVÍOS");
        List<Envio> envios = envioService.getAll();

        if (!envios.isEmpty()) {
            System.out.println("TOTAL DE ENVÍOS: " + envios.size());
            envios.forEach(e -> System.out.println(e));
        } else {
            System.out.println("No hay envíos registrados en el sistema.");
        }
    }
    
    // Métodos de gestión
    public void actualizarPedido() throws Exception {
        menuDisplay.mostrarTitulo("ACTUALIZAR PEDIDO");
        System.out.print("Ingrese el ID del pedido a actualizar: ");
        Long id = Long.parseLong(scanner.nextLine());

        Pedido pedido = pedidoService.getById(id);

        if (pedido == null) {
            System.out.println("No se encontró el pedido con ID: " + id);
            return;
        }

        System.out.println("Pedido encontrado:");
        mostrarResumenPedido(pedido);
    
        actualizarDatosBasicosPedido(pedido);
        pedido = pedidoService.getById(id);
        gestionarEnvioPedido(pedido);
        pedido = pedidoService.getById(id);

        System.out.println("Proceso de actualización completado.");
        mostrarResumenPedido(pedido);
    }
    
    public void eliminarPedido() throws Exception {
        System.out.print("\nID del pedido a eliminar: ");

        Long id = Long.parseLong(scanner.nextLine());
        
        Pedido pedido = pedidoService.getById(id);

        if (pedido == null) {
            System.out.println("No se encontró ningún pedido con id: " + id);
            return;
        }

        System.out.println("Pedido encontrado:");
        mostrarResumenPedido(pedido);

        // Confirmar eliminación
        System.out.print("¿Está seguro de que desea eliminar este pedido? (S/N): ");
        String confirmacion = scanner.nextLine().trim().toUpperCase();

        if (confirmacion.equals("S") || confirmacion.equals("SI")) {
            try {
                pedidoService.eliminar(id);
                System.out.println("Pedido eliminado correctamente(baja lógica).");
            } catch (Exception e) {
                System.out.println("Error al eliminar el pedido: " + e.getMessage());
            }
        } else {
            System.out.println("Eliminación cancelada.");
        }
    }
        
    
    public void actualizarEnvio() throws Exception {
        menuDisplay.mostrarTitulo("ACTUALIZAR ENVÍO");
        
        // Buscar envío por tracking
        System.out.print("Ingrese el número de tracking del envío a actualizar: ");
        String tracking = scanner.nextLine().trim();

        if (tracking.isEmpty()) {
            System.out.println("El tracking no puede estar vacío.");
            return;
        }

        Envio envio = envioService.buscarPorTracking(tracking);

        if (envio == null) {
            System.out.println("No se encontró ningún envío con tracking: " + tracking);
            return;
        }

        System.out.println("Envío encontrado:");
        mostrarResumenEnvio(envio);

        // Actualizar datos del envío
        actualizarEnvioExistente(envio.getId());
        
        System.out.println("Envío actualizado correctamente.");
    }
    
    public void eliminarEnvio() throws Exception {
        menuDisplay.mostrarTitulo("ELIMINAR ENVÍO");
        
        // Buscar envío por tracking
        System.out.print("Ingrese el número de tracking del envío a eliminar: ");
        String tracking = scanner.nextLine().trim();

        if (tracking.isEmpty()) {
            System.out.println("El tracking no puede estar vacío.");
            return;
        }

        Envio envio = envioService.buscarPorTracking(tracking);

        if (envio == null) {
            System.out.println("No se encontró ningún envío con tracking: " + tracking);
            return;
        }

        System.out.println("Envío encontrado:");
        mostrarResumenEnvio(envio);

        // Confirmar eliminación
        System.out.print("¿Está seguro de que desea eliminar este envío? (S/N): ");
        String confirmacion = scanner.nextLine().trim().toUpperCase();

        if (confirmacion.equals("S") || confirmacion.equals("SI")) {
            try {
                envioService.eliminar(envio.getId());
                System.out.println("Envío eliminado correctamente (baja lógica).");
            } catch (Exception e) {
                System.out.println("Error al eliminar el envío: " + e.getMessage());
            }
        } else {
            System.out.println("Eliminación cancelada.");
        }
    }
    
    // Métodos auxiliares privados
    private void confirmarCreacionPedido(Pedido pedido) {
        System.out.println("\nConfirmando datos del pedido:");
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
    
    // Métodos para selección de enums
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
    
    private void actualizarDatosBasicosPedido(Pedido pedido) throws Exception {
        System.out.println("ACTUALIZAR DATOS BÁSICOS (deje vacío para mantener valor actual):");

        String nuevoNumero = null;
        LocalDate nuevaFecha = null;
        String nuevoCliente = null;
        Double nuevoTotal = null;
        Pedido.EstadoPedido nuevoEstado = null;

        System.out.print("Nuevo número de pedido [" + pedido.getNumero() + "]: ");
        String inputNumero = scanner.nextLine();
        if (!inputNumero.trim().isEmpty()) {
            nuevoNumero = inputNumero;
        }

        System.out.print("Nueva fecha (YYYY-MM-DD) [" + pedido.getFecha() + "]: ");
        String inputFecha = scanner.nextLine();
        if (!inputFecha.trim().isEmpty()) {
            try {
                nuevaFecha = LocalDate.parse(inputFecha);
            } catch (Exception e) {
                System.out.println("Formato de fecha inválido. Se mantiene fecha actual.");
            }
        }

        System.out.print("Nuevo nombre de cliente [" + pedido.getClienteNombre() + "]: ");
        String inputCliente = scanner.nextLine();
        if (!inputCliente.trim().isEmpty()) {
            nuevoCliente = inputCliente;
        }

        System.out.print("Nuevo total [" + pedido.getTotal() + "]: ");
        String inputTotal = scanner.nextLine();
        if (!inputTotal.trim().isEmpty()) {
            try {
                nuevoTotal = Double.parseDouble(inputTotal);
            } catch (NumberFormatException e) {
                System.out.println("Formato de total inválido. Se mantiene valor actual.");
            }
        }

        System.out.println("Estado actual: " + pedido.getEstado());
        System.out.print("¿Cambiar estado? (S/N): ");
        String cambiarEstado = scanner.nextLine().trim().toUpperCase();
        if (cambiarEstado.equals("S") || cambiarEstado.equals("SI")) {
            nuevoEstado = seleccionarEstadoPedido();
        }

        if (nuevoNumero != null || nuevaFecha != null || nuevoCliente != null || nuevoTotal != null || nuevoEstado != null) {
            pedidoService.actualizarDatosBasicos(pedido.getId(), nuevoNumero, nuevaFecha, nuevoCliente, nuevoTotal, nuevoEstado);
            //pedidoService.actualizar(pedido);
            System.out.println("Datos básicos actualizados correctamente.");
        } else {
            System.out.println("No se realizaron cambios en los datos básicos.");
        }
    }
    
    private void gestionarEnvioPedido(Pedido pedido) throws Exception {
        System.out.println("\nGESTIÓN DE ENVÍO:");

        if (pedido.getEnvio() == null) {
            System.out.println("Este pedido no tiene envío asociado.");
            System.out.print("¿Desea agregar un envío? (S/N): ");
            String agregarEnvio = scanner.nextLine().trim().toUpperCase();

            if (agregarEnvio.equals("S") || agregarEnvio.equals("SI")) {
                Envio nuevoEnvio = crearEnvioInteractivo();
                pedidoService.agregarEnvioAPedido(pedido.getId(), nuevoEnvio);
                System.out.println("Envío agregado al pedido.");
            }
        } else {
            System.out.println("Opciones para el envío existente:");
            System.out.println("1. Actualizar datos del envío");
            System.out.println("2. Quitar envío del pedido");
            System.out.println("3. Mantener envío actual");
            System.out.print("Seleccione una opción (1-3): ");

            String opcionEnvio = scanner.nextLine().trim();
            switch (opcionEnvio) {
                case "1" -> {
                    actualizarEnvioExistente(pedido.getEnvio().getId());
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
    
    private void actualizarEnvioExistente(Long envioId) throws Exception {
        System.out.println("\nACTUALIZAR ENVÍO EXISTENTE (deje vacío para mantener valor actual):");

        Envio envioActual = envioService.getById(envioId);

        String nuevoTracking = null;
        Double nuevoCosto = null;
        Envio.Empresa nuevaEmpresa = null;
        Envio.TipoEnvio nuevoTipo = null;
        Envio.EstadoEnvio nuevoEstado = null;
        LocalDate nuevaFechaDespacho = null;
        LocalDate nuevaFechaEstimada = null;

        System.out.print("Nuevo tracking [" + envioActual.getTracking() + "]: ");
        String inputTracking = scanner.nextLine();
        if (!inputTracking.trim().isEmpty()) {
            nuevoTracking = inputTracking;
        }

        System.out.print("Nuevo costo [" + envioActual.getCosto() + "]: ");
        String inputCosto = scanner.nextLine();
        if (!inputCosto.trim().isEmpty()) {
            try {
                nuevoCosto = Double.parseDouble(inputCosto);
            } catch (NumberFormatException e) {
                System.out.println("Formato de costo inválido. Se mantiene valor actual.");
            }
        }

        System.out.println("Empresa actual: " + envioActual.getEmpresa());
        System.out.print("¿Cambiar empresa? (S/N): ");
        String cambiarEmpresa = scanner.nextLine().trim().toUpperCase();
        if (cambiarEmpresa.equals("S") || cambiarEmpresa.equals("SI")) {
            nuevaEmpresa = seleccionarEmpresa();
        }

        System.out.println("Tipo actual: " + envioActual.getTipo());
        System.out.print("¿Cambiar tipo? (S/N): ");
        String cambiarTipo = scanner.nextLine().trim().toUpperCase();
        if (cambiarTipo.equals("S") || cambiarTipo.equals("SI")) {
            nuevoTipo = seleccionarTipoEnvio();
        }

        System.out.println("Estado actual: " + envioActual.getEstado());
        System.out.print("¿Cambiar estado? (S/N): ");
        String cambiarEstado = scanner.nextLine().trim().toUpperCase();
        if (cambiarEstado.equals("S") || cambiarEstado.equals("SI")) {
            nuevoEstado = seleccionarEstadoEnvio();
        }

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

        System.out.print("Número de tracking: ");
        String tracking = scanner.nextLine();

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

        Envio envio = new Envio();
        envio.setTracking(tracking);
        envio.setCosto(costo);
        envio.setEmpresa(empresa);
        envio.setTipo(tipo);
        envio.setEstado(estado);
        envio.setFechaDespacho(fechaDespacho);
        envio.setFechaEstimada(fechaEstimada);

        if (fechaEstimada.isBefore(fechaDespacho)) {
            System.out.println("Advertencia: La fecha estimada de entrega es anterior a la fecha de despacho.");
        }

        return envio;
    }
    
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
                System.out.println("Formato de fecha inválido. Use el formato YYYY-MM-DD (ej: 2025-10-15).");
            }
        }
    }
    
    private void mostrarResumenEnvio(Envio envio) {
        System.out.println("\n INFORMACIÓN DEL ENVÍO:");
        System.out.println("   ID: " + envio.getId());
        System.out.println("   Tracking: " + envio.getTracking());
        System.out.println("   Empresa: " + envio.getEmpresa());
        System.out.println("   Tipo: " + envio.getTipo());
        System.out.println("   Estado: " + envio.getEstado());
        System.out.println("   Costo: $" + envio.getCosto());
        System.out.println("   Fecha despacho: " + envio.getFechaDespacho());
        System.out.println("   Fecha estimada: " + envio.getFechaEstimada());
    }
}
