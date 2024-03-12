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

    public static void addPosition(String file, String jsonString) {
        JSONObject json = new JSONObject(jsonString);

        try {
            // Convert JSON to XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(file));

            // Create XML element for the position
            Element positionElement = doc.createElement("position");

            // Add asset, lots, and operation from JSON
            Element assetElement = doc.createElement("asset");
            assetElement.appendChild(doc.createTextNode(json.getString("asset")));
            positionElement.appendChild(assetElement);

            Element lotsElement = doc.createElement("lots");
            lotsElement.appendChild(doc.createTextNode(String.valueOf(json.getDouble("lots"))));
            positionElement.appendChild(lotsElement);

            Element operationElement = doc.createElement("operation");
            operationElement.appendChild(doc.createTextNode(json.getString("operation")));
            positionElement.appendChild(operationElement);

            // Get current date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = dateFormat.format(new Date());

            // Add date element
            Element dateElement = doc.createElement("date");
            dateElement.appendChild(doc.createTextNode(dateTime));
            positionElement.appendChild(dateElement);

            // Get opening price from assets.xml
            Document assetsDoc = builder.parse(new File("assets.xml"));
            Element assetsRoot = assetsDoc.getDocumentElement();
            String assetName = json.getString("asset");
            String openingPrice = getOpeningPrice(assetsRoot, assetName);

            // Add opening price element
            Element openingPriceElement = doc.createElement("price");
            openingPriceElement.appendChild(doc.createTextNode(openingPrice));
            positionElement.appendChild(openingPriceElement);

            // Append the position element to the root element
            doc.getDocumentElement().appendChild(positionElement);

            // Write the XML back to the file
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
