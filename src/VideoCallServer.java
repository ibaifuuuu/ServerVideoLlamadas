
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class VideoCallServer {

    private static SSLServerSocketFactory sfact;
    private static SSLServerSocket servidorSSL;
    private static boolean encendido = true;
    private static List<String> IPsconectadas = new ArrayList<>();
    public static Queue<SSLSocket> colaEspera = new LinkedList<>();
    private static final int MAX_CONEXIONES = 6;
    private static int puertoServidor = 5000;

    public static void main(String[] args) {
        try {
            sfact = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            servidorSSL = (SSLServerSocket) sfact.createServerSocket(puertoServidor);

            while (encendido) {
                SSLSocket clienteSocket = (SSLSocket) servidorSSL.accept();
                if (IPsconectadas.size() < MAX_CONEXIONES) {
                    IPsconectadas.add(clienteSocket.getInetAddress().toString());
                    Thread clientHandlerThread = new Thread(new ClientVideoCallThread(clienteSocket));
                    clientHandlerThread.start();
                } else {
                    System.out.println("Límite de personas conectadas, encolando al cliente");
                    colaEspera.add(clienteSocket);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
