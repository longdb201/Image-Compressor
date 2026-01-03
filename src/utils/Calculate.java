package utils;

import java.util.ArrayList;
import java.util.List;

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

    public static double MSE(List<double[]> data1, List<double[]> data2) {
        double sum = 0.0;

        for (int i = 0; i < data1.size(); ++i) {
            sum += Math.pow(EuclideanDistance(data1.get(i), data2.get(i)), 2);
        }

        return sum / (3 * data1.size());
    }

    public static double PSNR(List<double[]> data1, List<double[]> data2) {
        double mse = MSE(data1, data2);
        double max_i = 255;

        return 10 * Math.log10(max_i * max_i / mse);
    }

    public static List<Double> GrayScale(List<double[]> data) {
        List<Double> res = new ArrayList<>();

        for (double[] point: data) {
            double r = point[0], g = point[1], b = point[2];
            res.add(0.299 * r + 0.587 * g + 0.114 * b);
        }

        return res;
    }

    public static List<List<Double>> SSIM(List<double[]> data1, List<double[]> data2, int width, int height) {
        List<Double> scaled1 = GrayScale(data1), scaled2 = GrayScale(data2);

        List<Double> col_sum1 = new ArrayList<>();
        List<Double> col_sum2 = new ArrayList<>();
        List<Double> col_var1 = new ArrayList<>();
        List<Double> col_var2 = new ArrayList<>();
        List<Double> col_covar = new ArrayList<>();

        // Sliding window size
        int win_width = Math.min(11, width), win_height = Math.min(11, height);
        int n = win_width * win_height;

        // Prefix sum
        for (int i = 0; i < width; ++i) {
            col_sum1.add(0.0);
            col_sum2.add(0.0);
            col_var1.add(0.0);
            col_var2.add(0.0);
            col_covar.add(0.0);
            for (int j = 0; j < win_height; ++j) {
                int t = i + j * width;
                col_sum1.set(i, col_sum1.get(i) + scaled1.get(t));
                col_sum2.set(i, col_sum2.get(i) + scaled2.get(t));
                col_var1.set(i, Math.pow(scaled1.get(t), 2) / n + col_var1.get(i));
                col_var2.set(i, Math.pow(scaled2.get(t), 2) / n + col_var2.get(i));
                col_covar.set(i, (scaled1.get(t) * scaled2.get(t)) / n + col_covar.get(i));
            }
        }

        // Calculate SSIM in each window
        List<List<Double>> res = new ArrayList<>();
        double c1 = 0.0001 * 255 * 255, c2 = 0.0009 * 255 * 255;

        for (int y = 0; y < height - win_height + 1; ++y) {
            double sum1 = 0, sum2 = 0, var1 = 0, var2 = 0, covar = 0;
            res.add(new ArrayList<>());
            for (int i = 0; i < win_width; ++i) {
                sum1 += col_sum1.get(i);
                sum2 += col_sum2.get(i);
                var1 += col_var1.get(i);
                var2 += col_var2.get(i);
                covar += col_covar.get(i);
            }

            for (int x = 0; x < width - win_width + 1; ++x) {
                double a = (2 * sum1 * sum2 / (n * n) + c1)
                        / ((sum1 * sum1 + sum2 * sum2) / (n * n) + c1);
                double b = (2 * (covar - sum1 * sum2 / (n * n)) + c2)
                        / (var1 + var2 - (sum1 * sum1 + sum2 * sum2) / (n * n) + c2);

                res.getLast().add(a * b);
                int t = x + win_width;
                if (t < width) {
                    sum1 += (col_sum1.get(t) - col_sum1.get(x));
                    sum2 += (col_sum2.get(t) - col_sum2.get(x));
                    var1 += (col_var1.get(t) - col_var1.get(x));
                    var2 += (col_var2.get(t) - col_var2.get(x));
                    covar += (col_covar.get(t) - col_covar.get(x));
                }
            }
            int t = y + win_height;
            if (t == height) break;
            for (int i = 0; i < width; ++i) {
                col_sum1.set(i, col_sum1.get(i) - scaled1.get(i + y * width) + scaled1.get(i + t * width));
                col_sum2.set(i, col_sum2.get(i) - scaled2.get(i + y * width) + scaled2.get(i + t * width));
                col_var1.set(i, col_var1.get(i) - Math.pow(scaled1.get(i + y * width), 2) / n
                        + Math.pow(scaled1.get(i + t * width), 2) / n);
                col_var2.set(i, col_var2.get(i) - Math.pow(scaled2.get(i + y * width), 2) / n
                        + Math.pow(scaled2.get(i + t * width), 2) / n);
                col_covar.set(i, col_covar.get(i) - (scaled1.get(i + y * width) * scaled2.get(i + y * width)) / n
                        + (scaled1.get(i + t * width) * scaled2.get(i + t * width)) / n);
            }
        }

        return res;
    }

}
