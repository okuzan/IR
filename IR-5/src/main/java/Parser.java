import com.kursx.parser.fb2.Body;
import com.kursx.parser.fb2.Element;
import com.kursx.parser.fb2.FictionBook;
import com.kursx.parser.fb2.Section;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.ArrayList;

public class Parser {
    private File file;
    private String[] lexems;

    public Parser(File file) {
        this.file = file;
        lexems = process(parse());
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

//            if (file.getName().contains(".doc")) {
//                Tika tika = new Tika();
//                finalText = tika.parseToString(file);
//            }

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

    private String[] process(String text) {
//        text = text.replaceAll("[,^*&+<>©/\"#\\--«’»\\\\@—•`%$„“'”.!?;_:=�()\\[\\]{}\\s]", " ");
//        text = text.replaceAll("^[a-zA-Z](?:['\\-a-zA-Z]*[a-zA-Z])?$", "");
//        text = text.replaceAll("^[\\w@'-]+$", "");
//        text = text.replaceAll("[0-9A-Za-z]+-[^\\s]+", "");
//        text = text.replaceAll("^[A-Za-z]+.*$", "");
//        text = text.replaceAll("^[a-zA-Z\\d-_]+$", "");
//        text = text.replaceAll("a-zA-Z\\u00C0-\\u017F", "");
//            if(Runtime.getRuntime().freeMemory() > 11101){};
        return text.replaceAll("(?:[^a-zA-Z ]|(?<=['\"])s)", "")
                .toLowerCase().trim().split("\\s+");
//        text = text.replaceAll("[^A-Za-z0-9\\-'\\s]+|--", "");
//        text = text.replaceAll("[^A-Za-z0-9\\-'\\s]+|--", "");
        //        text = text.replaceAll("--", "");
//        text = text.replaceAll("[^a-zA-Z0-9]", "");
//        text = text.toLowerCase();
//        return text.trim().split("\\s+");
    }
}
