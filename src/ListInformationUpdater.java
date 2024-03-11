import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ListInformationUpdater extends Thread {
    @Override
    public void run() {
        try {
            // Carica il file XML contenente i dati originali
            File file = new File("assets.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            // Carica il file XML contenente i prezzi iniziali
            File initialValuesFile = new File("initialprices.xml");
            Document initialValuesDoc = dBuilder.parse(initialValuesFile);
            initialValuesDoc.getDocumentElement().normalize();

            // Ottiene la lista degli elementi "asset" dai due file XML
            NodeList assetsNodeList = doc.getElementsByTagName("asset");
            NodeList initialValuesNodeList = initialValuesDoc.getElementsByTagName("asset");

            // Ottiene i dati dei prezzi iniziali e li memorizza in una mappa
            // per consentire un rapido accesso in base al nome dell'asset
            Map<String, Double> initialPricesMap = new HashMap<>();
            for (int i = 0; i < initialValuesNodeList.getLength(); i++) {
                Element element = (Element) initialValuesNodeList.item(i);
                String name = element.getElementsByTagName("name").item(0).getTextContent();
                double value = Double.parseDouble(element.getElementsByTagName("value").item(0).getTextContent().replace(",", "."));
                initialPricesMap.put(name, value);
            }


            // Carica il file XML contenente i prezzi iniziali
            File currentValuesFile = new File("assets.xml");
            Document currentValuesDoc = dBuilder.parse(currentValuesFile);
            currentValuesDoc.getDocumentElement().normalize();
            NodeList currentValuesNodeList = currentValuesDoc.getElementsByTagName("asset");

            // Ottiene i dati dei prezzi iniziali e li memorizza in una mappa
            // per consentire un rapido accesso in base al nome dell'asset
            Map<String, Double> currentPricesMap = new HashMap<>();
            for (int i = 0; i < currentValuesNodeList.getLength(); i++) {
                Element element = (Element) currentValuesNodeList.item(i);
                String name = element.getElementsByTagName("name").item(0).getTextContent();
                double value = Double.parseDouble(element.getElementsByTagName("value").item(0).getTextContent().replace(",", "."));
                currentPricesMap.put(name, value);
            }


            // Modifica i dati degli asset nel file XML
            Random random = new Random();
            for (int i = 0; i < assetsNodeList.getLength(); i++) {
                Element element = (Element) assetsNodeList.item(i);
                String name = element.getElementsByTagName("name").item(0).getTextContent();
                double initialValue = initialPricesMap.get(name);
                double currentValue = currentPricesMap.get(name);

                // Modifica il valore
                double percentageChange = (((float)(random.nextInt(3)-1)* 0.0001));
                percentageChange = Double.parseDouble(String.format("%.6f", percentageChange).replace(",", "."));

                double newValue = currentValue * (1 + percentageChange);
                String newValueFormatted = String.format("%.6f", newValue).replace(",", ".");

                // Calcola la percentuale di variazione rispetto al prezzo iniziale
                double percentageVariation = ((newValue - initialValue) / initialValue) * 100;
                double percentageVariationRounded = Math.round(percentageVariation * 100.0) / 100.0;

                // Modifica lo spread
                int newSpread = random.nextInt(10); // da 0 a 9

                // Aggiorna i valori nei nodi XML
                element.getElementsByTagName("value").item(0).setTextContent(newValueFormatted);
                element.getElementsByTagName("spread").item(0).setTextContent(Integer.toString(newSpread));
                element.getElementsByTagName("change").item(0).setTextContent(Double.toString(percentageVariationRounded));
            }

            // Salva le modifiche nel file XML sovrascrivendo il file originale
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
