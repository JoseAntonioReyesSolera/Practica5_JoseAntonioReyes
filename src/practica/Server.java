package practica;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Server {
    private static final int PORT = 5100;
    private KeyPair keyPair;
    private SecretKey sharedKey;

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            ColoresConsola.servidor("Iniciado en el puerto " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ColoresConsola.servidor("Cliente conectado");

                // Generar claves RSA (pública y privada)
                ColoresConsola.servidor("Generando claves RSA...");
                keyPair = RSA_Asimetric.randomGenerate(2048);
                PublicKey publicKey = keyPair.getPublic();
                PrivateKey privateKey = keyPair.getPrivate();
                ColoresConsola.servidor("Clave pública generada: " + publicKey);
                ColoresConsola.servidor("Clave privada generada (solo servidor): " + privateKey);

                // Manejar al cliente
                handleClient(clientSocket, publicKey, privateKey);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket, PublicKey publicKey, PrivateKey privateKey) {
        try (
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            // Paso 0: Manejar solicitud del cliente
            Object request = in.readObject();
            if ("REQUEST_PUBLIC_KEY".equals(request)) {
                ColoresConsola.servidor(" Solicitud de clave pública recibida del cliente.");

                // Paso 1: Enviar clave pública al cliente
                ColoresConsola.servidor(" 2. Enviando clave pública al cliente...");
                out.writeObject(publicKey);
                out.flush();
            }

            // Paso 2: Recibir clave simétrica cifrada
            ColoresConsola.servidor(" 4. Esperando clave simétrica del cliente...");
            Packet packet = (Packet) in.readObject();

            byte[] decryptedData = RSA_Asimetric.decryptData(packet.message, privateKey);
            ColoresConsola.servidor(" Clave simétrica descifrada: " + new String(decryptedData));

            // Paso 3: Verificar hash
            sharedKey = new SecretKeySpec(decryptedData, "AES");
            if (Hash.compareHash(Hash.hash(sharedKey.getEncoded()), packet.hash)) {
                ColoresConsola.servidor(" Clave simétrica verificada correctamente");
            } else {
                ColoresConsola.servidor(" Error: El hash no coincide");
                return;
            }

            // Paso 4: Comunicación con el cliente
            ColoresConsola.servidor(" Listo para recibir mensajes del cliente...");
            while (true) {
                packet = (Packet) in.readObject();
                byte[] decryptedMessage = AES_Simetric.decryptData(sharedKey, packet.message);

                String message = new String(decryptedMessage);
                ColoresConsola.servidor(" 6. Palabra descifrada: " + message);

                byte[] serverHash = Hash.hash(decryptedMessage);
                ColoresConsola.servidor(" Hash calculado: \n" + Arrays.toString(serverHash));
                ColoresConsola.servidor(" Hash recibido: \n" + Arrays.toString(packet.hash));


                // Verificar integridad del mensaje
                if (Hash.compareHash(Hash.hash(decryptedMessage), packet.hash)) {
                    ColoresConsola.servidor(" Hash verificado correctamente");
                    // Enviar acuse de recibo
                    String response = "Mensaje recibido";
                    byte[] encryptedResponse = AES_Simetric.encryptData(sharedKey, response.getBytes());
                    out.writeObject(new Packet(encryptedResponse, response.getBytes()));
                    out.flush();
                } else {
                    ColoresConsola.servidor(" Error: El hash del mensaje no coincide");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
