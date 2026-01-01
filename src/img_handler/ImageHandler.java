package img_handler;

import clustering_algorithm.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImageHandler {

    private String path;
    List<double[]> data;
    private int width;
    private int height;
    List<double[]> output;
    private String method;
    private final String[] METHOD_LIST = {"KMeans", "KMedoids", "MeanShift", "DBScan"};
    private int k = 20;
    private double radius = 7;
    private double bandwidth = 80;
    private double mergeThreshold = 60;
    private int numOfColors;
    private double execTime;
    private BufferedImage outputImg;

    public ImageHandler(String path) throws IOException {
        this.path = path;
        data = readImage();
        method = METHOD_LIST[0];
    }

    public List<double[]> readImage() throws IOException {
        List<double[]> dataSet = new ArrayList<>();
        BufferedImage img = ImageIO.read(new File(path));

        width = img.getWidth();
        height = img.getHeight();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int rgb = img.getRGB(x, y);

                int r = (rgb >> 16) & 255;
                int g = (rgb >> 8) & 255;
                int b = (rgb) & 255;

                dataSet.add(new double[]{r, g, b});
            }
        }

        return dataSet;
    }

    public void compressImage() {
        List<Cluster> clusters;

        if (method.equals("KMeans")) {
            KMeans kMeans = new KMeans(k, data);
            kMeans.run();
            clusters = kMeans.getClusters();
            execTime = kMeans.getExecTime();
        } else if (method.equals("KMedoids")) {
            KMedoids kMedoids = new KMedoids(k, data);
            kMedoids.run();
            clusters = kMedoids.getClusters();
            execTime = kMedoids.getExecTime();
        } else if (method.equals("MeanShift")) {
            MeanShift meanShift = new MeanShift(data);
            meanShift.setBandwidthAndMergeThreshold(bandwidth);
            meanShift.run();
            clusters = meanShift.getClusters();
            execTime = meanShift.getExecTime();
        } else if (method.equals("DBScan")) {
            DBScan dbScan = new DBScan(data);
            dbScan.setRadius(radius);
            dbScan.run();
            clusters = dbScan.getClusters();
            execTime = dbScan.getExecTime();
        } else clusters = new ArrayList<>();

        numOfColors = clusters.size();
        output = new ArrayList<>(Collections.nCopies(data.size(), new double[0]));
        for (Cluster cluster: clusters) {
            double[] center = cluster.getCenter();
            List<Integer> idx = cluster.getIdxOfPoints();
            for (int i = 0; i < idx.size(); ++i) output.set(idx.get(i), center);
        }
    }

    public void createImage() {
        outputImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < output.size(); ++i) {
            double[] rgb = output.get(i);
            int r = (int)rgb[0], g = (int)rgb[1], b = (int)rgb[2];
            int newRGB = (r << 16) | (g << 8) | b;
            outputImg.setRGB(i / height, i % height, newRGB);
        }
    }

    public void saveImage() throws IOException {
        int t = path.lastIndexOf('.');
        String format = path.substring(t + 1);
        String newPath = path.substring(0, t) + "_" + method + "." + format;

        ImageIO.write(outputImg, format, new File(newPath));
    }

    public double getExecTime() {
        return execTime;
    }

    public String getMethod() {
        return method;
    }

    public int getNumOfColors() {
        return numOfColors;
    }

    public BufferedImage getOutputImg() {
        return outputImg;
    }

    public void setPath(String path) throws IOException {
        this.path = path;
        this.data = readImage();
    }

    public void setK(int k) {
        this.k = k;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setBandwidth(double bandwidth) {
        this.bandwidth = bandwidth;
    }

    public void setMethod(int idx) {
        this.method = METHOD_LIST[idx % METHOD_LIST.length];
    }
}
