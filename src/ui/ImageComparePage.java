package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageComparePage extends JPanel {
    private ImagePreview original;
    private ImagePreview[] compressed;
    private JCheckBox[] methodList;
    private JLabel[] details;
    private JButton backBtn;
    private JButton heatmapBtn;
    private JButton saveBtn;
    private BufferedImage[] compressedImg;
    private BufferedImage[] heatmapImg;

    public ImageComparePage() {
        super();
        setLayout(null);
        setBounds(0, 0, 1200, 700);

        Font font = new Font("Arial", Font.PLAIN, 16);

        original = new ImagePreview(null);
        int n = 4;
        compressed = new ImagePreview[n];
        details = new JLabel[n];
        methodList = new JCheckBox[n];
        methodList[0] = new JCheckBox("KMeans");
        methodList[1] = new JCheckBox("KMedoids");
        methodList[2] = new JCheckBox("Mean Shift");
        methodList[3] = new JCheckBox("DBSCAN");

        for (int i = 0; i < n; ++i) {
            compressed[i] = new ImagePreview(null);
            methodList[i].setFont(font);
            details[i] = new JLabel();
            compressed[i].setEnabled(false);
            methodList[i].setEnabled(false);
            details[i].setEnabled(false);
        }

        setImageLocation();

        int btnWidth = 120, btnHeight = 25, gap = 5;

        backBtn = new JButton("Back");
        backBtn.setFont(font);
        backBtn.setBounds(20, 635, btnWidth, btnHeight);

        saveBtn = new JButton("Save");
        saveBtn.setFont(font);
        saveBtn.setEnabled(false);
        saveBtn.setBounds(20 + gap + btnWidth, 635, btnWidth, btnHeight);

        heatmapBtn = new JButton("Heat map");
        heatmapBtn.setFont(font);
        heatmapBtn.setBounds(20 + 2 * (gap + btnWidth), 635, btnWidth, btnHeight);

        add(backBtn);
        add(saveBtn);
        add(heatmapBtn);
        add(original);
        for (int i = 0; i < n; ++i) {
            add(compressed[i]);
            add(methodList[i]);
            add(details[i]);
        }
    }

    public void setImageLocation() {
        original.setBounds(20, 20, 578, 605);
        Font font = new Font("Arial", Font.PLAIN, 9);

        int w = 287, h = 255;
        int[] x = {602, 893};
        int[] y = {20, 340};
        int k = 0;

        for (int i: x) {
            for (int j: y) {
                methodList[k].setBounds(i, j, 130, 25);
                compressed[k].setBounds(i, j + 30, w, h);
                details[k].setFont(font);
                details[k].setBounds(i, j + 35 + h, 287, 25);
                ++k;
            }
        }
    }

    public JButton getHeatmapBtn() {
        return heatmapBtn;
    }

    public JButton getBackBtn() {
        return backBtn;
    }

    public JButton getSaveBtn() {
        return saveBtn;
    }

    public JCheckBox[] getMethodList() {
        return methodList;
    }

    public BufferedImage[] getCompressedImg() {
        return compressedImg;
    }

    public void setHeatMapImg(boolean isImg) {
        for (int i = 0; i < methodList.length; ++i) {
            if (compressedImg[i] == null) continue;
            methodList[i].setSelected(false);
            compressed[i].setImage((isImg) ? compressedImg[i] : heatmapImg[i]);
            methodList[i].setEnabled(isImg);
        }
        saveBtn.setEnabled(false);
        heatmapBtn.setText((isImg) ? "Heat map" : "Image");
    }

    public void setEnabledSaveBtn() {
        saveBtn.setEnabled(false);
        if (heatmapBtn.getText().equals("Heat map")) {
            for (JCheckBox box: methodList) {
                if (box.isSelected()) saveBtn.setEnabled(true);
            }
        }
    }

    public void setOriginal(BufferedImage original) {
        this.original.setImage(original);
    }

    public void setImgCompare(BufferedImage[] compressedImg, BufferedImage[] heatmapImg, String[] detailsList) {
        this.compressedImg = compressedImg;
        this.heatmapImg = heatmapImg;
        int n = methodList.length;

        for (int i = 0; i < n; ++i) {
            if (compressedImg[i] == null) {
                compressed[i].setImage(null);
                details[i].setText("");
                compressed[i].setEnabled(false);
                methodList[i].setEnabled(false);
                methodList[i].setSelected(false);
                details[i].setEnabled(false);
                continue;
            }
            compressed[i].setImage(compressedImg[i]);
            compressed[i].setEnabled(true);
            details[i].setText(detailsList[i]);
            details[i].setEnabled(true);
            methodList[i].setEnabled(true);
            heatmapBtn.setText("Heat map");
        }
    }
}
