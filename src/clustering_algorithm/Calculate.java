package clustering_algorithm;

public class Calculate {

    public static double EuclideanDistance(double []point1, double []point2) {
        double res = 0;

        for (int i = 0; i < point1.length; ++i) res += Math.pow(point1[i] - point2[i], 2);

        return Math.pow(res, 0.5);
    }


}
