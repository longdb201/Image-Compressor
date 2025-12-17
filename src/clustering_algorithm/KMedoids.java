package clustering_algorithm;

import java.util.*;

public class KMedoids {

    private int k;
    private final int MAX_ITERATION = 20;
//    private final int THRESHOLD = 50000;
    private final int MIN_SAMPLE_SIZE = 5000;
//    private final double SAMPLE_RATE = 0.01;
    private List<double[]> data;
    private List<Cluster> clusters;

    public KMedoids(int k, List<double[]> data) {
        this.k = k;

        this.data = data;
        clusters = new ArrayList<>();
    }

    public ArrayList<double[]> createSamplePoints(int sampleSize) {
        ArrayList<double[]> sample = new ArrayList<>();
        Set<Integer> check = new HashSet<>();
        Random random = new Random();

        while (sample.size() < sampleSize) {
            int t = random.nextInt(data.size());
            if(check.add(t)) sample.add(data.get(t));
        }

        return sample;
    }

    public ArrayList<double[]> createSamplePointsNoRandom(int sampleSize) {
        int n = data.size() / sampleSize;
        ArrayList<double[]> sample = new ArrayList<>();

        for (int i = 0; i < data.size(); i += n) {
            sample.add(data.get(i));
        }

        return sample;
    }

    public void initMedoids(List<double[]> dataSet) {
        Random random = new Random();
        Set<Integer> check = new HashSet<>();

        while (clusters.size() < k) {
            int t = random.nextInt(dataSet.size());
            if (check.add(t)) clusters.add(new Cluster(data.get(t).clone()));
        }

//        for (Cluster c: clusters) {
//            double[] point = c.getCenter();
//            for (int i = 0; i < point.length; ++i) {
//                System.out.print(point[i] + " ");
//            }
//            System.out.println();
//        }
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

    public boolean tryNewMedoid(int clusterIdx, double[] point, Double cost) {
        List<Cluster> newClusters = new ArrayList<>();
        for (int i = 0; i < k; ++i) {
            if (i == clusterIdx) newClusters.add(new Cluster(point));
            else newClusters.add(new Cluster(clusters.get(i).getCenter()));
        }

        Double newCost = 0.0;

        // Cập nhật cụm của các điểm thuộc cụm đang xét
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

        // Cập nhật cụm của các điểm thuôc các cụm còn lại
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
            return true;
        } else return false;
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
        List<double[]> dataSet = createSamplePointsNoRandom(Math.min(data.size(), MIN_SAMPLE_SIZE));
//        if (data.size() <= THRESHOLD) dataSet = data;
//        else {
//            int sampleSize = Math.max((int)(SAMPLE_RATE * data.size()), MIN_SAMPLE_SIZE);
//            dataSet = createSamplePoints(sampleSize);
//        }

        initMedoids(dataSet);
        Double cost = assignCluster(dataSet);

        for (int iter = 0; iter < MAX_ITERATION; ++iter) {
            boolean improved = false;

            for (int i = 0; i < k; ++i) {
                double[] center = clusters.get(i).getCenter();

                for (double[] point: clusters.get(i).getPoints()) {
                    if (point.equals(center)) continue;
                    improved |= tryNewMedoid(i, point, cost);
                }
            }

            if (!improved) break;
        }

        if (data.size() > MIN_SAMPLE_SIZE) assignCluster(data);
    }

    public List<Cluster> getClusters() {
        return clusters;
    }
}
