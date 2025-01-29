package practica;

public class ColoresConsola {
    // Códigos ANSI para colores de texto
    public static final String RESET = "\033[0m"; // Restablece el color predeterminado
    public static final String RED = "\033[31m"; // Rojo
    public static final String GREEN = "\033[92m"; // Intenso Verde
    public static final String YELLOW = "\033[33m"; // Amarillo
    public static final String BLUE = "\033[94m"; // Intenso Azul
    public static final String CYAN = "\033[96m"; // Cian
    public static final String MAGENTA = "\033[95m"; // Intenso Magenta

    // Métodos para aplicar colores al texto
    public static String colorize(String text, String color) {
        return color + text + RESET;
    }

    // Métodos predefinidos para cliente y servidor
    public static void cliente(String text) {
        System.out.println(colorize("[CLIENTE] " + text, CYAN)); // Mensajes del cliente en azul
    }

    public static void servidor(String text) {
        System.out.println(colorize("[SERVIDOR] " + text, MAGENTA)); // Mensajes del servidor en verde
    }

    public static void error(String text) {
        System.out.println(colorize("[ERROR] " + text, RED)); // Mensajes de error en rojo
    }

    public static void advertencia(String text) {
        System.out.println(colorize(text, YELLOW)); // Mensajes de advertencia en amarillo
    }
}
