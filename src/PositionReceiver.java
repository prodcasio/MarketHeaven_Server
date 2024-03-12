import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class PositionReceiver extends Thread{
    PositionReceiver(){}

    @Override
    public void run() {
        try {
            // Crea un DatagramSocket sulla porta 6677
            DatagramSocket socket = new DatagramSocket(6677);

            byte[] buffer = new byte[1024];

            System.out.println("PositionReceiver in attesa di ricevere dati...");

            while (true) {
                // Prepara il pacchetto per ricevere i dati
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Ricevi il pacchetto
                socket.receive(packet);

                // Estrai i dati dal pacchetto
                String receivedData = new String(packet.getData(), 0, packet.getLength());

                // Memorizza la richesta
                Positioner.addPosition("positions.xml", receivedData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
