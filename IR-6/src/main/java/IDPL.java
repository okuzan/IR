import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class IDPL implements Comparable<IDPL> {

    protected String entry;
    protected String term;
    protected ArrayList<Integer> posting;
    protected int blockID;
    protected int[] idArr;

    public IDPL(String entry, int blockID) {
        this.blockID = blockID;
        this.entry = entry;
        calcFields();
    }

    public IDPL(String entry) {
        this.entry = entry;
        calcFields();
    }

    private void calcFields() {
        String[] parts = entry.trim().split(" \\| ");
        int[] ids = Stream.of(parts[1].trim().split(" ")).mapToInt(Integer::parseInt).toArray();
        posting = (ArrayList<Integer>) Arrays.stream(ids).boxed().collect(Collectors.toList());
        term = parts[0];
        idArr = ids;
    }

    private String printAL(ArrayList<Integer> ids) {
        StringBuilder res = new StringBuilder();
        for (int i : ids) res.append(i).append(" ");
        return String.valueOf(res);
    }

    @Override
    public String toString() {
        return term + " | " + printAL(posting) + "\n";
    }

    @Override
    public int compareTo(IDPL that) {
        return this.term.compareTo(that.term);
    }
}
