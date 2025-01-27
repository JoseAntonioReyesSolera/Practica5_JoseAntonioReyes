package practica;

import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Hash {
    // Generar clave AES a partir de texto y tamaño de clave
    public static SecretKey passwordKeyGeneration(String text, int keySize) {
        SecretKey sKey = null;
        if ((keySize == 128) || (keySize == 192) || (keySize == 256)) {
            try {
                byte[] data = text.getBytes("UTF-8");
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(data);
                byte[] key = Arrays.copyOf(hash, keySize / 8);
                sKey = new SecretKeySpec(key, "AES");
            } catch (Exception ex) {
                System.err.println("Error generando la clave: " + ex);
            }
        }
        return sKey;
    }

    // Generar hash (SHA-256) a partir de datos
    public static byte[] hash(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(data);
        } catch (Exception ex) {
            ColoresConsola.error("Error generando el hash: " + ex);
            return null;
        }
    }

    // Comparar dos hashes (valida si son iguales)
    public static boolean compareHash(byte[] hash1, byte[] hash2) {
        if (Arrays.equals(hash1, hash2)) {
            ColoresConsola.advertencia("CORRECT hash: El mensaje no ha sido modificado");
            return true;
        } else {
            ColoresConsola.error("FALSE hash: El mensaje ha sido modificado");
            return false;
        }
    }
}
