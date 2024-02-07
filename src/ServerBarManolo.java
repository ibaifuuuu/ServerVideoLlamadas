
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocketFactory;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

	private static SSLSocket clienteSocketActual;
	private static InputStream inputStream;
	private static List<ObjectOutputStream> output = new ArrayList<ObjectOutputStream>();
    
	//----------------------------------------------------------------------------------------------------------------


    public static void main(String[] args) {

        try {
        	
        	System.setProperty("javax.net.ssl.keyStore", "AlmacenSSL");
        	System.setProperty("javax.net.ssl.keyStorePassword", "1234567");
                
            Thread mensajesThread = new Thread(() -> iniciarServidorMensajes());
            mensajesThread.start();
        

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void iniciarServidorMensajes() {
    	
        try {
          
            serverFactoryMensaje = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            servidorSSLMensaje = (SSLServerSocket) serverFactoryMensaje.createServerSocket(puertoServidorMensajes);

            while (encendido) {
            	
                clienteSocketActual = (SSLSocket) servidorSSLMensaje.accept();
        		
        		try {
        			
        			ObjectInputStream objectInputStream = new ObjectInputStream(clienteSocketActual.getInputStream());
        			output.add(new ObjectOutputStream(clienteSocketActual.getOutputStream()));
        			HashMap<String, String> receivedHashMap = (HashMap<String, String>) objectInputStream.readObject();
        			String url = null;
        			String json = null;
        			
        			for (Map.Entry<String, String> entry : receivedHashMap.entrySet()) {
        				
        				String key = entry.getKey();
        				String value = entry.getValue();
        				
        				if ("url".equals(key)) {
        					url = value;
        				} 
        				else if ("json".equals(key)) {
        					
        					json = value;
        					
        				}
        			}

        			System.out.println("El servidor escucha el mensaje del cliente: " + json);

        			ConectorAPIBBDD.postAPI(url, json);
        			
        			for (ObjectOutputStream ou : output) {
        				ou.writeObject("recargar");
        			}
                    
        			System.out.println("El mensaje es enviado a la bbdd");

        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        		
        	}
               

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
