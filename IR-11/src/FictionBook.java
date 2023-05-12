import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FictionBook {

    private File book;
    private double xmlVersion;
    private String encoding;
    private Description description;
    private Body body;

    public FictionBook (String path){
        book = new File(path);
        parseBook();
    }

    public FictionBook (File f){
        this.book = f;
        parseBook();
    }

    private void parseBook(){
        parseVersionAndEncoding(book);
        createDescription(book);
        createBody(book);
        createBinaries(book);
    }

    private void parseVersionAndEncoding(File book){
        try {
            FileInputStream fis = new FileInputStream(book);
            Scanner sc = new Scanner(fis);
            String line = "";
            while (!line.startsWith("<?xml")){
                line = sc.nextLine();
            }
            String[] elements = line.split(" ");
            for (int i = 0; i < elements.length; i++){
                if (elements[i].startsWith("version")){
                    xmlVersion = Double.parseDouble(elements[i].substring(elements[i].indexOf('"')+1,elements[i].length()-1));
                }
                if (elements[i].startsWith("encoding")){
                    encoding = elements[i].substring(elements[i].indexOf('"')+1,elements[i].lastIndexOf('"'));
                }
            }
        }catch (FileNotFoundException fnfe){
            System.out.println("No such file");
        }
    }

    private void createDescription(File f) {
        description = new Description(f);
    }

    private void createBody(File f) {
        body = new Body(f);
    }

    private void createBinaries(File f) {

    }

    public double getXmlVersion() {
        return xmlVersion;
    }

    public String getEncoding() {
        return encoding;
    }

    public Description getDescription() {
        return description;
    }

    public Body getBody() {
        return body;
    }

}
