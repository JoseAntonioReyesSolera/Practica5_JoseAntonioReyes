package practica;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    //private static final String HOST = "192.168.2.241";
    private static final String HOST = "localhost";
    private static final int PORT = 5100; //5000
    private SecretKey sharedKey;

    public void start() {
        try (Socket socket = new Socket(HOST, PORT)) {
            ColoresConsola.cliente(" Conectado al servidor");

            try (
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    Scanner scanner = new Scanner(System.in)
            ) {
                //Paso 0: Solicitar clave publica al servidor
                ColoresConsola.cliente(" 1. Solicitando clave pública al servidor...");
                out.writeObject("REQUEST_PUBLIC_KEY");

                // Paso 1: Recibir clave pública del servidor
                ColoresConsola.cliente(" Recibiendo clave pública del servidor...");
                PublicKey publicKey = (PublicKey) in.readObject();
                ColoresConsola.cliente(" Clave pública recibida: " + publicKey);

                // Paso 2: Generar clave simétrica
                ColoresConsola.cliente(" 3. Generando clave simétrica...");
                sharedKey = AES_Simetric.keygenKeyGeneration(128);
                ColoresConsola.cliente(" Clave simétrica generada: " + new String(sharedKey.getEncoded()));

                // Paso 3: Cifrar y enviar clave simétrica al servidor
                byte[] encryptedKey = RSA_Asimetric.encryptData(sharedKey.getEncoded(), publicKey);
                byte[] hash = Hash.hash(sharedKey.getEncoded());
                out.writeObject(new Packet(encryptedKey, hash));
                out.flush();
                ColoresConsola.cliente(" Clave simétrica enviada al servidor");

                // Paso 4: Enviar mensajes al servidor
                while (true) {
                    System.out.println(ColoresConsola.colorize("5. Introduce un mensaje:", ColoresConsola.GREEN));
                    String message = scanner.nextLine();

                    // Cifrar mensaje
                    byte[] encryptedMessage = AES_Simetric.encryptData(sharedKey, message.getBytes());
                    byte[] messageHash = Hash.hash(message.getBytes());
                    ColoresConsola.cliente(" Hash enviado: \n" + Arrays.toString(messageHash));

                    out.writeObject(new Packet(encryptedMessage, messageHash));
                    out.flush();

                    // Recibir acuse de recibo
                    Packet response = (Packet) in.readObject();
                    byte[] decryptedResponse = AES_Simetric.decryptData(sharedKey, response.message);
                    ColoresConsola.cliente(" Respuesta del servidor: " + new String(decryptedResponse));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
