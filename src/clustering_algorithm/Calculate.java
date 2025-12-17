package clustering_algorithm;

public class Calculate {

    public static double EuclideanDistance(double []point1, double []point2) {
        double res = 0;

        for (int i = 0; i < point1.length; ++i) res += Math.pow(point1[i] - point2[i], 2);

        return Math.pow(res, 0.5);
    }

    public static double ManhattanDistance(double []point1, double []point2) {
        double res = 0;

        for (int i = 0; i < point1.length; ++i) res += Math.abs(point1[i] - point2[i]);

        return res;
    }

    public static double KernelEpanechikov(double[] point) {
        int d = point.length;
        double cd = 2 * Gamma(d) / (Math.pow(Math.PI, d * 0.5));

        double dis = EuclideanDistance(point, new double[d]);
        if (d > 1) return 0;
        else return 0.5 * cd * (1 - dis);
    }

    public static double Gamma(int d) {
        double res = 1;
        if (d % 2 == 0) {
            int t = d / 2;
            while (t >= 1) res *= (t--);
        } else {
            res = Math.pow(Math.PI, 0.5);
            int t = d;
            while (d >= 1) {
                res *= d * 0.5;
                d -= 2;
            }
        }

        return res;
    }
}
