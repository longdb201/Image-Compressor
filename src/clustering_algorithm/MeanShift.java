package clustering_algorithm;

import java.util.*;

public class MeanShift {

    private List<double[]> data;
    private final int MIN_SAMPLE_SIZE = 5000;
    private final double BANDWIDTH = 80;
    private final double CONVERGING_THRESHOLD = 1;
    private final double MERGE_THRESHOLD = 60;
    private List<Cluster> clusters;
    private double execTime;

    public MeanShift(List<double[]> data) {
        this.data = data;
        clusters = new ArrayList<>();
    }

    public List<double[]> createSamplePoints() {
        ArrayList<double[]> sample = new ArrayList<>();
        Set<Integer> check = new HashSet<>();
        Random random = new Random();

        while (sample.size() < MIN_SAMPLE_SIZE) {
            int t = random.nextInt(data.size());
            if(check.add(t)) sample.add(data.get(t));
        }

        return sample;
    }

    public double[] move(double[] point, List<double[]> dataSet) {
        double[] res = new double[point.length];
        double[] tmp = new double[point.length];
        double sum = 0;

        for (double[] pt: dataSet) {
            if (Calculate.EuclideanDistance(point, pt) < BANDWIDTH) {
                for (int i = 0; i < point.length; ++i) tmp[i] = (point[i] - pt[i]) / BANDWIDTH;
                double k = Calculate.KernelEpanechikov(tmp);

                sum += k;
                for (int i = 0; i < point.length; ++i) res[i] += pt[i] * k;
            }
        }
        if (sum > 0) {
            for (int i = 0; i < point.length; ++i) res[i] /= sum;
            return res;
        } else return point;


    }

    public void merge(List<double[]> points) {
        clusters.add(new Cluster(points.get(0)));
        clusters.get(0).addPoint(points.get(0), 0);

        for (int i = 0; i < points.size(); ++i) {
            double[] point = points.get(i);
            boolean found = false;
            for (Cluster cluster: clusters) {
                double[] pt = cluster.getCenter();
                if (Calculate.EuclideanDistance(point, pt) < MERGE_THRESHOLD) {
                    int m = cluster.getPoints().size();
                    cluster.addPoint(point, i);
                    for (int j = 0; j < pt.length; ++j) pt[j] = (m * pt[j] + point[j]) / (m + 1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                clusters.add(new Cluster(point));
                clusters.getLast().addPoint(point, i);
            }
        }
    }

    public void assignCluster() {
        for (Cluster c: clusters) c.clearPoints();

        for (int i = 0; i < data.size(); ++i) {
            double []point = data.get(i);
            int minIdx = 0;
            double minDis = Calculate.EuclideanDistance(point, clusters.get(0).getCenter());

            for (int j = 1; j < clusters.size(); ++j) {
                double d = Calculate.EuclideanDistance(point, clusters.get(j).getCenter());

                if (d < minDis) {
                    minDis = d;
                    minIdx = j;
                }
            }

            clusters.get(minIdx).addPoint(point, i);
        }
    }

    public void run() {
        long start = System.nanoTime();
        List<double[]> dataSet = (data.size() <= MIN_SAMPLE_SIZE) ? data : createSamplePoints();

        List<double[]> mode = new ArrayList<>();

        // Move
        for (double[] point: dataSet) {
            double[] y = point, last;
            while (true) {
                last = y;
                y = move(last, dataSet);

                if (Calculate.EuclideanDistance(y, last) < CONVERGING_THRESHOLD) break;
            }
            mode.add(y);
        }

        // Merge
        merge(mode);
        if (data.size() > MIN_SAMPLE_SIZE) assignCluster();
        long end = System.nanoTime();
        execTime = ((end - start) / 1000000.0);
    }

    public double getExecTime() {
        return execTime;
    }

    public List<Cluster> getClusters() {
        return clusters;
    }
}
