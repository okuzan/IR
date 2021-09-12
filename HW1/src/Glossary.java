import com.kursx.parser.fb2.Body;
import com.kursx.parser.fb2.FictionBook;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Glossary {
    private Term[] terms = new Term[3000000];

    public Glossary() throws Exception {
        input();
    }

    private void input() throws Exception {
        File[] files = new File("./data").listFiles();
        System.out.println(files.length);
        for (File file : files) {
            parse(file);
            break;
        }
    }

    private void parse(File file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder text = new StringBuilder();
        String line;
        int o = 0;

        while ((line = br.readLine()) != null) {
            text.append(line);
        }

        String[] words = String.valueOf(text).trim().split("[A-Za-z]+");
        Html.

        for (String word : words) {
//            if(word.)
            System.out.println(word);
        }

    }

    private void writeData() {

    }

    public static void main(String[] args) throws Exception {
        Glossary glossary = new Glossary();
    }
}
