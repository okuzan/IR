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


public class PublishInfo {

    private String bookName;
    private String publisher;
    private String city;
    private String year;

    public String getBookName() {
        return bookName;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getCity() {
        return city;
    }

    public String getYear() {
        return year;
    }

    public PublishInfo(File f){
        createPublishInfo(f);
    }

    private void createPublishInfo(File f) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(f);
            doc.getDocumentElement().normalize();
           // System.out.println(doc.getDocumentElement().getNodeName());
            parseBookName(doc);
            parsePublisher(doc);
            parseCity(doc);
            parseYear(doc);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

    private void parseYear(Document doc) {
        NodeList nodes = doc.getElementsByTagName("publish-info");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
               // System.out.println("Year: " + getValue("year", element));
                year = getValue("year", element);
            }
        }
    }

    private void parseCity(Document doc) {
        NodeList nodes = doc.getElementsByTagName("publish-info");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
               // System.out.println("City: " + getValue("city", element));
                city = getValue("city", element);
            }
        }
    }

    private void parsePublisher(Document doc) {
        NodeList nodes = doc.getElementsByTagName("publish-info");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
              //  System.out.println("Publisher: " + getValue("publisher", element));
                publisher = getValue("publisher", element);
            }
        }
    }

    private void parseBookName(Document doc) {
        NodeList nodes = doc.getElementsByTagName("publish-info");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
               // System.out.println("Book name: " + getValue("book-name", element));
                bookName = getValue("book-name", element);
            }
        }
    }

    private String getValue(String tag, Element element) {
        try {
            NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = (Node) nodes.item(0);
            if (node == null)
                return "";
            return node.getNodeValue();
        }catch (NullPointerException npe){
            return "";
        }
    }
}
