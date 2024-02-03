import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.net.ssl.SSLSocket;

class HiloMensajes implements Runnable {
    private SSLSocket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public HiloMensajes(SSLSocket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.inputStream = clientSocket.getInputStream();
            this.outputStream = clientSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
    	try {
    	        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

    	        // Assuming you are receiving a HashMap<String, String>, adjust accordingly
    	        HashMap<String, String> receivedHashMap = (HashMap<String, String>) objectInputStream.readObject();

    	        // Now you have the received HashMap, you can use it as needed
    	        // For example, print the received data
    	        System.out.println("Received HashMap: " + receivedHashMap);

    	        // Iterate through the HashMap and send each key-value pair to ConectorAPIBBDD.postAPI
    	        for (Map.Entry<String, String> entry : receivedHashMap.entrySet()) {
    	            String key = entry.getKey();
    	            String value = entry.getValue();

    	            // Send key and value to ConectorAPIBBDD.postAPI
    	            ConectorAPIBBDD.postAPI(key, value);
    	        }
    	        // Close the input stream and socket when done
    	        objectInputStream.close();
    	        clientSocket.close();
    	     }catch (Exception e) {
				e.printStackTrace();
			}
    }
}

