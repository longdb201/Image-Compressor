package clustering_algorithm;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

    private double[] center;
    private List<double[]> points;

    public Cluster(double[] center) {
        this.center = center.clone();
        points = new ArrayList<>();
    }

    public Cluster() {
        this(new double[0]);
    }

    public void clearPoints() {
        points.clear();
    }

    public void addPoint(double[] point) {
        points.add(point.clone());
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

}
