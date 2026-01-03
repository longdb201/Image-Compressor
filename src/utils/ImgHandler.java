package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImgHandler {

    public static BufferedImage ReadImage(String path) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        return img;
    }

    public static List<Integer> GetImageData(BufferedImage img) {

        List<Integer> data = new ArrayList<>();

        int width = img.getWidth(), height = img.getHeight();

        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) data.add(img.getRGB(j, i));
        }

        return data;
    }

    public static List<double[]> ExtractRGB(List<Integer> data) {
        List<double[]> rgbData = new ArrayList<>();

        for (int i: data) {
            int r = (i >> 16) & 255;
            int g = (i >> 8) & 255;
            int b = i & 255;
            rgbData.add(new double[]{r, g, b});
        }

        return rgbData;
    }

    public static int CountColors(List<Integer> data) {
        Set<Integer> s = new HashSet<>();
        int cnt = 0;

        for (int i: data) {
            if (s.add(i)) ++cnt;
        }

        return cnt;
    }

    public static List<Integer> CreateImageData(List<double[]> rgbData) {
        List<Integer> data = new ArrayList<>();

        for (int i = 0; i < rgbData.size(); ++i) {
            int r = (int)rgbData.get(i)[0];
            int g = (int)rgbData.get(i)[1];
            int b = (int)rgbData.get(i)[2];

            int rgb = (r << 16) | (g << 8) | b;
            data.add(rgb);
        }

        return data;
    }

    public static BufferedImage CreateImage(List<Integer> data, int width, int height) {
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < data.size(); ++i) {
            int x = i % width, y = i / width;
            output.setRGB(x, y, data.get(i));
        }

        return output;
    }

    public static void SaveImage(BufferedImage output, String inpPath, String method) throws IOException{
        int t = inpPath.lastIndexOf('.');
        String format = inpPath.substring(t + 1);
        String outPath = inpPath.substring(0, t) + "_" + method +"." + format;

        ImageIO.write(output, format, new File(outPath));
    }
}
