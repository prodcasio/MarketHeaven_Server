import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;

public class PositionsSender extends Thread {

    @Override
    public void run() {
        try {
            // Crea un ServerSocket sulla porta 7788
            ServerSocket serverSocket = new ServerSocket(7788);

            System.out.println("Server in attesa di richieste...");

            while (true) {
                Socket socket = serverSocket.accept();

                // Carica i dati XML dal file positions.xml
                String xmlData = loadXMLFromFile("positions.xml");

                // Carica i dati XML dal file assets.xml
                String assetsXmlData = loadXMLFromFile("assets.xml");

                // Converte i dati XML in JSON
                String jsonData = convertXMLtoJSON(xmlData, assetsXmlData);

                // Invia i dati JSON al client
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(jsonData);
                dos.flush();
                dos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String loadXMLFromFile(String fileName) throws Exception {
        StringBuilder xmlData = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = reader.readLine()) != null) {
            xmlData.append(line);
        }
        reader.close();
        return xmlData.toString();
    }

    private static String convertXMLtoJSON(String positionsXmlData, String assetsXmlData) {
        JSONObject positionsJson = XML.toJSONObject(positionsXmlData);
        JSONObject assetsJson = XML.toJSONObject(assetsXmlData);

        JSONObject positions = positionsJson.getJSONObject("positions");
        if (positions != null) {
            JSONObject position = positions.optJSONObject("position");
            if (position != null) {
                // Estrarre i dati necessari dal JSON positions
                String assetName = position.getString("asset");
                double lots = position.getDouble("lots");
                String operation = position.getString("operation");
                double openPrice = position.getDouble("price");

                // Trova il valore di chiusura dell'asset
                double closePrice = findClosePrice(assetsJson, assetName);

                // Calcola il guadagno per la posizione e aggiungilo all'oggetto JSON
                double pnl = calculatePnL(operation, openPrice, closePrice, lots);
                String formattedPnl = String.valueOf(Math.floor(pnl * 100) / 100);
                position.put("pnl", formattedPnl);
            }
        }
        return positionsJson.toString();
    }

    private static double findClosePrice(JSONObject assetsJson, String assetName) {
        // Trova il valore di chiusura dell'asset dal JSON assets
        JSONObject assets = assetsJson.getJSONObject("assets");
        if (assets != null) {
            JSONArray assetArray = assets.getJSONArray("asset");
            for (int i = 0; i < assetArray.length(); i++) {
                JSONObject asset = assetArray.getJSONObject(i);
                if (asset.getString("name").equalsIgnoreCase(assetName)) {
                    return asset.getDouble("value");
                }
            }
        }
        return 0.0; // Ritorna 0 se non trova l'asset o il suo valore di chiusura
    }


    private static double calculatePnL(String operation, double openPrice, double closePrice, double lots) {
        // Calcola il guadagno in base all'operazione, al prezzo di apertura, al prezzo di chiusura e ai lotti della posizione
        if (operation.equalsIgnoreCase("buy")) {
            return (closePrice - openPrice) * lots;
        } else if (operation.equalsIgnoreCase("sell")) {
            return (openPrice - closePrice) * lots;
        }
        return 0.0; // Se l'operazione non è né buy né sell, ritorna 0
    }
}
