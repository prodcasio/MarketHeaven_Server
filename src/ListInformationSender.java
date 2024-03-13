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
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
public class ListInformationSender extends Thread {
    @Override
    public void run() {
        try {
            Map<String, String> initialPrices = readInitialPrices("initialprices.xml");
            updatePrices("assets.xml", initialPrices);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        try {
            ListInformationUpdater liu = new ListInformationUpdater();
            liu.start();
            ServerSocket serverSocket = new ServerSocket(5566);
            // Creazione della connessione TCP
            while (true){
                Socket socket = serverSocket.accept();
                ClientWorkerSender cws = new ClientWorkerSender(socket);
                cws.start();
                System.out.println("socket accettato");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, String> readInitialPrices(String filename) throws Exception {
        Map<String, String> prices = new HashMap<>();

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

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filename));
        transformer.transform(source, result);
    }
}