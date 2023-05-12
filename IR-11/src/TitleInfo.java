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

public class TitleInfo {
    private ArrayList<String> genres;
    private ArrayList<Author> authors;

    public ArrayList<String> getGenres() {
        return genres;
    }

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getDate() {
        return date;
    }

    public String getLang() {
        return lang;
    }

    private String bookTitle;
    private String date;
    private String lang;

    public TitleInfo(File f) {
        genres = new ArrayList<>();
        authors = new ArrayList<>();
        createTitleInfo(f);
    }

    private void createTitleInfo(File f) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(f);
            doc.getDocumentElement().normalize();
            // System.out.println(doc.getDocumentElement().getNodeName());
            parseGenres(doc);
            parseAuthors(doc);
            parseBookTitle(doc);
            parseDate(doc);
            parseLanguage(doc);
            //TODO annotation
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

    private void parseLanguage(Document doc) {
        NodeList nodes = doc.getElementsByTagName("title-info");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                // System.out.println("Language: " + getValue("lang", element));
                lang = getValue("lang", element);
            }
        }
    }

    private void parseDate(Document doc) {
        NodeList nodes = doc.getElementsByTagName("title-info");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                //  System.out.println("Date: " + getValue("date", element));
                date = getValue("date", element);
            }
        }
    }

    private void parseBookTitle(Document doc) {
        NodeList nodes = doc.getElementsByTagName("title-info");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                //  System.out.println("Book title: " + getValue("book-title", element));
                bookTitle = getValue("book-title", element);
            }
        }
    }

    private void parseAuthors(Document doc) {
        NodeList titleNodes = doc.getElementsByTagName("title-info");
        Element titleInfoElement = (Element) titleNodes.item(0);
        NodeList nodes = titleInfoElement.getElementsByTagName("author");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                //  System.out.println("Author name: " + getValue("first-name", element));
                // System.out.println("Author surname: " + getValue("last-name", element));
                Author author = new Author();
                author.setName(getValue("first-name", element));
                author.setSurname(getValue("last-name", element));
                authors.add(author);
            }
        }
    }

    private void parseGenres(Document doc) {
        NodeList nodes = doc.getElementsByTagName("title-info");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                //  System.out.println("Genre: " + getValue("genre", element));
                genres.add(getValue("genre", element));
            }
        }
    }

    private String getValue(String tag, Element element) {
        if (element.getElementsByTagName(tag).item(0) != null) {
            NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodes.item(0);
            if (node == null) return "";
            return node.getNodeValue();
        }
        return "";
    }
}
