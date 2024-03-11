import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XMLtoJSONArray {
    public static JSONArray convert(File xmlFile) {
        try {
            // Carica il file XML
            File file = xmlFile;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            // Ottiene la lista degli elementi "asset"
            NodeList nodeList = doc.getElementsByTagName("asset");

            // Crea un JSONArray per gli asset
            JSONArray assetsArray = new JSONArray();

            // Itera sugli elementi "asset"
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);

                // Ottiene i valori degli attributi
                String name = element.getElementsByTagName("name").item(0).getTextContent();
                double value = Double.parseDouble(element.getElementsByTagName("value").item(0).getTextContent());
                double spread = Double.parseDouble(element.getElementsByTagName("spread").item(0).getTextContent());
                double change = Double.parseDouble(element.getElementsByTagName("change").item(0).getTextContent());

                // Crea un oggetto JSON per l'asset corrente
                JSONObject assetObject = new JSONObject();
                assetObject.put("name", name);
                assetObject.put("value", value);
                assetObject.put("spread", spread);
                assetObject.put("change", change);

                // Aggiunge l'asset al JSONArray
                assetsArray.put(assetObject);
            }

            return assetsArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
