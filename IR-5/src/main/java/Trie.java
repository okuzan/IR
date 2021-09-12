import java.util.ArrayList;
import java.util.HashMap;

public class Trie {

    private static int ABC = 26;
    public Node root;
    private HashMap<String, ArrayList<Integer>> map;

    Trie(HashMap<String, ArrayList<Integer>> map, boolean extended) {
        if (extended) ABC++;
        root = new Node();
        this.map = map;
    }

    public void insert(String word) {
//        if (!check(word)) return;
//        if (!word.matches("[A-Za-z]*|[ЁёА-я]*")) return;
        Node cur = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            int index = c - 'a';
            if (c == '$')
                index = 26;

            if (cur.kids[index] == null)
                cur.kids[index] = new Node(c);

            cur = cur.kids[index];
        }
//        if (cur.c == word.charAt(word.length() - 1)) System.out.println("checked");
        cur.isEnd = true;
    }

    public void insert(String[] words) {
        for (String word : words) {
            insert(word);
        }
    }

    private boolean check(String word) {
        return word.matches("\\w*");
    }

    public static void main(String[] args) {

    }

    class Node {
        Node[] kids = new Node[ABC];
        public boolean isEnd = false;
        public char c;
        public Node(char c) {
            this.c = c;
        }
        public Node() { }
    }
}
