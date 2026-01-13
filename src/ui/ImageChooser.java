package ui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageChooser extends JFileChooser {
    public ImageChooser() {
        super();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Images file (*.png, *.jpg, *.jpeg)",
            "png", "jpg", "jpeg"
        );
        setFileFilter(filter);
    }

    public String browse(JFrame frame) {
        setDialogTitle("Browse");
        setFileSelectionMode(JFileChooser.FILES_ONLY);
        setAcceptAllFileFilterUsed(false);

        int res = showOpenDialog(frame);
        if (res == JFileChooser.APPROVE_OPTION) {
            return getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    public String save(JFrame frame) {
        setDialogTitle("Save");
        setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int res = showSaveDialog(frame);
        if (res == JFileChooser.APPROVE_OPTION) {
            return getSelectedFile().getAbsolutePath();
        }

        return null;
    }
}
