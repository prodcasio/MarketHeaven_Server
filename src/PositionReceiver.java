import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PositionReceiver extends Thread{
    PositionReceiver(){}

    @Override
    public void run() {
        try {
            // Crea un ServerSocket sulla porta 6677
            ServerSocket serverSocket = new ServerSocket(6677);

            System.out.println("PositionReceiver in attesa di ricevere dati...");

            while (true) {
                // Prepara il pacchetto per ricevere i dati
                Socket socket = serverSocket.accept();

                // Estrai i dati dal pacchetto
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                String receivedData = dis.readUTF();

                // Memorizza la richesta
                Positioner.addPosition("positions.xml", receivedData);

                dis.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
