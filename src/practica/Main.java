package practica;

public class Main {
    public static void main(String[] args) {
        // Inicia el servidor en un hilo separado
        new Thread(() -> {
            Server server = new Server();
            server.start();
        }).start();

        // Espera un poco para que el servidor inicie
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Inicia el cliente
        Client client = new Client();
        client.start();
    }
}
