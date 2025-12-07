package clustering_algorithm;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

    private double[] center;
    private List<double[]> points;
    private List<Integer> idxOfPoints;

    public Cluster(double[] center) {
        this.center = center.clone();
        points = new ArrayList<>();
        idxOfPoints = new ArrayList<>();
    }

    public Cluster() {
        this(new double[0]);
    }

    public void clearPoints() {
        points.clear();
        idxOfPoints.clear();
    }

    public void addPoint(double[] point, int idx) {
        points.add(point.clone());
        idxOfPoints.add(idx);
    }

    public double[] getCenter() {
        return center;
    }

    public void setCenter(double[] center) {
        this.center = center;
    }

    public List<double[]> getPoints() {
        return points;
    }

    public List<Integer> getIdxOfPoints() {
        return idxOfPoints;
    }
}
