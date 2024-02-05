import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.net.ssl.SSLSocket;

class HiloMensajes implements Runnable {
    private SSLSocket clienteSocketActual;
    private InputStream inputStream;
    private OutputStream outputStream;
    
    private static List<SSLSocket> clientesConectados = new ArrayList<>();
    private static List<ObjectOutputStream> clientesEsperandoMensaje = new ArrayList<>();
    
    
    public HiloMensajes(SSLSocket clienteSocket) {
    	
        try {
        	
        	boolean encontrado = false;
        	if(clientesConectados.size()>0) {
        		for (SSLSocket sslSocket : clientesConectados) {
            		
            		//Si la IP es la misma y ya ha enviado un mensaje antes, utilizamos el socket ya abierto
    				if(sslSocket.getInetAddress().equals(clienteSocket.getInetAddress())) {
    					this.clienteSocketActual= sslSocket;
    			        System.out.print("repe");

    				}
    				else {
    			        clientesConectados.add(clienteSocket);
    				}
    			}
        	}
        	else {
    	        this.clienteSocketActual = clienteSocket;

        	}
        	
            this.inputStream = clienteSocket.getInputStream();
            this.outputStream = clienteSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            HashMap<String, String> receivedHashMap = (HashMap<String, String>) objectInputStream.readObject();

            String url = null;
            String json = null;
            for (Map.Entry<String, String> entry : receivedHashMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if ("url".equals(key)) {
                    url = value;
                } else if ("json".equals(key)) {
                    json = value;
                }
            }
            System.out.println("El servidor escucha el mensaje del cliente: " + json);

            if ("cerrar".equals(json)) {
                System.out.println("El cliente pide cerrar el socket");
                // Realiza acciones necesarias para cerrar el socket si es necesario
                
            }

            ConectorAPIBBDD.postAPI(url, json);
            System.out.println("El mensaje es enviado a la bbdd");

            if (!clienteSocketActual.isClosed() && outputStream != null) { // Verificar si la conexión está cerrada y outputStream no es nulo
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                clientesEsperandoMensaje.add(objectOutputStream);
                String mensajeRecarga = "Recargar";
                if (clientesEsperandoMensaje.size() > 0) {
                    for (ObjectOutputStream cliente : clientesEsperandoMensaje) {
                        if (!clienteSocketActual.isClosed()) { // Verificar si la conexión está cerrada
                            try {
                                cliente.writeObject(mensajeRecarga);
                                System.out.println("Mensaje de recarga enviado");
                                cliente.flush();
                            } catch (IOException ioException) {
                                // Manejar la excepción según sea necesario
                                ioException.printStackTrace();
                            }
                        }
                    }
                }
            
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

