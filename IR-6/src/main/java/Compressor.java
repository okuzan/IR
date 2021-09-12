import java.io.*;
import java.util.*;

public class Compressor {
    private static final int PREFIX_LENGTH = 3;
    private static final int BLOCK_SIZE = 4;
    ArrayList<String> terms = new ArrayList<>();
    ArrayList<int[]> postingList = new ArrayList<>();
    ArrayList<Long> termPtrs;
    StringBuilder dictString;

    Compressor(TreeMap<String, ArrayList<Integer>> map) {
        convertMap(map);
        createZipIndex();
    }

    Compressor(File file) {
        parseIndexFile(file);
        createZipIndex();
    }

    private void convertMap(TreeMap<String, ArrayList<Integer>> map) {
        for (Map.Entry entry : map.entrySet()) {
            terms.add((String) entry.getKey());
            postingList.add((int[]) entry.getValue());
        }
    }

    private void parseIndexFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                IDPL idpl = new IDPL(line);
                terms.add(idpl.term);
                postingList.add(idpl.idArr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    byte[] strToByteArr(String encoded) {
        BitSet bitSet = new BitSet(encoded.length());
        for (int i = 0; i < encoded.length(); i++)
            bitSet.set(i, encoded.charAt(i) == '0');
        return bitSet.toByteArray();
    }

    private String gammaEnc(Integer i) {
        String encoded = "";
        for (int j = 0; j < i; j++) encoded += '1';
        encoded += '0';
        String binaryString = Integer.toBinaryString(i);
        String offset = binaryString.substring(1);
        encoded += offset;
        return encoded;
    }

    private static ArrayList<Integer> gammaDec(String posting) {
        ArrayList<Integer> postingList = new ArrayList<>();
        int j, lastJ = 0;
        while (lastJ < posting.length()) {
            j = 0;
            while (posting.charAt(lastJ + j) != '0') j++;
            String binary = '1' + posting.substring(lastJ + j + 1, lastJ + 2 * j + 1);
            int n = 0;
            for (int i = 0; i < binary.length(); ++i) n = (n << 1) + (binary.charAt(i) == '1' ? 1 : 0);
            postingList.add(n);
            lastJ += 2 * j + 1;
        }
        return postingList;
    }

    private String frontCoding(List<String> portion) {
        String compressed = "";
        int smallest = Short.MAX_VALUE;

        //find shortest string's length
        for (String s : portion) {
            int size = s.length();
            if (size < smallest) smallest = size;
        }
        int prefixLength = 0;

        //find prefix's length
        outerloop:
        for (int i = 0; i < smallest; i++) {
            char cur = portion.get(0).charAt(prefixLength);
            for (int j = 1; j < portion.size(); j++) {
                char cand = portion.get(j).charAt(prefixLength);
                if (cand != cur) break outerloop;
            }
            prefixLength++;
        }
        if (prefixLength < PREFIX_LENGTH) return null;
        compressed = prefixLength + portion.get(0).substring(0, prefixLength) + "$";
        for (String s : portion) compressed += s.length() + "$" + s.substring(prefixLength);

        return compressed;
    }

    private int[] zipList(int[] postings) {
        int[] gapList = new int[postings.length];
        gapList[0] = postings[0];
        for (int i = 1; i < postings.length; i++)
            gapList[i] = postings[i] - postings[i - 1];
        return gapList;
    }

    private void zipDict() {
        dictString = new StringBuilder();
        termPtrs = new ArrayList<>();
        termPtrs.add(0L);
        long length = 0L;
        for (int i = BLOCK_SIZE; i < terms.size(); i += BLOCK_SIZE)
            pointTerms(terms.subList(i - BLOCK_SIZE, i - 1), length);
        int div = terms.size() % BLOCK_SIZE;
        pointTerms(terms.subList(terms.size() - BLOCK_SIZE, terms.size() - 1), length);
    }

    private void pointTerms(List<String> subList, long length) {
        if (frontCoding(subList) != null) {
            String append = frontCoding(subList);
            length += append.length();
            dictString.append(append);
            termPtrs.add(length);
            dictString.append(frontCoding(subList));
        } else for (String tempS : subList) {
            length += tempS.length();
            termPtrs.add(length);
        }
    }

    public static int[] convertIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++) ret[i] = iterator.next();
        return ret;
    }

    private String printAL(ArrayList<Long> ids) {
        StringBuilder res = new StringBuilder();
        for (Long i : ids) res.append(i).append(" ");
        return String.valueOf(res);
    }

    private String printArr(int[] ids) {
        StringBuilder res = new StringBuilder();
        for (int i : ids) res.append(i).append(" ");
        return String.valueOf(res);
    }

    private void createZipIndex() {
        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(new File("src/main/output/zippedIndex.txt")));
            zipDict();
            writer.write(dictString + "\n");
            writer.write(printAL(termPtrs) + "\n");
            StringBuilder postingSB = new StringBuilder();
            for (int[] ints : postingList) {
                int[] zipped = zipList(ints);
                for (int i : zipped) postingSB.append(gammaEnc(i));
                postingSB.append("\n");
            }
            writer.write(String.valueOf(postingSB));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readZipIndex() {
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader("src/main/output/zippedIndex.txt"));
            dictString = new StringBuilder(reader.readLine());
            String[] ptrsStrings = reader.readLine().split("\\s+");
            for (String ptrStr : ptrsStrings)
                termPtrs.add(Long.parseLong(ptrStr));
            String line;
            while ((line = reader.readLine()) != null)
                postingList.add(convertIntegers(gammaDec(line)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        File index = new File("src/main/output/index.txt");
        Compressor zip = new Compressor(index);
    }
}