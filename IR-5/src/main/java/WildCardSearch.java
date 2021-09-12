import java.util.ArrayList;
import java.util.HashMap;

public class WildCardSearch {
    private HashMap<String, ArrayList<Integer>> map;
    private HashMap<String, ArrayList<String>> gramMap;
    private Trie trie, reverseTrie, permutrie;
    private static int ABCsize = 26;

    WildCardSearch(HashMap<String, ArrayList<Integer>> map,
                   HashMap<String, ArrayList<String>> gramMap,
                   Trie trie, Trie reverseTrie, Trie permutrie) {

        this.trie = trie;
        this.reverseTrie = reverseTrie;
        this.map = map;
        this.permutrie = permutrie;
        this.gramMap = gramMap;
    }

    public ArrayList<String> anyJoker(String query) {
        if (query.matches(".+\\*")) return endJoker(query);
        if (query.matches("\\*.+")) return frontJoker(query);
        if (query.matches(".+\\*.+")) return permutationJoker(query);
        return null;
    }

    private ArrayList<String> endJoker(String query) {
        String plain = query.substring(0, query.length() - 1);
        Trie.Node curNode = trail(query, 0);
        return joker(curNode, new ArrayList<>(), plain, 0);
    }

    public ArrayList<String> frontJoker(String query) {
        String plain = query.substring(1);
        Trie.Node lastNode = trail(query, 1);
        return joker(lastNode, new ArrayList<>(), plain, 1);
    }


    public ArrayList<String> permutationJoker(String query) {
        String framedQ = query + "$";
        int delim = framedQ.indexOf("*");
        int delim2 = framedQ.lastIndexOf("*");
        framedQ = framedQ.substring(delim2 + 1) + framedQ.substring(0, delim + 1);
        String plain = framedQ.substring(0, framedQ.length() - 1);
        Trie.Node node = trail(framedQ, 2);
        ArrayList<String> suitable = joker(node, new ArrayList<>(), plain, 2);
        regexQ(query, suitable);
        return suitable;
    }

    public void regexQ(String query, ArrayList<String> suitable) {
        int delim = query.indexOf("*");
        int delim2 = query.lastIndexOf("*");

        if (delim != delim2) {
            String finalRegex = query.replace("*", ".*");
            suitable.removeIf(s -> !s.matches(finalRegex));
        }
    }

//    public ArrayList<String> kgram(String query) {
//        String framedQ = "$" + query + "$";
//        int delim = framedQ.indexOf("*");
//        int delim2 = framedQ.lastIndexOf("*");
//        String backPart = framedQ.substring(delim2);
//        String firstPart = framedQ.substring(0, delim + 1);
//        ArrayList<String> grams = new ArrayList<>(Arrays.asList(Glossary.gram(firstPart, 3)));
//        grams.addAll(Arrays.asList(Glossary.gram(backPart, 3)));
//        ArrayList<String> suitable = gramMap.get(grams.get(0));
//
//        for (int i = 1; i < grams.size(); i++) {
//            String str = grams.get(i);
//            suitable.retainAll(gramMap.get(str));
//            if (suitable.size() == 0) break;
//        }
//        regexQ(query, suitable);
//        return suitable;
//    }

    private Trie.Node trail(String query, int regime) {
        Trie trie = this.trie;
        if (regime == 1) {
            query = String.valueOf(new StringBuilder(query).reverse());
            trie = reverseTrie;
        }
        if (regime == 2) trie = permutrie;
        Trie.Node cur = trie.root;
        for (int i = 0; i < query.length(); i++) {
            char c = query.charAt(i);
            if (c == '*') return cur;
            int index = c - 'a';
            if (c == '$') index = 26;
            if (cur.kids[index] != null) cur = cur.kids[index];
            else {
                System.out.println("There is no such word!");
                break;
            }
        }
        return null;
    }

    private ArrayList<String> joker(Trie.Node cur, ArrayList<String> al, String plain, int regime) {
        for (int j = 0; j < cur.kids.length; j++) {
            if (cur.kids[j] != null) {
                if (!cur.kids[j].isEnd) {
                    if (regime != 1) {
                        joker(cur.kids[j], al, plain + Character.toString('a' + j), regime);
                    } else
                        joker(cur.kids[j], al, Character.toString('a' + j) + plain, 1);
                } else {
                    if (regime != 1) {
                        al.add(plain + Character.toString('a' + j));
                    } else
                        al.add(Character.toString('a' + j) + plain);
                }
            }
        }
        if (regime == 2) frameAL(al);
        return al;
    }

    private void frameAL(ArrayList<String> al) {
        for (int i = 0; i < al.size(); i++) {
            String s = al.get(i);
            int delim = s.indexOf("$");
            s = s.substring(delim + 1) + s.substring(0, delim + 1);
            s = s.replace("$", "");
            al.set(i, s);
        }

    }
}
