package ui;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public class HomePage extends JPanel {
    private JButton browseBtn;
    private JButton confirmBtn;
    private JLabel methodLabel;
    private JCheckBox[] methodList;
    private JPanel btnPanel;
    private JLabel[] argsNameList;
    private JTextField[] argsList;
    private ImagePreview previewPanel;

    public HomePage() {
        super();
        setLayout(null);
        setBounds(0, 0, 1200, 700);

        btnPanel = new JPanel();
        btnPanel.setLayout(null);
        btnPanel.setBounds(10, 10, 170, 660);
        btnPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        btnPanel.setBackground(Color.lightGray);

        browseBtn = new JButton("Browse");
        browseBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        Dimension d1 = browseBtn.getPreferredSize();
        browseBtn.setBounds(20, 70, 130, d1.height);
        btnPanel.add(browseBtn);

        confirmBtn = new JButton("Confirm");
        confirmBtn.setFont(browseBtn.getFont());
        confirmBtn.setBounds(20, 80 + d1.height, 130, d1.height);
        confirmBtn.setEnabled(false);
        btnPanel.add(confirmBtn);

        methodLabel = new JLabel("Method");
        methodLabel.setFont(new Font("Arial", Font.BOLD, 20));
        Dimension d2 = methodLabel.getPreferredSize();
        methodLabel.setBounds(20, 140 + 2 * d1.height, d2.width, d2.height);
        btnPanel.add(methodLabel);

        methodList = new JCheckBox[4];
        methodList[0] = new JCheckBox("KMeans");
        methodList[1] = new JCheckBox("KMedoids");
        methodList[2] = new JCheckBox("Mean Shift");
        methodList[3] = new JCheckBox("DBSCAN");

        argsNameList = new JLabel[4];
        argsNameList[0] = new JLabel("k: ");
        argsNameList[1] = new JLabel("k: ");
        argsNameList[2] = new JLabel("Bandwidth: ");
        argsNameList[3] = new JLabel("Radius: ");

        argsList = new JTextField[4];

        for (int i = 0; i < methodList.length; ++i) {
            argsList[i] = new JTextField();
            methodList[i].setFont(new Font("Arial", Font.PLAIN, 20));
            argsNameList[i].setFont(new Font("Arial", Font.PLAIN, 20));
            int w = argsNameList[i].getPreferredSize().width, h = argsNameList[0].getPreferredSize().height;
            methodList[i].setBounds(20, 160 + 2 * d1.height + d2.height + i * (40 + d1.height), 130, d1.height);
            methodList[i].setOpaque(false);
            argsNameList[i].setBounds(20, 165 + 3 * d1.height + d2.height + i * (40 + d1.height), w, h);
            argsList[i].setBounds(25 + w, 165 + 3 * d1.height + d2.height + i * (40 + d1.height), 125 - w, h);
            argsList[i].setFont(new Font("Arial", Font.PLAIN, h - 14));
            btnPanel.add(methodList[i]);
            btnPanel.add(argsList[i]);
            btnPanel.add(argsNameList[i]);
        }

        previewPanel = new ImagePreview(null);
        previewPanel.setBounds(185, 10, 990, 660);
        previewPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        previewPanel.setBackground(Color.white);

        add(btnPanel);
        add(previewPanel);
        setEnabledBtnAndCheckbox(false);
    }

    public JButton getBrowseBtn() {
        return browseBtn;
    }

    public JButton getConfirmBtn() {
        return confirmBtn;
    }

    public JCheckBox[] getMethodList() {
        return methodList;
    }

    public JPanel getBtnPanel() {
        return btnPanel;
    }

    public JTextField[] getArgsList() {
        return argsList;
    }

    public ImagePreview getPreviewPanel() {
        return previewPanel;
    }

    public void setEnabledBtnAndCheckbox(boolean enabled) {
        methodLabel.setEnabled(enabled);

        for (int i = 0; i < 4; ++i) {
            methodList[i].setEnabled(enabled);
            methodList[i].setSelected(false);
            argsList[i].setEnabled(false);
            argsList[i].setText("");
            argsNameList[i].setEnabled(false);

        }
    }

    public void setEnabledCheckbox() {
        boolean canConfirm = false;
        for (int i = 0; i < 4; ++i) {
            boolean enabled = methodList[i].isSelected();
            argsNameList[i].setEnabled(enabled);
            argsList[i].setEnabled(enabled);
            canConfirm |= enabled;
        }
        confirmBtn.setEnabled(canConfirm);
    }
}
