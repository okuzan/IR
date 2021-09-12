package parser;

import java.io.File;
import java.util.Arrays;

public class Tester {
    static final String PATH = "./lib/";

    public static void main(String[] args) {
        process();
    }

    private static void process() {
        File[] files = new File(PATH).listFiles();
        for (File file : files) {
            System.out.println(file.getName());
            FictionBook fb2 = new FictionBook(file);
            getInfo(fb2);
        }
    }

    public static void getInfo(FictionBook fb2) {
        //general
        System.out.println("Title: " + fb2.getDescription().getTitleInfo().getBookTitle());

        try {
            System.out.println("XML version: " + fb2.getXmlVersion());
            System.out.println("Encoding: " + fb2.getEncoding());
            System.out.println("N sections: " + fb2.getBody().getSections().size());
            System.out.println("Body sections: " + Arrays.toString(fb2.getBody().getSections().toArray()));

            //title info
            System.out.println("Title: " + fb2.getDescription().getTitleInfo().getBookTitle());
            System.out.println("Date: " + fb2.getDescription().getTitleInfo().getDate());
            System.out.println("Lang: " + fb2.getDescription().getTitleInfo().getLang());
            System.out.println("Genres: " + Arrays.toString(fb2.getDescription().getTitleInfo().getGenres().toArray()));
            System.out.println("Title lang: " + fb2.getDescription().getTitleInfo().getLang());

            //document info
            System.out.println("Authors: " + Arrays.toString(fb2.getDescription().getDocumentInfo().getAuthors().toArray()));
            System.out.println("Document date: " + fb2.getDescription().getDocumentInfo().getDate());
            System.out.println("Document ID: " + fb2.getDescription().getDocumentInfo().getId());
            System.out.println("Document ID: " + fb2.getDescription().getDocumentInfo().getId());

            //publish info
            System.out.println("Book Name: " + fb2.getDescription().getPublishInfo().getBookName());
            System.out.println("City: " + fb2.getDescription().getPublishInfo().getCity());
            System.out.println("Publisher: " + fb2.getDescription().getPublishInfo().getPublisher());
            System.out.println("Year: " + fb2.getDescription().getPublishInfo().getYear());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
