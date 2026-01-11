package ui;

import controller.Controller;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private HomePage homePage;
    private ImageComparePage imageComparePage;
    private Controller controller;

    public MainFrame() {
        super();
        controller = new Controller(this);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        homePage = new HomePage();
        imageComparePage = new ImageComparePage();

        setLayout(null);
        setSize(1200, 720);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Image compressor");
        setVisible(true);
        mainPanel.add(homePage, "Home");
        mainPanel.add(imageComparePage, "Compare");
        mainPanel.setBounds(0, 0, 1200, 700);

        add(mainPanel);
        cardLayout.show(mainPanel, "Home");
        addBtnAction();
    }

    public void addBtnAction() {
        homePage.getBrowseBtn().addActionListener(e -> controller.onBrowse());
        for (int i = 0; i < 4; ++i) {
            homePage.getMethodList()[i].addItemListener(e -> controller.onHomepageCheckbox());
            imageComparePage.getMethodList()[i].addItemListener(e -> controller.onCompareCheckBox());
        }
        homePage.getConfirmBtn().addActionListener(e -> controller.onConfirm());
        imageComparePage.getHeatmapBtn().addActionListener(e -> controller.onHeatmap());
        imageComparePage.getBackBtn().addActionListener(e -> controller.onBack());
        imageComparePage.getSaveBtn().addActionListener(e -> controller.onSave());
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public HomePage getHomePage() {
        return homePage;
    }

    public ImageComparePage getImageComparePage() {
        return imageComparePage;
    }
}
