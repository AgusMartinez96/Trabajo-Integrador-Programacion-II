
package main;

/**
 *
 * @author Andres Meshler
 */
public class MenuDisplay {
    
    public void mostrarMenuPrincipal() {
        System.out.println("\n--- MENÚ PRINCIPAL ---");
        System.out.println("1. Gestión de Pedidos");
        System.out.println("2. Búsquedas y Consultas");
        System.out.println("3. Listados Completos");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }
    
    public void mostrarMenuGestionPedidos() {
        System.out.println("\n--- GESTIÓN DE PEDIDOS ---");
        System.out.println("1. Crear nuevo pedido con envío");
        System.out.println("2. Crear nuevo pedido sin envío");
        System.out.println("3. Actualizar pedido");
        System.out.println("4. Eliminar pedido (baja lógica)");
        System.out.println("5. Volver al menú principal");
        System.out.print("Seleccione una opción: ");
    }
    
    public void mostrarMenuBusquedas() {
        System.out.println("\n--- BÚSQUEDAS Y CONSULTAS ---");
        System.out.println("1. Buscar pedido por número");
        System.out.println("2. Buscar pedidos por cliente");
        System.out.println("3. Buscar pedidos por estado");
        System.out.println("4. Buscar envío por tracking");
        System.out.println("5. Buscar envíos por empresa");
        System.out.println("6. Buscar envíos por estado");
        System.out.println("7. Volver al menú principal");
        System.out.print("Seleccione una opción: ");
    }
    
    public void mostrarMenuListados() {
        System.out.println("\n--- LISTADOS COMPLETOS ---");
        System.out.println("1. Listar todos los pedidos");
        System.out.println("2. Listar todos los envíos");
        System.out.println("3. Volver al menú principal");
        System.out.print("Seleccione una opción: ");
    }
    
    public void mostrarTitulo(String titulo) {
        System.out.println("\n--- " + titulo + " ---");
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarError(String error) {
        System.out.println("Error: " + error);
    }
}
