package clustering_algorithm;

import java.util.*;

public class DBScan {
    private List<double[]> data;
    private final double RADIUS = 7;
    private final int MIN_PTS = 4;
    private final int MIN_SAMPLE_SIZE = 5000;
    private int[] visited; // 0: Not visited, 1: Visited
    private int[] label; // 0: Noise, -1: Border, 1: Core
    private int[] assigned; // 0: Unassigned, 1: Assigned
    private List<Cluster> clusters;
    private List<List<Integer>> neighbor;
    private double execTime;

    public DBScan(List<double[]> data) {
        this.data = data;
        clusters = new ArrayList<>();
        visited = new int[MIN_SAMPLE_SIZE];
        label = new int[MIN_SAMPLE_SIZE];
        assigned = new int[MIN_SAMPLE_SIZE];
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

    public void findNeighborIdx(List<double[]> dataSet) {
        int n = dataSet.size();
        neighbor = new ArrayList<>();
        for (int i = 0; i < n; ++i)
            neighbor.add(new ArrayList<>());

        for (int i = 0; i < n; ++i) {
            double[] pt1 = dataSet.get(i);
            for (int j = i; j < n; ++j) {
                double[] pt2 = dataSet.get(j);
                if (Calculate.EuclideanDistance(pt1, pt2) <= RADIUS) {
                    neighbor.get(i).add(j);
                    neighbor.get(j).add(i);
                }
            }
        }
    }

    public void run() {
        long start = System.nanoTime();
        List<double[]> dataSet = (data.size() > MIN_SAMPLE_SIZE) ? createSamplePoints() : data;
        int n = dataSet.size();
        findNeighborIdx(dataSet);

        for (int i = 0; i < dataSet.size(); ++i) {
            if (visited[i] == 1)
                continue;
            visited[i] = 1;
            if (neighbor.get(i).size() < MIN_PTS)
                label[i] = 0;
            else {
                clusters.add(new Cluster(dataSet.get(i)));
                clusters.getLast().addPoint(dataSet.get(i), i);
                expandLastCluster(i, dataSet);
            }
        }

        if (data.size() > MIN_SAMPLE_SIZE)
            assignCluster();
        long end = System.nanoTime();
        execTime = ((end - start) / 1000000.0);
    }

    void expandLastCluster(int idx, List<double[]> dataSet) {
        Cluster cluster = clusters.getLast();

        for (int i = 0; i < neighbor.get(idx).size(); ++i) {
            int t = neighbor.get(idx).get(i);
            if (visited[t] == 0) {
                visited[t] = 1;
                List<Integer> tNeighbor = neighbor.get(t);

                if (tNeighbor.size() >= MIN_PTS) {
                    for (int j: tNeighbor)
                        neighbor.get(idx).add(j);
                }
            }
            if (assigned[t] == 0) {
                cluster.addPoint(dataSet.get(t), t);
                assigned[t] = 1;
            }
        }
    }

    public double getExecTime() {
        return execTime;
    }

    public List<Cluster> getClusters() {
        return clusters;
    }
}
