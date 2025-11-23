package clustering_algorithm;

import java.util.ArrayList;
import java.util.List;

public class KMeans {

    private int k;
    private final int maxIterations = 20;
    private double[][] data;
    private double[][] coordinates;
    private List<double[]>[] points;

    public KMeans(int k, double[][] data) {
        this.k = k;

        this.data = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; ++i) {
            for (int j = 0; j < data[0].length; ++j) this.data[i][j] = data[i][j];
        }

        coordinates = new double[k][data[0].length];
        points = new List[k];
        for (int i = 0; i < k; ++i) points[i] = new ArrayList<>();
    }

    public void initCentroid() {

    }
}
