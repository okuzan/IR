import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class Glossary {
    private static int vocSize = 0;
    private static int textSize = 0;
    private int libSize = 0;

    private static TreeMap<String, Map<Integer, Integer>> map;

    public Glossary() throws Exception {
        map = new TreeMap<String, Map<Integer, Integer>>();
        input();
        createMatrix();
        writeData();
    }

    private void createMatrix() {
        StringBuilder matrix = new StringBuilder();
        for (String key : map.keySet()) {
            matrix.append(key).append(": ");
            for (int i = 0; i < 30 - key.length(); i++) matrix.append(" ");

            for (int j = 0; j < libSize; j++) {
                if(map.get(key).keySet().contains(j)) matrix.append("[1] ");
                else matrix.append("[0] ");
            }
            matrix.append("\n");
        }

        FileOutputStream fos;
        File file;

        try {
            file = new File("src/main/output/Matrix.txt");
            fos = new FileOutputStream(file);
            byte[] contentInBytes = String.valueOf(matrix).getBytes();
            fos.write(contentInBytes);
            fos.close();
            System.out.println("Matrix printed in the file.");

        } catch (IOException e) {
            e.printStackTrace();
        }
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
            for (String s : parser.getLexems()) insert(s, id);
            id++;
        }
    }

    private void insert(String word, int id) {
//        if(!word.matches("[A-Za-z]*|[ЁёА-я]*")) return;
        if (!map.containsKey(word)) {
            vocSize += word.getBytes().length;
            Map<Integer, Integer> inner = new TreeMap<Integer, Integer>();
            inner.put(id, 1);
            map.put(word, inner);

        } else {
            Map<Integer, Integer> inner = map.get(word);
            if (!inner.containsKey(id)) inner.put(id, 1);
            else inner.put(id, inner.get(id) + 1);
        }
    }

    private void writeData() {
        StringBuilder results = new StringBuilder();

        results.append("Unique entries: ").append(map.size()).append("\n");
        results.append("Size of collection: ").append(vocSize).append("\n");
        results.append("Size of books: ").append(textSize).append("\n");

        for (Map.Entry<String, Map<Integer, Integer>> entry : map.entrySet()) {
            String key = entry.getKey();
            Map<Integer, Integer> value = entry.getValue();
            results.append(key).append(": ");
            for (Map.Entry<Integer, Integer> ids : value.entrySet()) {
                Integer docid = ids.getKey();
                Integer instances = ids.getValue();
                results.append("Doc #" + docid + ": " + instances + " times. ");
            }
            results.append("\n");
        }

        FileOutputStream fop;
        File file;

        try {
            file = new File("src/main/output/Results.txt");
            fop = new FileOutputStream(file);
            byte[] contentInBytes = String.valueOf(results).getBytes();
            fop.write(contentInBytes);
            fop.close();
            System.out.println("Results printed in the file.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        Glossary glossary = new Glossary();
        long end = System.nanoTime();
        System.out.println(((end - start) / 1000000000) + " s");
        StringBuilder stats = new StringBuilder();
        stats.append("Unique entries: ").append(map.size()).append("\n");
        stats.append("Size of collection: ").append(vocSize).append("\n");
        stats.append("Size of books: ").append(textSize).append("\n");
        Search search = new Search(map);
        System.out.println(Arrays.toString(search.processRequest(" верить || сомневаться ").toArray()));
    }
}