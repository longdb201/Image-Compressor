package clustering_algorithm;

import java.util.*;

public class KMeans {

    private int k;
    private final int maxIterations = 20;
    private List<double[]> data;
    private List<Cluster> clusters;

    public KMeans(int k, ArrayList<double[]> data) {
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

        for (double[] point: data) {
            int minIdx = 0;
            double minDis = Calculate.EuclideanDistance(point, clusters.get(0).getCenter());

            for (int i = 1; i < k; ++i) {
                double d = Calculate.EuclideanDistance(point, clusters.get(i).getCenter());

                if (d < minDis) {
                    minDis = d;
                    minIdx = i;
                }
            }

            clusters.get(minIdx).addPoint(point);
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

        for (int i = 0; i < maxIterations; ++i) {
            assignCluster();
            updateCentroid();
        }
    }

    public List<Cluster> getClusters() {
        return clusters;
    }
}
