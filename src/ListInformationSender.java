import org.json.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Random;
import java.util.Map;

public class ListInformationSender extends Thread{
    @Override
    public void run(){
        Random r = new Random();

        try {
            // Leggi i valori da initialPrices.xml
            Map<String, String> initialPrices = readInitialPrices("initialprices.xml");

            // Aggiorna i valori in prices.xml
            updatePrices("assets.xml", initialPrices);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        while(true) {
            ListInformationUpdater liu = new ListInformationUpdater();
            liu.start();
            try {
                liu.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                JSONArray assets = XMLtoJSONArray.convert(new File("assets.xml")); // Carica gli asset da un file XML
                DatagramSocket socket = new DatagramSocket(); // Crea un socket Datagram

                for (int i = 0; i < assets.length(); i++) {
                    byte[] sendData = assets.getJSONObject(i).toString().getBytes(); // Converte l'elemento dell'array in un array di byte
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("127.0.0.1"), 5566); // Crea il pacchetto da inviare
                    socket.send(sendPacket); // Invia il pacchetto al server
                }

                socket.close(); // Chiude il socket quando tutte le richieste sono state inviate
                Thread.sleep(100);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static Map<String, String> readInitialPrices(String filename) throws Exception {
        // Crea una mappa per rendere più facile trovare le informazioni corrispondenti
        Map<String, String> prices = new HashMap<>();

        // Prende il file dei prezzi iniziali
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File(filename));
        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("asset");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String name = element.getElementsByTagName("name").item(0).getTextContent();
                String value = element.getElementsByTagName("value").item(0).getTextContent();
                prices.put(name, value);
            }
        }

        return prices;
    }
    private static void updatePrices(String filename, Map<String, String> initialPrices) throws Exception {
        // Crea il file su cui scaricare le informazioni appena generate
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File(filename));
        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("asset");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String name = element.getElementsByTagName("name").item(0).getTextContent();
                String value = initialPrices.get(name);
                if (value != null) {
                    element.getElementsByTagName("value").item(0).setTextContent(value);
                }
            }
        }

        // Salva le modifiche nel file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filename));
        transformer.transform(source, result);
    }
}
