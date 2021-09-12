import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Glossary {
    private static final int K = 3;

    private static HashMap<String, ArrayList<Integer>> map;
    private static HashMap<String, ArrayList<String>> gramMap;

    private static Trie trie;
    private static Trie reverseTrie;
    private static Trie permuTrie;

    public Glossary() {
        map = new HashMap<>();
        gramMap = new HashMap<>();
        input();
        trie = trie(true);
        reverseTrie = trie(false);
        permuTrie = permutermIndexing();
    }

    private void input() {
        try {
            File[] files = new File("src/main/resources").listFiles();
            if (files == null) throw new FileNotFoundException();
            System.out.println(files.length + " files presented");
            int id = 0;
            for (File file : files) {
                System.out.println("now: id - " + id);
                Parser parser = new Parser(file);
                simpleIndex(parser.getLexems(), id, map);
                kGramIndexing(parser.getLexems());
                id++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Filling data structures
     */
    private Trie permutermIndexing() {
        Trie trie = new Trie(map, true);
        for (String word : map.keySet()) {
            if (word.equals("")) continue;
            if (!word.matches("[A-Za-z]+")) continue;
            trie.insert(permutations(word));
        }
        return trie;
    }

    private Trie trie(boolean straightOrder) {
        Trie trie = new Trie(map, false);
        for (String word : map.keySet()) {
            if (word.equals("")) continue;
            if (!word.matches("[A-Za-z]+|[ЁёА-я]+")) continue;
            if (!straightOrder) word = String.valueOf(new StringBuilder(word).reverse());
            trie.insert(word);
        }
        return trie;
    }


    private void kGramIndexing(String[] words) {
        for (String word : words) {
            if (!word.matches("[A-Za-z]+")) continue;
            String extWord = "$" + word + "$";
            String[] grams = gram(extWord, Glossary.K);
            gramInsert(word, grams);
        }
    }

    private void simpleIndex(String[] lexems, int id, Map<String, ArrayList<Integer>> map) {
        for (String word : lexems) {
            if (!map.containsKey(word)) {
                ArrayList<Integer> postlist = new ArrayList<>();
                postlist.add(id);
                map.put(word, postlist);
            } else {
                ArrayList<Integer> postlist = map.get(word);
                if (!postlist.contains(id))
                    postlist.add(id);
            }
        }
    }

    private void gramInsert(String word, String[] grams) {
        for (String gram : grams) {
            if (!gramMap.containsKey(gram)) {
                ArrayList<String> wordlist = new ArrayList<>();
                wordlist.add(word);
                gramMap.put(gram, wordlist);
            } else {
                ArrayList<String> postlist = gramMap.get(gram);
                if (!postlist.contains(word))
                    postlist.add(word);
            }
        }
    }

    // util. gram tokenizer
    static String[] gram(String word, int step) {
        word = word.replace("*", "");
        int length = word.length() - step + 1;
        String[] grams = new String[length];
        for (int i = 0; i < length; i++) {
            grams[i] = word.substring(i, i + step);
        }
        return grams;
    }

    // util. permutation generator
    private static String[] permutations(String word) {
        String[] permutations = new String[word.length()];
        word = word + "$";
        permutations[0] = word;
        for (int i = 1; i < word.length() - 1; i++)
            permutations[i] = word.substring(i) + word.substring(0, i);
        return permutations;
    }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        Glossary glossary = new Glossary();
        long end = System.nanoTime();
        System.out.println(((end - start) / 1000000000) + " s");
        WildCardSearch search = new WildCardSearch(map, gramMap, trie, reverseTrie, permuTrie);
        System.out.println(Arrays.toString(search.anyJoker("ador*").toArray()));
        System.out.println(Arrays.toString(search.anyJoker("*ster").toArray()));
        System.out.println(Arrays.toString(search.anyJoker("ter*ed").toArray()));
        System.out.println(Arrays.toString(search.kgram("ter*ed").toArray()));
    }
}