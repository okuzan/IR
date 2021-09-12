import com.kursx.parser.fb2.*;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;

public class Parser {
    private File file;
    private String[] lexems;
    private ArrayList<String> metadata;

    public Parser(File file) {
        this.file = file;
        lexems = process(parse());
        metadata = new ArrayList<>();
    }

    public String[] getLexems() {
        return lexems;
    }

    private String parse() {
        String finalText = "";
        try {
            if (file.getName().contains(".epub")) {

                EpubReader epubReader = new EpubReader();
                Book book = epubReader.readEpub(new FileInputStream(file));
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < book.getSpine().size(); i++) {
                    InputStream is = book.getSpine().getSpineReferences().get(i).getResource().getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append(" ");
                    }
                }

                Document doc = Jsoup.parse(sb.toString());
                finalText = doc.body().text();
            }

            if (file.getName().contains(".fb2")) {
                StringBuilder sb = new StringBuilder();
                FictionBook fb2 = new FictionBook(file);
                Body body = fb2.getBody();
                ArrayList<Section> sections = body.getSections();
                for (Section section : sections)
                    for (Element element : section.getElements())
                        sb.append(element.getText()).append("\n");

                finalText = String.valueOf(sb);
            }

            if (file.getName().contains(".txt")) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String st;
                StringBuilder sb = new StringBuilder();
                while ((st = br.readLine()) != null)
                    sb.append(st).append(" ");
                finalText = String.valueOf(sb);

            }
        } catch (Exception e) {
            System.out.println("File format now supported!");
            e.printStackTrace();
        }
        return finalText;
    }

    private String authorsStr(ArrayList<Person> ids) {
        StringBuilder res = new StringBuilder();
        for (Person i : ids) res.append(i.getFullName()).append(" ");
        return String.valueOf(res);
    }

    private String annotStr(ArrayList<Element> ids) {
        StringBuilder res = new StringBuilder();
        for (Element i : ids) res.append(i.getText()).append(" ");
        return String.valueOf(res);
    }


    private String[] process(String text) {
        text = text.replaceAll("[,^*&+<>©/\"«’»\\\\@—•„“'”.!?; ’_:=�()\\[\\]{}\\s]", " ");
//        text = text.replaceAll("[^a-zA-Z0-9]", "");
        text = text.toLowerCase();
        return text.trim().split("\\s+");
    }

    public ArrayList<String> getMetadata() throws IOException, SAXException, ParserConfigurationException {
        FictionBook fb2 = new FictionBook(file);
        metadata.add(fb2.getTitle().toLowerCase());
        metadata.add(authorsStr(fb2.getAuthors()));
        if (fb2.getAnnotation() != null)
            metadata.add(annotStr(fb2.getAnnotation().getAnnotations()));
        else {
            metadata.add("");
        }
        metadata.add(fb2.getDescription().getDocumentInfo().getDate().getValue());
        return metadata;
    }
}
