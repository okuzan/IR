package parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Body {
    public ArrayList<Section> getSections() {
        return sections;
    }

    private ArrayList<Section> sections;

    public Body(File f) {
        sections = new ArrayList<>();
        createBody(f);
    }

    private void createBody(File f) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(f);
            doc.getDocumentElement().normalize();
           // System.out.println(doc.getDocumentElement().getNodeName());
            parseSections(doc);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

    }


    private void parseSections(Document doc) {
        NodeList titleNodes = doc.getElementsByTagName("body");
        Element titleInfoElement = (Element) titleNodes.item(0);
        NodeList nodes = titleInfoElement.getElementsByTagName("section");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Section sec = new Section();
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                NodeList elements = element.getElementsByTagName("p");
                for (int j = 0; j < elements.getLength(); j++){
                    String p = elements.item(j).getTextContent();
                //    System.out.println(p);
                    sec.setText(sec.getText()+p);
                }
            }
        }
    }
}
