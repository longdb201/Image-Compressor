package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePreview extends JPanel {
    private BufferedImage image;

    public ImagePreview(BufferedImage image) {
        super();
        this.image = image;
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        this.setBackground(Color.white);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image == null) return;

        int panelW = getWidth();
        int panelH = getHeight();

        int imgW = image.getWidth(this);
        int imgH = image.getHeight(this);

        double scale = Math.min(
                (double) panelW / imgW,
                (double) panelH / imgH
        );

        int drawW = (int) (imgW * scale);
        int drawH = (int) (imgH * scale);

        int x = (panelW - drawW) / 2;
        int y = (panelH - drawH) / 2;

        g.drawImage(image, x, y, drawW, drawH, this);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        if (image != null) repaint();
    }

}
