package main;

import java.util.Scanner;

public class AppMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final MenuDisplay menuDisplay = new MenuDisplay();
    private final MenuHandler menuHandler = new MenuHandler(scanner);

    public void iniciar() {
        System.out.println("=== SISTEMA DE GESTIÓN DE PEDIDOS Y ENVÍOS ===");
    
        while (true) {
            menuDisplay.mostrarMenuPrincipal();

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
            menuDisplay.mostrarMenuGestionPedidos();

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1 -> menuHandler.crearPedidoConEnvio();
                    case 2 -> menuHandler.crearPedidoSinEnvio();
                    case 3 -> menuHandler.actualizarPedido();
                    case 4 -> menuHandler.eliminarPedido();
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
        while (true) {
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