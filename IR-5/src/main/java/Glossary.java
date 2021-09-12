import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Glossary {
    private static final String GUT_PATH = "D:/JavaPro/Library/gutenberg/1";
    private static final String LIB_PATH = "src/main/resources";
    private static final int FILES_THRESHOLD = 20;
    private static final int MEMORY_THRESHOLD = 3;
    private static int booksID = 0;
    private static int collectionSize = 0;
    private static int totalWords = 0;
    private static int dictSize = 0;
    private static int blocksQ = 0;
    private static long start = 0;

    public Glossary() {
        processInput();
    }

    private void processInput() {
        try {
            streamContent(GUT_PATH);
            mergeParts();
        } catch (IOException e) {
            System.out.println("Something went wrong");
            e.printStackTrace();
        }
    }

    public void streamContent(String sDir) throws IOException {
        ArrayList<File> al = new ArrayList<>();
        Files.find(Paths.get(sDir), Integer.MAX_VALUE,
                (p, bfa) -> bfa.isRegularFile()).forEach(file -> fillStream(file, al));
        if (al.size() != 0) psimi(al);
    }

    public void fillStream(Path path, ArrayList<File> tokenized) {
        tokenized.add(path.toFile());
        double percent = Runtime.getRuntime().freeMemory() / (double) Runtime.getRuntime().maxMemory() * 100;
//        System.out.println("Usage: " + percent);
//        if (percent < MEMORY_THRESHOLD) System.out.println("New stream due to low memory remaining");
        if (tokenized.size() >= FILES_THRESHOLD ) {
            System.out.println("New stream just started");
            try {
                psimi(tokenized);
                tokenized.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void psimi(ArrayList<File> files) throws IOException {
        System.out.println("Books [" + booksID + " - " + (booksID + files.size()) + "]");
        var map = new TreeMap<String, ArrayList<Integer>>();
        for (File file : files) {
            collectionSize += file.length();
            Parser parser = new Parser(file);
            String[] lexemes = parser.getLexems();
            for (String word : lexemes) {
                totalWords++;
                if (!map.containsKey(word)) {
                    ArrayList<Integer> postlist = new ArrayList<>();
                    postlist.add(booksID);
                    map.put(word, postlist);
                } else {
                    ArrayList<Integer> postlist = map.get(word);
                    if (postlist.get(postlist.size() - 1) != booksID) postlist.add(booksID);
                }
            }
            if (booksID % 100 == 0 || booksID % FILES_THRESHOLD == 0) printRuntime(map);
            booksID++;
        }
        printMap(map);
    }


    private void printRuntime(Map<String, ArrayList<Integer>> map) {
        double percent = Runtime.getRuntime().freeMemory() / (double) Runtime.getRuntime().totalMemory() * 100;
        System.out.printf("%.1f" + " min\n", (System.nanoTime() - start) / Math.pow(10, 9) / 60);
        System.out.printf("%.1f" + " MB\n", collectionSize / Math.pow(2, 20));
        System.out.printf("%.1f" + " percents\n", percent);
        System.out.printf("%.1f" + " MB free memory\n", Runtime.getRuntime().freeMemory() / Math.pow(2, 20));
        System.out.printf("%.1f" + " MB total memory\n", Runtime.getRuntime().totalMemory() / Math.pow(2, 20));
        System.out.printf("%.1f" + " MB max memory\n", Runtime.getRuntime().maxMemory() / Math.pow(2, 20));
        System.out.println("ID " + booksID + " proceeded");
        System.out.println("Keys: " + map.keySet().size());
        System.out.printf("Speed per 100 books: " + "%.1f\n", (double) (System.nanoTime() - start)
                / Math.pow(10, 9) * 100 / booksID);
        System.out.printf("%.1f" + " MB/s\n", collectionSize / Math.pow(2, 20) / ((System.nanoTime() - start)
                / Math.pow(10, 9)));
        System.out.println("________________\n");

    }

    private void printMap(Map<String, ArrayList<Integer>> map) throws IOException {
        String path = "src/main/output/parts";
        File directory = new File(path);
        directory.mkdirs();
        var block = new File(path + "/part " + (blocksQ++) + ".txt");
        var writer = new BufferedWriter(new FileWriter(block));
        for (Map.Entry<String, ArrayList<Integer>> entry : map.entrySet())
            writer.write(entry.getKey() + " | " + printAL(entry.getValue()) + "\n");
        writer.close();
    }

    private static String printAL(ArrayList<Integer> ids) {
        StringBuilder res = new StringBuilder();
        for (int i : ids) res.append(i).append(" ");
        return String.valueOf(res);
    }

    private void mergeParts() throws IOException {
        blocksQ = 20;
        File resultIndex = new File("src/main/output/index.txt");
        var writer = new BufferedWriter(new FileWriter(resultIndex));
        ArrayList<BufferedReader> bfReaders = new ArrayList<>();
        IDPL curEntry, recentEntry;
        String line;
        var pq = new PriorityQueue<IDPL>();
        for (int i = 0; i < blocksQ; i++)
            bfReaders.add(i, new BufferedReader(new FileReader("src/main/output/parts/part " + i + ".txt")));
        for (int i = 0; i < blocksQ; i++) {
            line = bfReaders.get(i).readLine();
            pq.add(new IDPL(line, i));
        }
        recentEntry = pq.poll();

        while (pq.size() > 0) {
            curEntry = pq.poll();
            if (curEntry.term.equals(recentEntry.term)) {
                recentEntry.posting.addAll(curEntry.posting);
            } else {
                Collections.sort(recentEntry.posting);
                writer.write(recentEntry.toString());
                dictSize++;
                recentEntry = curEntry;
                if (pq.size() == 0)
                    writer.write(recentEntry.toString());
            }
            int lastChoiceIndex = curEntry.blockID;
            BufferedReader reader = bfReaders.get(lastChoiceIndex);
            if (reader != null) {
                line = reader.readLine();
                if (line != null) pq.add(new IDPL(line, lastChoiceIndex));
                else {
                    bfReaders.get(lastChoiceIndex).close();
                    bfReaders.set(lastChoiceIndex, null);
                }
            }
        }
        writer.close();
    }

    public static void main(String[] args) {
        start = System.nanoTime();
        Glossary glossary = new Glossary();
        printResults();
    }

    private static void printResults() {
        System.out.printf("Collection size: " + "%.1f" + " MBs\n", (double) collectionSize / Math.pow(2, 20));
        System.out.println("Total books: " + (booksID + 1));
        System.out.println("Total words: " + totalWords);
        System.out.println("Dictionary size: " + dictSize + " terms");
        System.out.printf("%.2f" + " sec", (System.nanoTime() - start) / Math.pow(10, 9));
    }
}