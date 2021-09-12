public class Tester {
    public static void main(String[] args) {
        System.out.println(gammaEnc(35));
    }

    private static String gammaEnc(Integer i) {
        String encoded = "";
        for (int j = 0; j < i; j++) encoded += '1';
        encoded += '0';
        String binaryString = Integer.toBinaryString(i);
        String offset = binaryString.substring(1);
        encoded += offset;
        return encoded;
    }
}
