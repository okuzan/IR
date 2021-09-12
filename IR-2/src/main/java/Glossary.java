import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Glossary {
    private static int textSize = 0;
    private static int libSize = 0;

    //    private static TreeMap<String, Map<Integer, Integer>> simpleMap;
    private static HashMap<String, Map<Integer, ArrayList<Integer>>> coordMap, biwordMap;

    public Glossary() throws Exception {
//        simpleMap = new TreeMap<String, Map<Integer, Integer>>();
        biwordMap = new HashMap<String, Map<Integer, ArrayList<Integer>>>();
        coordMap = new HashMap<String, Map<Integer, ArrayList<Integer>>>();
        input();
        writeData(coordMap, "coordMap");
        writeData(biwordMap, "biwordMap");
    }

    private void input() throws Exception {
        File[] files = new File("src/main/resources").listFiles();
        if (files == null) throw new FileNotFoundException();
        libSize = files.length;
        System.out.println(files.length + " files presented");
        int id = 0;
        for (File file : files) {
            textSize += file.length();
            System.out.println("id: " + id);
            Parser parser = new Parser(file);
            biwordIndex(parser.getLexems(), id);
            coordIndex(parser.getLexems(), id);
            id++;
        }
    }

//    private void simpleInsert(String words[], int id) {
//        for (String word : words) {
//
////        if(!word.matches("[A-Za-z]*|[ЁёА-я]*")) return;
//            if (!simpleMap.containsKey(word)) {
//                vocSize += word.getBytes().length;
//                Map<Integer, Integer> inner = new TreeMap<Integer, Integer>();
//                inner.put(id, 1);
//                simpleMap.put(word, inner);
//
//            } else {
//                Map<Integer, Integer> inner = simpleMap.get(word);
//                if (!inner.containsKey(id)) inner.put(id, 1);
//                else inner.put(id, inner.get(id) + 1);
//            }
//        }
//    }


    private void coordInsert(String word, int id, int coord, Map<String, Map<Integer, ArrayList<Integer>>> map) {
//      if(!word.matches("[A-Za-z]*|[ЁёА-я]*")) return;
        if (!map.containsKey(word)) {
//            System.out.println("new word "+word);
            Map<Integer, ArrayList<Integer>> inner = new HashMap<Integer, ArrayList<Integer>>();
            ArrayList<Integer> al = new ArrayList<Integer>();
            al.add(coord);
            inner.put(id, al);
            map.put(word, inner);

        } else {
            Map<Integer, ArrayList<Integer>> inner = map.get(word);
            if (!inner.containsKey(id)) {
                ArrayList<Integer> al = new ArrayList<Integer>();
                al.add(coord);
                inner.put(id, al);
            } else inner.get(id).add(coord);
        }
    }

    private void biwordIndex(String[] words, int id) {
        int coord = 0;
        for (int i = 0; i < words.length - 1; i++)
            coordInsert(words[i] + " " + words[i + 1], id, coord++, biwordMap);
    }

    private void coordIndex(String[] words, int id) {
        int coord = 0;
        for (String word : words) coordInsert(word, id, coord++, coordMap);
    }

    private void writeData(HashMap<String, Map<Integer, ArrayList<Integer>>> map, String filename) {
        StringBuilder results = new StringBuilder();

        results.append("Unique entries: ").append(map.size()).append("\n");
        results.append("Size of collection: ").append(libSize).append("\n");
        results.append("Size of books: ").append(textSize).append("\n");

        for (Map.Entry<String, Map<Integer, ArrayList<Integer>>> entry : map.entrySet()) {
            String key = entry.getKey();
            Map<Integer, ArrayList<Integer>> value = entry.getValue();
            results.append(key).append(": ");
            for (Map.Entry<Integer, ArrayList<Integer>> ids : value.entrySet()) {
                Integer docid = ids.getKey();
                ArrayList<Integer> coords = ids.getValue();
                results.append("Doc #" + docid + ": " + Arrays.toString(coords.toArray()) + "\t");
            }
            results.append("\n");
        }

        FileOutputStream fop;
        File file;

        try {
            file = new File("src/main/output/Results " + filename + ".txt");
            fop = new FileOutputStream(file);
            byte[] contentInBytes = String.valueOf(results).getBytes();
            fop.write(contentInBytes);
            fop.close();
//            System.out.println("Results printed in the file.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        Glossary glossary = new Glossary();
        long end = System.nanoTime();
        System.out.println(((end - start) / 1000000000) + " s");
        BoolSearch search = new BoolSearch(coordMap, biwordMap);
        System.out.println("Method 1" + Arrays.toString(search.processBoolRequest("honor || do/1/this").toArray()));
        System.out.println("Method 2" + Arrays.toString(search.proximity("do", "this", 1).toArray()));
        System.out.println("Method 3" + Arrays.toString(search.phrasalSearchBiword("do this").toArray()));
        System.out.println("Method 4" + Arrays.toString(search.phrasalSearchCoord("do this").toArray()));
    }
}