import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.*;

public class ClientWorkerSender extends Thread{
    Socket socket;
    ClientWorkerSender(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run(){
        while (true) {
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                JSONArray assets = XMLtoJSONArray.convert(new File("assets.xml"));

                if(assets==null) continue;
                for (int i = 0; i < assets.length(); i++) {
                    writer.write(assets.getJSONObject(i).toString());
                    writer.newLine();
                    try{
                        writer.flush();
                    } catch (IOException e){
                        System.out.println("Il client si è disconnesso");
                        return;
                    }
                }

                Thread.sleep(100);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
