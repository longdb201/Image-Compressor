package clustering_algorithm;

import utils.Calculate;

import java.util.*;

public class KMeans {

    private int k;
    private final int MAX_ITERATION = 20;
    private final int MIN_SAMPLE_SIZE = 5000;
    private List<double[]> data;
    private List<Cluster> clusters;
    private double execTime;

    public KMeans(int k, List<double[]> data) {
        this.k = k;

        this.data = data;
        clusters = new ArrayList<>();
    }

    public ArrayList<double[]> createSamplePoints() {
        ArrayList<double[]> sample = new ArrayList<>();
        Set<Integer> check = new HashSet<>();
        Random random = new Random();

        while (sample.size() < MIN_SAMPLE_SIZE) {
            int t = random.nextInt(data.size());
            if(check.add(t)) sample.add(data.get(t));
        }

        return sample;
    }

    public void initCentroid(List<double[]> dataSet) {
        Random random = new Random();
        Set<Integer> check = new HashSet<>();

        while (clusters.size() < k) {
            int t = random.nextInt(dataSet.size());
            if (check.add(t)) clusters.add(new Cluster(dataSet.get(t).clone()));
        }
    }

    public void assignCluster(List<double[]> data) {
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
        long start = System.nanoTime();
        List<double[]> dataSet = (data.size() < MIN_SAMPLE_SIZE) ? data : createSamplePoints();
        initCentroid(dataSet);

        for (int i = 0; i < MAX_ITERATION; ++i) {
            assignCluster(dataSet);
            updateCentroid();
        }

        assignCluster(data);

        long end = System.nanoTime();
        execTime = ((end - start) / 1000000000.0);
    }

    public List<double[]> replace() {
        List<double[]> res = new ArrayList<>();

        for (int i = 0; i < data.size(); ++i) res.add(new double[]{});
        for (Cluster cluster: clusters) {
            double[] center = cluster.getCenter();
            for (int i = 0; i < center.length; ++i) center[i] = (int)center[i];
            List<double[]> point = cluster.getPoints();
            List<Integer> idx = cluster.getIdxOfPoints();

            for (int i = 0; i < point.size(); ++i) res.set(idx.get(i), center);
        }

        return res;
    }

    public double getExecTime() {
        return execTime;
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    public void setK(int k) {
        this.k = k;
    }

    public void setData(List<double[]> data) {
        this.data = data;

        clusters.clear();
    }
}
