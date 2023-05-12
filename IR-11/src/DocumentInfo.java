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

public class DocumentInfo {
    private ArrayList<Author> authors;
    private String date;
    private String id;

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public DocumentInfo (File f){
        authors = new ArrayList<>();
        createDocumentInfo(f);
    }

    private void createDocumentInfo(File f) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(f);
            doc.getDocumentElement().normalize();
          //  System.out.println(doc.getDocumentElement().getNodeName());
            parseAuthors(doc);
            parseDate(doc);
            parseId(doc);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

    private void parseId(Document doc) {
        NodeList nodes = doc.getElementsByTagName("document-info");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
              //  System.out.println("ID: " + getValue("id", element));
                id = getValue("id",element);
            }
        }
    }

    private void parseDate(Document doc) {
        NodeList nodes = doc.getElementsByTagName("document-info");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
               // System.out.println("Date: " + getValue("date", element));
                date = getValue("date",element);
            }
        }
    }

    private void parseAuthors(Document doc) {
        NodeList titleNodes = doc.getElementsByTagName("document-info");
        Element titleInfoElement = (Element) titleNodes.item(0);
        NodeList nodes = titleInfoElement.getElementsByTagName("author");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
              //  System.out.println("Author name: " + getValue("first-name", element));
              //  System.out.println("Author surname: " + getValue("last-name", element));
                Author author = new Author();
                author.setName(getValue("first-name", element));
                author.setSurname(getValue("last-name", element));
                authors.add(author);
            }
        }
    }

    private String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        if (node == null)
            return "";
        return node.getNodeValue();
    }
}
