package main;

import dao.EnvioDao;
import dao.PedidoDao;
import entities.Pedido;
import entities.Envio;
import entities.Pedido.EstadoPedido;
import entities.Envio.Empresa;
import entities.Envio.TipoEnvio;
import entities.Envio.EstadoEnvio;
import service.PedidoService;
import java.time.LocalDate;
import java.util.Scanner;
import service.EnvioService;

public class AppMenu {

    //private final PedidoService pedidoService = new PedidoService();
    private final Scanner scanner = new Scanner(System.in);

    public void iniciar() {
        while (true) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Crear pedido con envío");
            System.out.println("2. Listar pedidos");
            System.out.println("3. Eliminar pedido");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            int opcion = Integer.parseInt(scanner.nextLine());

            try {
                switch (opcion) {
                    case 1 -> crearPedidoConEnvio();
                    case 2 -> listarPedidos();
                    case 3 -> eliminarPedido();
                    case 0 -> {
                        System.out.println("Saliendo...");
                        return;
                    }
                    default -> System.out.println("Opción inválida");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void crearPedidoConEnvio() throws Exception {
        
        PedidoService pedidoService = createPedidoService();
        System.out.println("\n--- CREAR PEDIDO ---");
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

        pedido.setEstado(EstadoPedido.NUEVO);

        System.out.print("Tracking: ");
        envio.setTracking(scanner.nextLine());

        System.out.print("Costo: ");
        envio.setCosto(Double.parseDouble(scanner.nextLine()));

        envio.setEmpresa(Empresa.ANDREANI);
        envio.setTipo(TipoEnvio.ESTANDAR);
        envio.setEstado(EstadoEnvio.EN_PREPARACION);
        envio.setFechaDespacho(LocalDate.now());
        envio.setFechaEstimada(LocalDate.now().plusDays(5));

        //pedidoService.crearPedidoConEnvio(pedido, envio);
        pedidoService.insertar(pedido);
        System.out.println("Pedido creado con éxito.");
    }

    private void listarPedidos() throws Exception {
        System.out.println("\n--- LISTADO DE PEDIDOS ---");
        PedidoService pedidoService = createPedidoService();
        for (Pedido p : pedidoService.leerTodos()) {
            System.out.println(p);
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
}