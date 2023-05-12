public class BM25 {

    private double k_1 = 1.2d;
    private double k_3 = 8d;
    private double b;

    public BM25() {
        super();
        b = 0.75d;
    }

    public final String settingsInfo() {
        return "BM25: b = " + b + ", k_1 = " + k_1 + ", k_3 = " + k_3;
    }

    public final double score(double tf, double numberOfDocuments, double docLength,
                              double averageDocumentLength, double queryFrequency, double documentFrequency) {

        double K = k_1 * ((1 - b) + ((b * docLength) / averageDocumentLength));
        double weight = (((k_1 + 1d) * tf) / (K + tf));
        weight = weight * (((k_3 + 1) * queryFrequency) / (k_3 + queryFrequency));

        return weight * Math.log((numberOfDocuments - documentFrequency + 0.5d) / (documentFrequency + 0.5d));
    }
}