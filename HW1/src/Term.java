import java.util.ArrayList;

public class Term {

    private String word;
    private int count;
    private ArrayList<Integer> ids;

    public Term(String word, int id) {
        this.word = word;
        count = 0;
        ids = new ArrayList<>();
        ids.add(id);
    }

}
