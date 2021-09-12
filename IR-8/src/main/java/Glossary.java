import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.IntStream;

public class Glossary {
    private static final String GUT_PATH = "D:/JavaPro/Library/gutenberg/1";
    private static final String LIB_PATH = "src/main/resources";
    private final static int N_LEADERS_Q = 3;
    private final static int N_LEADERS = 1;
    private final static int N_RESULTS = 10;
    public static ArrayList<double[]> vectorSpace = new ArrayList<>();
    private static HashMap<String, Map<Integer, Integer>> map;
    private Map<Integer, ArrayList<Integer>> clusters;
    private List<String> vocNumerated;
    private static double[] idfs;
    private static int[] leadersIds;
    private static int collectionSize = 0;
    private static int totalWords = 0;
    private static int dictSize = 0;
    private static int booksID = 0;
    private static long start;

    public Glossary() throws Exception {
        input();
        calcIDFs();
        calcVectors();
        formClusters();
    }

    //docs -> vectors
    private void calcVectors() {
        vocNumerated = new ArrayList<>(map.keySet());
        for (int i = 0; i < booksID; i++) {
            double[] vals = new double[dictSize];
            for (int j = 0; j < dictSize; j++)
                vals[j] = tf_idf(i, j);
            vectorSpace.add(vals);
        }
    }

    //static IDFs pre-calculating
    private void calcIDFs() {
        idfs = new double[dictSize];
        int i = 0;
        for (String key : map.keySet())
            idfs[i++] = Math.log((double) booksID / map.get(key).keySet().size());
    }

    //func to calc tf_idf score for some doc-term pair
    private double tf_idf(int docID, int wordID) {
        String word = vocNumerated.get(wordID);
        if (map.get(word).containsKey(docID)) {
            int tf = map.get(word).get(docID);
            return tf * idfs[wordID];
        } else return 0;
    }

    //forming clusters: finding leaders and attaching followers to them
    private void formClusters() {
        leadersIds = randLeaders(booksID);
        clusters = new HashMap<>();
        for (int i = 0; i < booksID; i++) {
            List<Integer> leaders = closestLeaders(vectorSpace.get(i), true);
            for (int leader : leaders) {
                clusters.computeIfAbsent(leader, k -> new ArrayList<>());
                clusters.get(leader).add(i);
            }
        }
    }

    //query -> vector
    private double[] queryToVector(String query) {
        double[] vals = new double[dictSize];
        String[] splits = query.split(" ");
        for (String split : splits) {
            int i = vocNumerated.indexOf(split);
            vals[i] = idfs[i];
        }
        return vals;
    }

    //func for both comapring doc to doc(leader) or query to doc. Thus, we need boolean
    private List<Integer> closestLeaders(double[] vector, boolean docNotQ) {
        ArrayList<Integer> leaders = new ArrayList<>();
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        int max = docNotQ ? N_LEADERS : N_LEADERS_Q;

        for (int i : leadersIds) {
            double score = cosSim(vectorSpace.get(i), vector);
            pq.add(new Pair(i, score));
            if (pq.size() > max) pq.poll();
        }
        while (pq.size() > 0) leaders.add(pq.poll().id);

        return leaders;
    }

    //comparing query to leaders and then to their followers to receive ranged output
    public void processQuery(String query) {
        double[] qVec = queryToVector(query);
        List<Integer> leaders = closestLeaders(qVec, false);
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        ArrayList<Integer> result = new ArrayList<>();
        for (int leader : leaders) {
            List<Integer> followers = clusters.get(leader);
            for (int follower : followers)
                pq.add(new Pair(follower, cosSim(qVec, vectorSpace.get(follower))));
        }

        int i = 0;
        while (pq.size() > 0 && i < N_RESULTS) {
            result.add(pq.poll().id);
            i++;
        }
        System.out.println(Arrays.toString(leaders.toArray()));
    }

    //func for calc cosine similarity between vectors (docs/queries)
    private double cosSim(double[] v1, double[] v2) {
        double num;
        double sum1 = 0, sum2 = 0;
        num = IntStream.range(0, v1.length).mapToDouble(i -> v1[i] * v2[i]).sum();
        for (double i2 : v1) sum1 += i2 * i2;
        for (double i2 : v2) sum2 += i2 * i2;
        double den = Math.sqrt(sum1) * Math.sqrt(sum2);
        return num / den;
    }

    //pseudo-randomizing leaders among docs
    static int[] randLeaders(int n) {
        int i;
        int k = (int) Math.sqrt(n);
        int[] reservoir = new int[k];
        for (i = 0; i < k; i++) reservoir[i] = i;
        Random r = new Random();
        while (i < n) {
            int j = r.nextInt(i + 1);
            if (j < k) reservoir[j] = i;
            i++;
        }
        return reservoir;
    }

    //processing input
    private void input() throws Exception {
        map = new HashMap<>();
        File[] files = new File(LIB_PATH).listFiles();
        if (files == null) throw new FileNotFoundException();
        System.out.println(files.length + " files presented");
        for (File file : files) {
            System.out.println("now: id - " + booksID);
            simpleIndex(file, booksID);
            booksID++;
        }
    }

    //filling map so we have frequency of every term
    private void simpleIndex(File file, int id) {
        collectionSize += file.length();
        Parser parser = new Parser(file);
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

    public static void main(String[] args) throws Exception {
        start = System.nanoTime();
        Glossary glossary = new Glossary();
        glossary.printGeneralResults();
        glossary.processQuery("my dear");
    }

    private void printGeneralResults() {
        System.out.printf("Collection size: " + "%.1f" + " MBs\n", (double) collectionSize / Math.pow(2, 20));
        System.out.println("Total books: " + (booksID + 1));
        System.out.println("Total words: " + totalWords);
        System.out.println("Dictionary size: " + dictSize + " terms");
        System.out.printf("%.2f" + " sec\n", (System.nanoTime() - start) / Math.pow(10, 9));
        clustersInfo();
    }

    private void clustersInfo() {
        System.out.println("There are " + clusters.keySet().size() + " clusters");
        int i = 0;
        for (int key : clusters.keySet())
            System.out.println("Cluster #" + i + " (doc " + key + ") : "
                    + clusters.get(key).size() + " followers");

    }

    //util class for using in PriorityQueue by scores comparison meanwhile saving ids
    static class Pair implements Comparable<Pair> {
        int id;
        double score;

        Pair(int id, double score) {
            this.id = id;
            this.score = score;
        }

        @Override
        public int compareTo(Pair pair) {
            double d = pair.score - score;
            if (d > 0) return 1;
            if (d < 0) return -1;
            return 0;
        }
    }
}
