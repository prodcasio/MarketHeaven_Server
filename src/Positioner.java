import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Positioner {

    // Aggiunge una posizione al file XML con i dati forniti in formato JSON
    public static void addPosition(String file, String jsonString) {
        JSONObject json = new JSONObject(jsonString);

        try {
            // Converti JSON in XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(file));

            // Crea l'elemento XML per la posizione
            Element positionElement = doc.createElement("position");

            // Aggiungi asset, lotti e operazione dal JSON
            Element assetElement = doc.createElement("asset");
            assetElement.appendChild(doc.createTextNode(json.getString("asset")));
            positionElement.appendChild(assetElement);

            Element lotsElement = doc.createElement("lots");
            lotsElement.appendChild(doc.createTextNode(String.valueOf(json.getDouble("lots"))));
            positionElement.appendChild(lotsElement);

            Element operationElement = doc.createElement("operation");
            operationElement.appendChild(doc.createTextNode(json.getString("operation")));
            positionElement.appendChild(operationElement);

            // Ottieni data e ora correnti
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = dateFormat.format(new Date());

            // Aggiungi elemento data
            Element dateElement = doc.createElement("date");
            dateElement.appendChild(doc.createTextNode(dateTime));
            positionElement.appendChild(dateElement);

            // Ottieni il prezzo di apertura da assets.xml
            Document assetsDoc = builder.parse(new File("assets.xml"));
            Element assetsRoot = assetsDoc.getDocumentElement();
            String assetName = json.getString("asset");
            String openingPrice = getOpeningPrice(assetsRoot, assetName);

            // Aggiungi elemento prezzo di apertura
            Element openingPriceElement = doc.createElement("price");
            openingPriceElement.appendChild(doc.createTextNode(openingPrice));
            positionElement.appendChild(openingPriceElement);

            // Aggiungi l'elemento della posizione all'elemento radice
            doc.getDocumentElement().appendChild(positionElement);

            // Scrivi l'XML nel file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            FileWriter writer = new FileWriter("positions.xml");
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Ottiene il prezzo di apertura per un determinato asset dal documento XML assets.xml
    private static String getOpeningPrice(Element assetsRoot, String assetName) {
        NodeList assetNodes = assetsRoot.getElementsByTagName("asset");
        for (int i = 0; i < assetNodes.getLength(); i++) {
            Element assetElement = (Element) assetNodes.item(i);
            String name = assetElement.getElementsByTagName("name").item(0).getTextContent();
            if (name.equals(assetName)) {
                if (assetElement.getElementsByTagName("value").getLength() > 0) {
                    return assetElement.getElementsByTagName("value").item(0).getTextContent();
                }
            }
        }
        return null;
    }
}
