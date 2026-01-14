package controller;

import clustering_algorithm.DBSCAN;
import clustering_algorithm.KMeans;
import clustering_algorithm.KMedoids;
import clustering_algorithm.MeanShift;
import ui.ImageChooser;
import ui.MainFrame;
import utils.Calculate;
import utils.ImgHandler;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Controller {
    public MainFrame frame;
    public ImageChooser fileChooser;
    public String path;

    public Controller(MainFrame frame) {
        this.frame  = frame;
        this.fileChooser = new ImageChooser();
    }

    public void onBrowse() {
        path = fileChooser.browse(frame);
        if (path == null) return;
        try {
            frame.getHomePage().getPreviewPanel().setImage(ImgHandler.ReadImage(path));
            frame.getHomePage().setEnabledBtnAndCheckbox(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Invalid path",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void onConfirm() {
        JOptionPane loadingPane = new JOptionPane(
                "Processing...",
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{},
                null
        );
        JDialog dialog = loadingPane.createDialog("Loading");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {}
        });

        dialog.setModal(true);

        new Thread(() -> {
            // Get compress methods
            JCheckBox[] methodList = frame.getHomePage().getMethodList();
            JTextField[] args = frame.getHomePage().getArgsList();

            List<Integer> data = ImgHandler.GetImageData(frame.getHomePage().getPreviewPanel().getImage());
            int w = frame.getHomePage().getPreviewPanel().getImage().getWidth();
            int h = frame.getHomePage().getPreviewPanel().getImage().getHeight();
            List<double[]> inpRGBData = ImgHandler.ExtractRGB(data), outRGBData = null;
            BufferedImage[] compressedImg = new BufferedImage[4], heatMapImg = new BufferedImage[4];
            String[] details = new String[4];

            for (int i = 0; i < 4; ++i) {
                if (methodList[i].isSelected()) {
                    double execTime = 0;
                    int colors = 0;
                    if (i == 0) {
                        int k;
                        try {
                            k = Integer.parseInt(args[0].getText());
                        } catch (NumberFormatException e) {
                            k = 20;
                        }

                        KMeans kMeans = new KMeans(k, inpRGBData);
                        kMeans.run();
                        outRGBData = kMeans.replace();
                        execTime = kMeans.getExecTime();
                        colors = kMeans.getClusters().size();
                    } else if (i == 1) {
                        int k;
                        try {
                            k = Integer.parseInt(args[1].getText());
                        } catch (NumberFormatException e) {
                            k = 20;
                        }

                        KMedoids kMedoids = new KMedoids(k, inpRGBData);
                        kMedoids.run();
                        outRGBData = kMedoids.replace();
                        execTime = kMedoids.getExecTime();
                        colors = kMedoids.getClusters().size();
                    } else if (i == 2) {
                        double bandwidth;
                        try {
                            bandwidth = Double.parseDouble(args[2].getText());
                        } catch (NumberFormatException e) {
                            bandwidth = 0;
                        }

                        MeanShift meanShift = new MeanShift(inpRGBData);
                        if (bandwidth != 0) meanShift.setBandwidthAndMergeThreshold(bandwidth);
                        meanShift.run();
                        outRGBData = meanShift.replace();
                        execTime = meanShift.getExecTime();
                        colors = meanShift.getClusters().size();
                    } else if (i == 3) {
                        double radius;
                        try {
                            radius = Double.parseDouble(args[3].getText());
                        } catch (NumberFormatException e) {
                            radius = 0;
                        }

                        DBSCAN dbscan = new DBSCAN(inpRGBData);
                        if (radius != 0) dbscan.setRadius(radius);
                        dbscan.run();
                        outRGBData = dbscan.replace();
                        execTime = dbscan.getExecTime();
                        colors = dbscan.getClusters().size();
                    }

                    BufferedImage tmp = ImgHandler.CreateImage(ImgHandler.CreateImageData(outRGBData), w, h);
                    List<List<Double>> ssim = Calculate.SSIM(inpRGBData, outRGBData, w, h);
                    BufferedImage heatMap = ImgHandler.RenderSSIMHeatmap(ssim);
                    double avgSSIM = Calculate.AverageSSIM(ssim);
                    double psnr = Calculate.PSNR(inpRGBData, outRGBData);
                    compressedImg[i] = tmp;
                    heatMapImg[i] = heatMap;
                    details[i] = "Colors: " + colors + ". "
                            + "PSNR: " + String.format("%.2f", psnr) + ". "
                            + "SSIM: " + String.format("%.2f", avgSSIM) + ". "
                            + "Execution time: " + String.format("%.3f", execTime) + "s.";
                } else {
                    compressedImg[i] = null;
                    heatMapImg[i] = null;
                    details[i] = null;
                }
            }
            frame.getImageComparePage().setImgCompare(compressedImg, heatMapImg, details);
            frame.getImageComparePage().setOriginal(frame.getHomePage().getPreviewPanel().getImage());
            for (int i = 0; i < 4; ++i) {
                frame.getImageComparePage().getMethodList()[i].setSelected(false);
            }
            frame.getImageComparePage().getSaveBtn().setEnabled(false);
            frame.getCardLayout().show(frame.getMainPanel(), "Compare");
            SwingUtilities.invokeLater(dialog::dispose);
        }).start();

        dialog.setVisible(true);
    }

    public void onHomepageCheckbox() {
        frame.getHomePage().setEnabledCheckbox();
    }

    public void onHeatmap() {
        frame.getImageComparePage().setHeatMapImg(frame.getImageComparePage().getHeatmapBtn().getText().equals("Image"));
    }

    public void onBack() {
        frame.getCardLayout().show(frame.getMainPanel(), "Home");
    }

    public void onCompareCheckBox() {
        frame.getImageComparePage().setEnabledSaveBtn();
    }

    public void onSave() {
        String directory = fileChooser.save(frame);
        if (directory == null) return;

        BufferedImage[] saveImg = new BufferedImage[4];
        for (int i = 0; i < 4; ++i) {
            if (frame.getImageComparePage().getMethodList()[i].isSelected()) {
                saveImg[i] = frame.getImageComparePage().getCompressedImg()[i];
            } else saveImg[i] = null;
        }

        String name = path.substring(path.lastIndexOf('\\'), path.lastIndexOf('.'));
        String format = path.substring(path.lastIndexOf('.'));

        for (int i = 0; i < 4; ++i) {
            if (saveImg[i] != null) {
                String method;
                if (i == 0) method = "_KMeans";
                else if (i == 1) method = "_KMedoids";
                else if (i == 2) method = "_MeanShift";
                else method = "_DBSCAN";

                String savePath = directory + name + method + format;
                if (!Files.exists(Paths.get(savePath))) {
                    try {
                        ImgHandler.SaveImage(saveImg[i], savePath, format.substring(1));
                        JOptionPane.showMessageDialog(
                                frame,
                                "Saved " + savePath,
                                "Saved",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(
                                frame,
                                "Can't save " + savePath,
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                } else {
                    int j = 1;
                    while (true) {
                        savePath = directory + name + method + "(" + j + ")" + format;
                        if (Files.exists(Paths.get(savePath))) ++j;
                        else {
                            try {
                                ImgHandler.SaveImage(saveImg[i], savePath, format.substring(1));
                                JOptionPane.showMessageDialog(
                                        frame,
                                        "Saved " + savePath,
                                        "Saved",
                                        JOptionPane.INFORMATION_MESSAGE
                                );
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog(
                                        frame,
                                        "Can't save " + savePath,
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            } finally {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
