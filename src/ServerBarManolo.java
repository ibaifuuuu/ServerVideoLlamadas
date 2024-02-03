
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class ServerBarManolo {
	
	//---------------------------------- PARTE SERVER VIDEOLLAMADAS---------------------------------------------------
	
    private static SSLServerSocketFactory serverFactoryVideollamada;
    private static SSLServerSocket servidorSSLVideoLlamada;
    private static int puertoServidorVideollamadas = 5002;
    private static boolean encendido = true;
    private static List<String> IPsconectadasVideollamada = new ArrayList<>();
    public static Queue<SSLSocket> colaEsperaVideollamada = new LinkedList<>();
    private static final int MAX_CONEXIONES_VIDEOLLAMADA = 6;
    public static List<SSLSocket> clientesConectadosVideollamada = new ArrayList<>();

    //----------------------------------------------------------------------------------------------------------------
    
	//---------------------------------- PARTE SERVER MENSAJES--------------------------------------------------------
    
    private static SSLServerSocket servidorSSLMensaje;
    private static SSLServerSocketFactory serverFactoryMensaje;
    private static int puertoServidorMensajes = 5003;

    
	//----------------------------------------------------------------------------------------------------------------


    public static void main(String[] args) {

        try {
        	
        	System.setProperty("javax.net.ssl.keyStore", "AlmacenSSL");
        	System.setProperty("javax.net.ssl.keyStorePassword", "1234567");
           
            Thread videollamadaThread = new Thread(() -> iniciarServidorVideollamada());
            videollamadaThread.start();
                
            Thread mensajesThread = new Thread(() -> iniciarServidorMensajes());
            mensajesThread.start();
        

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void iniciarServidorVideollamada() {
        try {


            serverFactoryVideollamada = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            servidorSSLVideoLlamada = (SSLServerSocket) serverFactoryVideollamada.createServerSocket(puertoServidorVideollamadas);

            while (encendido) {
                SSLSocket clienteSocket = (SSLSocket) servidorSSLVideoLlamada.accept();
                System.out.println("cliente en cola a videollamada");
                if (IPsconectadasVideollamada.size() < MAX_CONEXIONES_VIDEOLLAMADA) {
                    IPsconectadasVideollamada.add(clienteSocket.getInetAddress().toString());
                    clientesConectadosVideollamada.add(clienteSocket);
                    Thread clientHandlerThread = new Thread(new HiloVideollamada(clienteSocket));
                    clientHandlerThread.start();
                    System.out.println("cliente en videollamada");

                } else {
                    System.out.println("Límite de personas conectadas, " + colaEsperaVideollamada.size() + "personas en espera");
                    colaEsperaVideollamada.add(clienteSocket);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void iniciarServidorMensajes() {
        try {
          
            serverFactoryMensaje = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            servidorSSLMensaje = (SSLServerSocket) serverFactoryMensaje.createServerSocket(puertoServidorMensajes);

            while (encendido) {
                SSLSocket clienteSocket = (SSLSocket) servidorSSLMensaje.accept();
                System.out.println("cliente en cola a mensaje");
                Thread clientHandlerThread = new Thread(new HiloVideollamada(clienteSocket));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
