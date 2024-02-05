import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javax.net.ssl.SSLSocket;

class HiloVideollamada implements Runnable {
    private SSLSocket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public HiloVideollamada(SSLSocket clientSocket) {
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
            // Recibir bytes continuamente desde el cliente y enviar a otros clientes
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // Enviar los bytes a todos los clientes conectados
                broadcastBytes(buffer, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();

                // Si hay alguien en la cola de espera, lo iniciamos y lo eliminamos de la cola
                if (!ServerBarManolo.colaEsperaVideollamada.isEmpty()) {
                    SSLSocket siguienteCliente = ServerBarManolo.colaEsperaVideollamada.poll();
                    Thread clientHandlerThread = new Thread(new HiloVideollamada(siguienteCliente));
                    ServerBarManolo.colaEsperaVideollamada.remove(siguienteCliente);
                    clientHandlerThread.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastBytes(byte[] buffer, int bytesRead) {
        // Enviar bytes a todos los clientes conectados, excepto al remitente
        List<SSLSocket> clientesConectados = new ArrayList<>(ServerBarManolo.clientesConectadosVideollamada );
        clientesConectados.remove(clientSocket);

        for (SSLSocket cliente : clientesConectados) {
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(cliente.getOutputStream());
                dataOutputStream.write(buffer, 0, bytesRead);
                dataOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

