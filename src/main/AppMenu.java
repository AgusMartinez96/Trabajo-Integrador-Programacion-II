package main;

import java.util.Scanner;
import dao.EnvioDao;
import dao.PedidoDao;
import service.PedidoService;
import service.EnvioService;

public class AppMenu {

    private final Scanner scanner;
    private final MenuDisplay menuDisplay;
    private final MenuHandler menuHandler;
    private final boolean running;
    
    // inicializar con todas las dependencias
    
    public AppMenu() {
        this.scanner = new Scanner(System.in);
        this.menuDisplay = new MenuDisplay();
        PedidoService pedidoService = createPedidoService();
        EnvioService envioService = createEnvioService();
        this.menuHandler = new MenuHandler(scanner, pedidoService, envioService);
        this.running = true;
    }
    
    private PedidoService createPedidoService() {
        EnvioDao envioDAO = new EnvioDao();
        PedidoDao pedidoDao = new PedidoDao(envioDAO);
        EnvioService envioService = new EnvioService(envioDAO);
        return new PedidoService(pedidoDao, envioService);
    }
    
    private EnvioService createEnvioService() {
        EnvioDao envioDAO = new EnvioDao();
        return new EnvioService(envioDAO);
    }

    public void iniciar() {
        System.out.println("=== SISTEMA DE GESTIÓN DE PEDIDOS Y ENVÍOS ===");
    
        while (running) {
            menuDisplay.mostrarMenuPrincipal();

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1 -> menuGestionPedidosYEnvios();
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

    private void menuGestionPedidosYEnvios() {
        while (running) {
            menuDisplay.mostrarMenuGestionPedidosYEnvios();

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1 -> menuHandler.crearPedidoConEnvio();
                    case 2 -> menuHandler.crearPedidoSinEnvio();
                    case 3 -> menuHandler.actualizarPedido();
                    case 4 -> menuHandler.eliminarPedido();
                    case 5 -> menuHandler.actualizarEnvio();
                    case 6 -> menuHandler.eliminarEnvio();
                    case 7 -> { return; }
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
        while (running) {
            menuDisplay.mostrarMenuBusquedas();

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1 -> menuHandler.buscarPedidoPorNumero();
                    case 2 -> menuHandler.buscarPedidosPorCliente();
                    case 3 -> menuHandler.buscarPedidosPorEstado();
                    case 4 -> menuHandler.buscarEnvioPorTracking();
                    case 5 -> menuHandler.buscarEnviosPorEmpresa();
                    case 6 -> menuHandler.buscarEnviosPorEstado();
                    case 7 -> { return; }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void menuListados() {
        while (running) {
            menuDisplay.mostrarMenuListados();

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1 -> menuHandler.listarPedidos();
                    case 2 -> menuHandler.listarEnvios();
                    case 3 -> { return; }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}