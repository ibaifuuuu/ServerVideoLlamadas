import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.net.ssl.SSLSocket;


class ClientVideoCallThread implements Runnable {
    private SSLSocket clientSocket;

    public ClientVideoCallThread(SSLSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            // Implementar lógica de transmisión de video y audio

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                
                //si hay alguno en cola de espera le hacemos iniciar y lo eliminamos de la cola de espera
                if (!VideoCallServer.colaEspera.isEmpty()) {
                    SSLSocket siguienteCliente = VideoCallServer.colaEspera.poll();
                    Thread clientHandlerThread = new Thread(new ClientVideoCallThread(siguienteCliente));
                    VideoCallServer.colaEspera.remove(siguienteCliente);
                    clientHandlerThread.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
