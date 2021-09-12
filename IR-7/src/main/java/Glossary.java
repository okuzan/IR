import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Glossary {
    private static HashMap<String, Map<Integer, Integer>> map;
    private static HashMap<String, Map<Integer, ArrayList<Integer>>> metaMap;
    private static HashMap<Integer, ArrayList<String>> idToMeta;
    private static final String GUT_PATH = "D:/JavaPro/Library/gutenberg/1";
    private static final String LIB_PATH = "src/main/resources";
    private static final short FEED_MAX = 4;
    private static final double TITLE_K = 0.3;
    private static final double AUTHOR_K = 0.1;
    private static final double ANNOT_K = 0.2;
    private static final double BODY_K = 0.4;
    private static int booksID = 0;
    private static int collectionSize = 0;
    private static int totalWords = 0;
    private static int dictSize = 0;
    private static long start;

    public Glossary() throws Exception {
        map = new HashMap<>();
        metaMap = new HashMap<>();
        idToMeta = new HashMap<>();
        input();
    }

    private void input() throws Exception {
        File[] files = new File(LIB_PATH).listFiles();
        if (files == null) throw new FileNotFoundException();
        System.out.println(files.length + " files presented");
        for (File file : files) {
            System.out.println("now: id - " + booksID);
            simpleIndex(file, booksID);
            metaIndex(file, booksID);
            booksID++;
        }
    }

    
    private void simpleIndex(File file, int id) throws ParserConfigurationException, SAXException, IOException {
        collectionSize += file.length();
        Parser parser = new Parser(file);
        idToMeta.put(id, parser.getMetadata());
        String[] lexemes = parser.getLexems();
        for (String word : lexemes) {
            totalWords++;
            if (!map.containsKey(word)) {
                dictSize++;
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

    private void metaIndex(File file, int id) throws ParserConfigurationException, SAXException, IOException {
        Parser parser = new Parser(file);
        ArrayList<String> meta = parser.getMetadata();
        int i = 0;
        for (String word : meta) {
            if (!metaMap.containsKey(word)) {
                Map<Integer, ArrayList<Integer>> map = new HashMap<>();
                ArrayList<Integer> a = new ArrayList<>();
                a.add(i);
                map.put(id, a);
                metaMap.put(word, map);
            } else {
                Map<Integer, ArrayList<Integer>> map = metaMap.get(word);
                if (map.get(id) != null) {
                    if (!map.get(id).contains(i)) map.get(id).add(i);
                } else {
                    ArrayList<Integer> a = new ArrayList<>();
                    a.add(i);
                    map.put(id, a);
                }
            }
            i++;
        }
    }

    public static List processMQuery(String query) {
        assert FEED_MAX < booksID;
        Search searchMeta = new Search(metaMap);
        Search searchBody = new Search(map);
        String[] words = query.split("\\s+");
        Set<Integer> idsMeta = searchMeta.processRequest(query.replace(" ", " & "));
        Set<Integer> idsBody = searchBody.processRequest(query.replace(" ", " & "));
        Set<Integer> mySet = new HashSet<>();
        mySet.addAll(idsBody);
        mySet.addAll(idsMeta);
        ArrayList<Integer> scores = new ArrayList<>();
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        for (int i : mySet) {
            double score = 0;
            for (String word : words) {
                if (metaMap.get(word) != null) {
                    ArrayList<Integer> d = metaMap.get(word).get(i);
                    for (int k : d) {
                        switch (k) {
                            case 0:
                                score += AUTHOR_K;
                                System.out.println("0");
                                break;
                            case 1:
                                score += TITLE_K;
                                System.out.println("1");
                                break;
                            case 2:
                                score += ANNOT_K;
                                System.out.println("2");
                                break;
                            default:
                                break;
                        }
                    }
                }
                if (map.get(word).get(i) != null) score += BODY_K;
            }
            pq.add(new Pair(i, score));
        }
        for (int i = 0; i < FEED_MAX && pq.size() > 0; i++) scores.add(pq.poll().id);
        return scores;
    }

    static class Pair implements Comparable<Pair> {
        int id;
        double score;

        Pair(int id, double score) {
            this.id = id;
            this.score = score;
        }

        @Override
        public int compareTo(Pair pair) {
            return (int) (pair.score - score);
        }
    }

    public static List processQuery(String query) {
        assert FEED_MAX < booksID;
        String[] words = query.split("\\s+");
        Search searchBody = new Search(map);
        Set<Integer> foundIn = searchBody.processRequest(query.replace(" ", " & "));
        System.out.println(Arrays.toString(foundIn.toArray()));
        ArrayList<Integer> scores = new ArrayList<>();
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        for (int i : foundIn) {
            double score = 0;
            for (String word : words) {
                int freq = map.get(word).get(i);
                double idf = Math.log((float) booksID / map.get(word).keySet().size());
                double idft = idf * freq;
                score += idft;
            }
            System.out.printf("Score: %.1f\n", score);
            pq.add(new Pair(i, score));
        }
        for (int i = 0; i < FEED_MAX && pq.size() > 0; i++) scores.add(pq.poll().id);
        return scores;
    }

    public static void main(String[] args) throws Exception {
        start = System.nanoTime();
        Glossary glossary = new Glossary();
        glossary.printResults();
//        System.out.println(Arrays.toString(processQuery("clock dizzy").toArray()));
        System.out.println(Arrays.toString(processMQuery("clock dracula").toArray()));
    }

    private void printResults() {
        System.out.printf("Collection size: " + "%.1f" + " MBs\n", (double) collectionSize / Math.pow(2, 20));
        System.out.println("Total books: " + (booksID + 1));
        System.out.println("Total words: " + totalWords);
        System.out.println("Dictionary size: " + dictSize + " terms");
        System.out.printf("%.2f" + " sec\n", (System.nanoTime() - start) / Math.pow(10, 9));
    }
}