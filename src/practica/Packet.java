package practica;
import java.io.Serializable;
public class Packet implements Serializable{
    private static final long serialVersionUID = 8799656478674716638L;
    byte[] message;
    byte[] hash;
    public Packet(byte[] m, byte[] k){
        message = m;
        hash = k;
    }
}
