package main;

import ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            String systemLaf = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(systemLaf);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new MainFrame();
    }
}
