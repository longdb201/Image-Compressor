package clustering_algorithm;

import utils.Calculate;

import java.util.*;

public class KMedoids {

    private int k;
    private final int MAX_ITERATION = 20;
    private final int MIN_SAMPLE_SIZE = 5000;
    private List<double[]> data;
    private List<Cluster> clusters;
    private double execTime;

    public KMedoids(int k, List<double[]> data) {
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

    public void initMedoids(List<double[]> dataSet) {
        Random random = new Random();
        Set<Integer> check = new HashSet<>();

        while (clusters.size() < k) {
            int t = random.nextInt(dataSet.size());
            if (check.add(t)) clusters.add(new Cluster(dataSet.get(t).clone()));
        }
    }

    public double assignCluster(List<double[]> dataSet) {
        for (Cluster c: clusters) c.clearPoints();

        double cost = 0;

        for (int i = 0; i < dataSet.size(); ++i) {
            int j = findNearestMedoidIdx(dataSet.get(i));
            cost += Calculate.ManhattanDistance(dataSet.get(i), clusters.get(j).getCenter());
            clusters.get(j).addPoint(dataSet.get(i), i);
        }

        return cost;
    }

    public double tryNewMedoid(int clusterIdx, double[] point, double cost) {
        List<Cluster> newClusters = new ArrayList<>();
        for (int i = 0; i < k; ++i) {
            if (i == clusterIdx) newClusters.add(new Cluster(point));
            else newClusters.add(new Cluster(clusters.get(i).getCenter()));
        }

        double newCost = 0.0;

        List<double[]> points = clusters.get(clusterIdx).getPoints();
        List<Integer> idx = clusters.get(clusterIdx).getIdxOfPoints();

        for (int i = 0; i < points.size(); ++i) {
            double[] pt = points.get(i);
            int minIdx = 0;
            double minDis = Calculate.ManhattanDistance(pt, newClusters.get(0).getCenter());

            for (int j = 1; j < k; ++j) {
                double d = Calculate.ManhattanDistance(newClusters.get(j).getCenter(), pt);

                if (d < minDis) {
                    minDis = d;
                    minIdx = j;
                }
            }
            newCost += minDis;
            newClusters.get(minIdx).addPoint(points.get(i), idx.get(i));
        }

        for (int i = 0; i < k; ++i) {
            if (i == clusterIdx) continue;

            double[] center = clusters.get(i).getCenter();
            points = clusters.get(i).getPoints();
            idx = clusters.get(i).getIdxOfPoints();

            for (int j = 0; j < points.size(); ++j) {
                double disToMedoid = Calculate.ManhattanDistance(center, points.get(j));
                double disToNewPoint = Calculate.ManhattanDistance(point, points.get(j));

                if (disToMedoid <= disToNewPoint) newClusters.get(i).addPoint(points.get(j), idx.get(j));
                else newClusters.get(clusterIdx).addPoint(points.get(j), idx.get(j));

                newCost += Math.min(disToMedoid, disToNewPoint);
            }
        }

        if (newCost < cost) {
            cost = newCost;
            clusters = newClusters;
        }
        return newCost;
    }

    public int findNearestMedoidIdx(double[] point) {
        int minIdx = 0;
        double minDis = Calculate.ManhattanDistance(point, clusters.get(0).getCenter());

        for (int i = 1; i < k; ++i) {
            double d = Calculate.ManhattanDistance(point, clusters.get(i).getCenter());

            if (d < minDis) {
                minDis = d;
                minIdx = i;
            }
        }

        return minIdx;
    }

    public void run() {
        long start = System.nanoTime();
        List<double[]> dataSet = (data.size() < MIN_SAMPLE_SIZE) ? data : createSamplePoints();

        initMedoids(dataSet);
        double cost = assignCluster(dataSet);

        for (int iter = 0; iter < MAX_ITERATION; ++iter) {
            boolean improved = false;

            for (int i = 0; i < k; ++i) {
                double[] center = clusters.get(i).getCenter();

                for (double[] point: clusters.get(i).getPoints()) {
                    if (Arrays.equals(center, point)) continue;
                    double newCost = tryNewMedoid(i, point, cost);
                    if (newCost < cost) {
                        cost = newCost;
                        improved = true;
                    }
                }
            }

            if (!improved) break;
        }

        if (data.size() > MIN_SAMPLE_SIZE) assignCluster(data);
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
