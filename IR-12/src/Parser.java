import com.kursx.parser.fb2.Body;
import com.kursx.parser.fb2.Element;
import com.kursx.parser.fb2.FictionBook;
import com.kursx.parser.fb2.Section;

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

        } catch (Exception e) {
            System.out.println("File format not supported!");
            e.printStackTrace();
        }
        return finalText;
    }

    private String[] process(String text) {
        return text.replaceAll("(?:[^a-zA-Z ]|(?<=['\"])s)", "")
                .toLowerCase().trim().split("\\s+");
    }
}
