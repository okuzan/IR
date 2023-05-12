import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tester {
    private static HashMap<String, Map<Integer, Integer>> map = new HashMap<>();
    private static ArrayList<Integer> docLengths = new ArrayList<>();
    private static int books;

    public static void main(String[] args) {
        input();
        process("never again");
    }

    private static void input() {
        try {
            File[] files = new File("./lib/").listFiles();
            if (files == null) throw new FileNotFoundException();
            books = files.length;
            System.out.println(files.length + " files presented");
            int id = 0;
            for (File file : files) {
                System.out.println("now: id - " + id);
                simpleIndex(file, id);
                id++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //filling map so we have frequency of every term
    private static void simpleIndex(File file, int id) {
        Parser parser = new Parser(file);
        String[] lexemes = parser.getLexems();
        docLengths.add(lexemes.length);
        for (String word : lexemes) {
            if (!map.containsKey(word)) {
                HashMap<Integer, Integer> imap = new HashMap<>();
                imap.put(id, 0);
                map.put(word, imap);
            } else {
                Map<Integer, Integer> imap = map.get(word);
                if (imap.get(id) != null) imap.put(id, imap.get(id) + 1);
                else imap.put(id, 0);
            }
        }
    }


    static void process(String input) {
        String[] query = input.split(" ");
        HashMap<String, Integer> request = new HashMap<>();
        for (String s : query) {
            System.out.println("Adding word " + s);
            if (request.containsKey(s))
                request.put(s, request.get(s) + 1);
            else
                request.put(s, 1);
        }
        System.out.println("Query words: " + request.keySet());

        for (String reqWord : request.keySet()) {
            double avgLen = 0;
            for (Integer partIdLen : map.get(reqWord).keySet())
                avgLen += docLengths.get(partIdLen);
            avgLen /= map.get(reqWord).keySet().size();

            System.out.println("Current WORD: " + reqWord);
            for (Integer docID : map.get(reqWord).keySet()) {
                System.out.println("Doc ID: " + docID);
                System.out.println("Average document length: " + avgLen);
                BM25 bm25 = new BM25();
                double termFrequencyInDoc = map.get(reqWord).get(docID);
                System.out.println("Term frequency in document " + docID + ": " + termFrequencyInDoc);
                System.out.println("Total docs in collection: " + books);
                double docLen = docLengths.get(docID);
                System.out.println("Document (" + docID + ") length is: " + docLen);
                double docFreq = map.get(reqWord).size();
                System.out.println("Document frequency of word " + reqWord + "  is: " + docFreq);
                System.out.println("BM25 score is: " + bm25.score(termFrequencyInDoc, books, docLen, avgLen, request.get(reqWord), docFreq));
            }
        }
    }
}