package clustering_algorithm;

import java.util.*;

public class KMeans {

    private int k;
    private final int MAX_ITERATION = 20;
    private List<double[]> data;
    private List<Cluster> clusters;

    public KMeans(int k, List<double[]> data) {
        this.k = k;

        this.data = data;
        clusters = new ArrayList<>();
    }

    public void initCentroid() {
        Set<Integer> check = new HashSet<>();
        Random random = new Random();

        while(clusters.size() < k) {
            int t = random.nextInt(data.size());
            if (check.add(t)) clusters.add(new Cluster(data.get(t).clone()));
        }
    }

    public void assignCluster() {
        for (Cluster c: clusters) c.clearPoints();

        for (int i = 0; i < data.size(); ++i) {
            double []point = data.get(i);
            int minIdx = 0;
            double minDis = Calculate.EuclideanDistance(point, clusters.get(0).getCenter());

            for (int j = 1; j < k; ++j) {
                double d = Calculate.EuclideanDistance(point, clusters.get(j).getCenter());

                if (d < minDis) {
                    minDis = d;
                    minIdx = j;
                }
            }

            clusters.get(minIdx).addPoint(point, i);
        }
    }

    public void updateCentroid() {
        for (int i = 0; i < k; ++i) {
            double[] centroid = clusters.get(i).getCenter();
            List<double[]> points = clusters.get(i).getPoints();

            if (points.isEmpty()) continue;
            double[] newCentroid = new double[centroid.length];

            for (double[] point: points) {
                for (int j = 0; j < point.length; ++j) newCentroid[j] += point[j];
            }

            for (int j = 0; j < centroid.length; ++j) centroid[j] = newCentroid[j] / points.size();
        }
    }

    public void run() {
        initCentroid();

        for (int i = 0; i < MAX_ITERATION; ++i) {
            assignCluster();
            updateCentroid();
        }
    }

    public List<Cluster> getClusters() {
        return clusters;
    }
}
