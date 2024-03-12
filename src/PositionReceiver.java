import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.net.*;

public class PositionReceiver extends Thread{
    PositionReceiver(){}

    @Override
    public void run(){
        try {
            JSONArray positions = XMLtoJSONArray.convert(new File("positions.xml")); // Carica gli asset da un file XML
            DatagramSocket socket = new DatagramSocket(6677); // Crea un socket Datagram in ascolto sulla porta specificata
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket); // Riceve il pacchetto dal client



            socket.close(); // Chiude il socket quando tutte le richieste sono state inviate
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
